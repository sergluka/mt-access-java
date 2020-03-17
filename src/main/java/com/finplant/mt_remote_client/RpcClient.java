package com.finplant.mt_remote_client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.finplant.mt_remote_client.dto.common.Request;
import com.finplant.mt_remote_client.dto.common.Subscription;
import com.finplant.mt_remote_client.dto.jackson.BytesMapping;
import com.finplant.mt_remote_client.dto.jackson.LocalDateTimeMapping;
import com.finplant.mt_remote_client.dto.jackson.MonthMapping;
import com.finplant.mt_remote_client.dto.jackson.ZoneOffsetMapping;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RpcClient implements AutoCloseable {

    private static final int UNSUBSCRIBE_DELAY_S = 10;

    private final WsClient client;

    private final Map<Long, MonoProcessor<JsonNode>> requests = new ConcurrentHashMap<>();
    private final Map<String, EmitterProcessor<JsonNode>> subscriptions = new ConcurrentHashMap<>();

    private final ObjectMapper mapper = new ObjectMapper();
    private final AtomicLong idCounter = new AtomicLong(0);

    private Disposable messagesDisposable;

    public RpcClient(WsClient client) {
        this.client = client;
        initializeMapper();
    }

    @Override
    public void close() {
        messagesDisposable.dispose();
    }

    public <I, T> Mono<T> call(String method, I payload, Class<T> resultClass) {
        return doCall(method, payload).map(response -> parse(response, resultClass));
    }

    public <I, T> Mono<T> call(String method, I payload, TypeReference<T> typeReference) {
        return doCall(method, payload).map(response -> parse(response, typeReference));
    }

    public <T> Mono<T> call(String method, TypeReference<T> typeReference) {
        return doCall(method, null).map(response -> parse(response, typeReference));
    }

    public <T> Mono<T> call(String method, Class<T> resultClass) {
        return doCall(method, null).map(response -> parse(response, resultClass));
    }

    public <T> Mono<Void> call(String method, T payload) {
        return doCall(method, payload).then();
    }

    public Mono<Void> call(String method) {
        return doCall(method, null).then();
    }

    public <T> Flux<T> subscribe(String subscription, Class<T> eventClass) {
        return doSubscription(subscription).map(json -> parse(json, eventClass));
    }

    Flux<Boolean> connection(URI url, Map<String, String> headers) {

        return Flux.defer(() -> {
            if (client.isConnected()) {
                return Flux.error(new Errors.AlreadyConnectedError());
            }

            return client.connection(url, headers)
                         .doOnNext(unused -> listenForMessages())
                         .doFinally(signal -> cleanup());
        });
    }

    private void listenForMessages() {
        if (messagesDisposable != null) {
            messagesDisposable.dispose();
        }
        messagesDisposable = client.listen()
                                   .map(this::toJsonNode)
                                   .doOnError(this::logMessageError)
                                   .retry()
                                   .subscribe(this::onMessage, this::logMessageError);
    }

    private void cleanup() {

        log.trace("Cleanup");

        requests.values().forEach(sink -> sink.onError(new Errors.ConnectionIsDisposed()));
        requests.clear();
        subscriptions.values().forEach(sink -> sink.onError(new Errors.ConnectionIsDisposed()));
        subscriptions.clear();
    }

    private <T> Mono<JsonNode> doCall(String method, T payload) {

        long id = idCounter.getAndIncrement();

        return Mono.defer(() -> {

            MonoProcessor<JsonNode> emitter = MonoProcessor.create();
            requests.put(id, emitter);

            val json = makeJson(method, payload, id);
            return client.send(json).then(emitter);

        }).subscribeOn(Schedulers.newSingle("sender"))
                   .doFinally(signal -> requests.remove(id));
    }

    private Flux<JsonNode> doSubscription(String name) {

        return Flux.defer(() -> {

            val subscribeRequest = call("subscribe", new Subscription(name));

            EmitterProcessor<JsonNode> emitter = EmitterProcessor.create();
            val oldSink = subscriptions.putIfAbsent(name, emitter);
            if (oldSink != null) {
                return Flux.error(new Exception(String.format("Subscription to '%s' already exists", name)));
            }

            return subscribeRequest.thenMany(emitter)
                                   .doOnCancel(() -> unsubscribe(name))
                                   .doFinally(signal -> subscriptions.remove(name));
        });
    }

    private void unsubscribe(String name) {
        call("unsubscribe", new Subscription(name))
                .timeout(Duration.ofSeconds(UNSUBSCRIBE_DELAY_S))
                .subscribe();
    }

    @SneakyThrows
    private <T> T parse(JsonNode json, Class<T> resultClass) {
        return mapper.treeToValue(json, resultClass);
    }

    @SneakyThrows
    private <T> T parse(JsonNode json, TypeReference<T> typeReference) {
        val javaType = mapper.getTypeFactory().constructType(typeReference);
        return mapper.readValue(mapper.treeAsTokens(json), javaType);
    }

    @SneakyThrows
    private <T> String makeJson(String method, T payload, long id) {
        val request = new Request<>(method, id, payload);
        return mapper.writeValueAsString(request);
    }

    @SneakyThrows
    private void onMessage(JsonNode json) {

        if (handleEventIfExists(json)) {
            return;
        }

        handleResponseIfExists(json);
    }

    private boolean handleEventIfExists(JsonNode json) {
        val eventNode = json.get("event");
        if (eventNode == null) {
            return false;
        }

        val sink = subscriptions.get(eventNode.asText());
        if (sink == null) {
            throw new IllegalStateException(String.format("Got unsubscribed event: %s", json));
        }

        val data = json.get("data");
        sink.onNext(data);

        return true;
    }

    private void handleResponseIfExists(JsonNode json) {
        JsonNode idNode = json.get("id");
        if (idNode == null) {
            throw new IllegalArgumentException("Message has no mandatory field 'id'");
        }

        val id = idNode.asLong();
        val request = requests.get(id);
        if (request == null) {
            throw new IllegalArgumentException(String.format("Unexpected response: %s", json));
        }

        val status = json.get("status").asText();
        if (!status.equals("OK")) {

            val message = json.get("message");
            if (message == null) {
                request.onError(new Errors.MtmError("Missing 'message' in error response"));
                return;
            }

            request.onError(new Errors.MtmError(message.asText()));
            return;
        }

        val result = json.get("result");
        if (result == null) {
            request.onComplete();
            return;
        }

        request.onNext(result);
        request.onComplete();
    }

    @SneakyThrows
    private JsonNode toJsonNode(String message) {
        return mapper.readTree(message);
    }

    private void initializeMapper() {
        mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Month.class, new MonthMapping.Serialize());
        simpleModule.addDeserializer(Month.class, new MonthMapping.Deserializer());
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeMapping.Serialize());
        simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeMapping.Deserializer());
        simpleModule.addSerializer(ZoneOffset.class, new ZoneOffsetMapping.Serialize());
        simpleModule.addDeserializer(ZoneOffset.class, new ZoneOffsetMapping.Deserializer());
        simpleModule.addSerializer(byte[].class, new BytesMapping.Serialize());
        simpleModule.addDeserializer(byte[].class, new BytesMapping.Deserializer());
        mapper.registerModule(simpleModule);
    }

    private void logMessageError(Throwable throwable) {
        log.error("Error handling message", throwable);
    }
}

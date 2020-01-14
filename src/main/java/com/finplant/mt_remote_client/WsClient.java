package com.finplant.mt_remote_client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import reactor.core.publisher.*;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@Slf4j
class WsClient {

    private static final int BUFFER_SIZE = 50 * 1024 * 1024;

    private static final int IDLE_TIMEOUT_MS = 300_000;
    private static final int WRITE_TIMEOUT_MS = 10_000;

    private final WebSocketClient client = new WebSocketClient();
    private final WsClientImpl implementation = new WsClientImpl();

    WsClient() {
        client.getPolicy().setMaxBinaryMessageBufferSize(BUFFER_SIZE);
        client.getPolicy().setMaxBinaryMessageSize(BUFFER_SIZE);
        client.getPolicy().setMaxTextMessageBufferSize(BUFFER_SIZE);
        client.getPolicy().setMaxTextMessageSize(BUFFER_SIZE);
        client.getPolicy().setIdleTimeout(IDLE_TIMEOUT_MS);
        client.getPolicy().setAsyncWriteTimeout(WRITE_TIMEOUT_MS);
    }

    Flux<Boolean> connectionStatus() {
        return implementation.getConnectionProcessor();
    }

    Mono<Void> connect(URI uri) {
        return doConnect(uri)
                .onErrorMap(Errors.ConnectionError::new)
                .then();
    }

    Mono<Void> disconnect() {
        return implementation.disconnect();
    }

    Flux<String> listen() {
        return implementation.getMessageProcessor();
    }

    Mono<Void> send(String message) {
        return implementation.send(message);
    }

    Flux<Boolean> connection() {
        return implementation.getConnectionProcessor();
    }

    @SneakyThrows
    private Mono<Session> doConnect(URI uri) {
        client.start();

        ClientUpgradeRequest request = new ClientUpgradeRequest();

        CompletableFuture<Session> future = (CompletableFuture<Session>) client.connect(implementation, uri, request);
        return Mono.fromFuture(future).doFinally(signal -> {
            if (signal == SignalType.AFTER_TERMINATE) {
                future.cancel(true);
            }
        });
    }
}

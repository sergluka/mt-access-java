package com.finplant.mtm_client;

import com.finplant.mtm_client.dto.internal.Registration;
import com.finplant.mtm_client.requests.ProtocolExtensions;
import com.finplant.mtm_client.requests.RequestsConfig;
import com.finplant.mtm_client.requests.Users;
import lombok.val;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;
import reactor.util.annotation.NonNull;

import java.net.URI;
import java.time.Duration;

public class MtmClient implements AutoCloseable {

    private final ReplayProcessor<Boolean> connectSubject = ReplayProcessor.create();
    private final Disposable.Composite disposables = Disposables.composite();

    private final RequestsConfig requestsConfig;
    private final ProtocolExtensions protocolExtensions;

    private final RpcClient client;
    private final Users users;

    public MtmClient() {
        client = new RpcClient(new WsClient());
        requestsConfig = new RequestsConfig(client);
        protocolExtensions = new ProtocolExtensions(client);
        users = new Users(client);
    }

    @Override
    public void close() {
        disposables.dispose();
    }

    public Mono<Void> connect(URI uri) {
        return client.connect(uri);
    }

    public Flux<Boolean> connection() {
        return client.connection();
    }

    public Mono<Void> disconnect() {
        return client.disconnect();
    }

    public Mono<Void> connectToMt(@NonNull String server, @NonNull Integer login, @NonNull String password,
                                  @NonNull Duration reconnectDelay) {

        val params = new Registration(server, login, password, reconnectDelay);
        return client.request("connect", params);
    }

    public Mono<Void> disconnectFromMt() {
        return client.request("disconnect");
    }

    public Flux<Boolean> connectionStatus() {
        return client.subscribe("connection", Boolean.class);
    }

    public RequestsConfig config() {
        return requestsConfig;
    }

    public Users users() {
        return users;
    }

    public ProtocolExtensions protocolExtensions() {
        return protocolExtensions;
    }
}

package com.finplant.mtm_client;

import java.net.URI;
import java.time.Duration;

import com.finplant.mtm_client.dto.internal.Registration;
import com.finplant.mtm_client.procedures.ConfigProcedures;
import com.finplant.mtm_client.procedures.MarketProcedures;
import com.finplant.mtm_client.procedures.ProtocolExtensionsProcedures;
import com.finplant.mtm_client.procedures.SymbolProcedures;
import com.finplant.mtm_client.procedures.UserProcedures;

import lombok.val;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

public class MtmClient implements AutoCloseable {

    private final Disposable.Composite disposables = Disposables.composite();

    private final RpcClient client;
    private final ConfigProcedures configProcedures;
    private final ProtocolExtensionsProcedures protocolExtensionsProcedures;
    private final UserProcedures userProcedures;
    private final SymbolProcedures symbolProcedures;
    private final MarketProcedures marketProcedures;

    public MtmClient() {
        client = new RpcClient(new WsClient());
        configProcedures = new ConfigProcedures(client);
        protocolExtensionsProcedures = new ProtocolExtensionsProcedures(client);
        userProcedures = new UserProcedures(client);
        symbolProcedures = new SymbolProcedures(client);
        marketProcedures = new MarketProcedures(client);
    }

    @Override
    public void close() {
        disposables.dispose();
    }

    public Mono<Void> connect(URI uri) {
        return client.connect(uri);
    }

    public Flux<Boolean> localConnection() {
        return client.connection();
    }

    public Flux<Boolean> remoteConnection() {
        return client.subscribe("connection", Boolean.class);
    }

    public Mono<Void> disconnect() {
        return client.disconnect();
    }

    public Mono<Void> connectToMt(@NonNull String server, @NonNull Integer login, @NonNull String password,
                                  @NonNull Duration reconnectDelay) {

        val params = new Registration(server, login, password, reconnectDelay);
        return client.call("connect", params);
    }

    public Mono<Void> disconnectFromMt() {
        return client.call("disconnect");
    }

    public ConfigProcedures config() {
        return configProcedures;
    }

    public UserProcedures user() {
        return userProcedures;
    }

    public SymbolProcedures symbol() {
        return symbolProcedures;
    }

    public MarketProcedures market() {
        return marketProcedures;
    }

    public ProtocolExtensionsProcedures protocolExtensions() {
        return protocolExtensionsProcedures;
    }
}

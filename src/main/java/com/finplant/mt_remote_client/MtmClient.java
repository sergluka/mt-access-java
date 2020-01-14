package com.finplant.mt_remote_client;

import java.net.URI;
import java.time.Duration;

import com.finplant.mt_remote_client.dto.internal.Registration;
import com.finplant.mt_remote_client.procedures.ConfigProcedures;
import com.finplant.mt_remote_client.procedures.DealingProcedures;
import com.finplant.mt_remote_client.procedures.MarketProcedures;
import com.finplant.mt_remote_client.procedures.OrderProcedures;
import com.finplant.mt_remote_client.procedures.ProtocolExtensionsProcedures;
import com.finplant.mt_remote_client.procedures.SymbolsProcedures;
import com.finplant.mt_remote_client.procedures.UsersProcedures;

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
    private final UsersProcedures userProcedures;
    private final SymbolsProcedures symbolProcedures;
    private final MarketProcedures marketProcedures;
    private final DealingProcedures dealingProcedures;

    private final OrderProcedures orderProcedure;

    public MtmClient() {
        client = new RpcClient(new WsClient());
        configProcedures = new ConfigProcedures(client);
        protocolExtensionsProcedures = new ProtocolExtensionsProcedures(client);
        userProcedures = new UsersProcedures(client);
        symbolProcedures = new SymbolsProcedures(client);
        marketProcedures = new MarketProcedures(client);
        orderProcedure = new OrderProcedures(client);
        dealingProcedures = new DealingProcedures(client);
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

    public UsersProcedures users() {
        return userProcedures;
    }

    public SymbolsProcedures symbols() {
        return symbolProcedures;
    }

    public MarketProcedures market() {
        return marketProcedures;
    }

    public OrderProcedures orders() {
        return orderProcedure;
    }

    public DealingProcedures dealing() {
        return dealingProcedures;
    }

    public ProtocolExtensionsProcedures protocolExtensions() {
        return protocolExtensionsProcedures;
    }
}

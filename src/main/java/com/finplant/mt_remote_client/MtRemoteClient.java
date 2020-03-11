package com.finplant.mt_remote_client;

import com.finplant.mt_remote_client.procedures.*;
import lombok.Builder;
import lombok.Value;
import lombok.val;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;

public class MtRemoteClient {

    private static final String HEADER_MT_SERVER = "MT-SERVER";
    private static final String HEADER_MT_LOGIN = "MT-LOGIN";
    private static final String HEADER_MT_PASSWORD = "MT-PASSWORD";

    private final RpcClient client;
    private final ConfigProcedures configProcedures;
    private final ProtocolExtensionsProcedures protocolExtensionsProcedures;
    private final UsersProcedures userProcedures;
    private final SymbolsProcedures symbolProcedures;
    private final MarketProcedures marketProcedures;
    private final DealingProcedures dealingProcedures;

    private final OrderProcedures orderProcedure;
    private final ConnectionParameters parameters;

    private MtRemoteClient(WsClient wsClient, ConnectionParameters params) {
        this.parameters = params;

        client = new RpcClient(wsClient);
        configProcedures = new ConfigProcedures(client);
        protocolExtensionsProcedures = new ProtocolExtensionsProcedures(client);
        userProcedures = new UsersProcedures(client);
        symbolProcedures = new SymbolsProcedures(client);
        marketProcedures = new MarketProcedures(client);
        orderProcedure = new OrderProcedures(client);
        dealingProcedures = new DealingProcedures(client);
    }

    @SuppressWarnings("unused")
    public static MtRemoteClient create(ConnectionParameters parameters) {
        return new MtRemoteClient(new WsClient(), parameters);
    }

    @SuppressWarnings("unused")
    public static MtRemoteClient createSecure(ConnectionParameters parameters,
                                              InputStream keystoreStream, String keystorePassword,
                                              boolean hostnameVerification) {
        return new MtRemoteClient(new WsClient(keystoreStream, keystorePassword, hostnameVerification), parameters);
    }

    public Flux<Boolean> connection() {
        val headers = new HashMap<String, String>();
        headers.put(HEADER_MT_SERVER, parameters.getServer());
        headers.put(HEADER_MT_LOGIN, parameters.getLogin().toString());
        headers.put(HEADER_MT_PASSWORD, parameters.getPassword());

        return client.connection(parameters.uri, headers);
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

    @Value
    @Builder
    public static class ConnectionParameters {
        private final URI uri;
        private final String server;
        private final Integer login;
        private final String password;
    }
}

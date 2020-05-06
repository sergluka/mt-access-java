package lv.sergluka.mt_access;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;

import lv.sergluka.mt_access.procedures.ConfigProcedures;
import lv.sergluka.mt_access.procedures.DealingProcedures;
import lv.sergluka.mt_access.procedures.MarketProcedures;
import lv.sergluka.mt_access.procedures.OrderProcedures;
import lv.sergluka.mt_access.procedures.ProtocolExtensionsProcedures;
import lv.sergluka.mt_access.procedures.SymbolsProcedures;
import lv.sergluka.mt_access.procedures.UsersProcedures;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class MtAccessClient implements AutoCloseable {

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

    @SuppressWarnings("unused")
    public static MtAccessClient create(@NonNull ConnectionParameters parameters,
                                        @NonNull InputStream keystoreStream,
                                        @NonNull String keystorePassword) {
        return new MtAccessClient(new WsClient(keystoreStream, keystorePassword), parameters);
    }

    public Flux<MtAccessClient> connection() {
        return Flux.defer(() -> {

            log.info("Initialize");

            val headers = new HashMap<String, String>();
            headers.put(HEADER_MT_SERVER, parameters.getServer());
            headers.put(HEADER_MT_LOGIN, parameters.getLogin().toString());
            headers.put(HEADER_MT_PASSWORD, parameters.getPassword());

            log.info("Connecting to {} as {} via {}", parameters.getServer(), parameters.getLogin(), parameters.uri);
            return client.connection(parameters.uri, headers).map(unused -> this);
        });
    }

    @Override
    public void close() throws Exception {
        client.close();
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

    private MtAccessClient(WsClient wsClient, ConnectionParameters params) {
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

    @Data
    @Builder
    public static class ConnectionParameters {
        @NonNull
        private final URI uri;

        @NonNull
        private final String server;

        @NonNull
        private final Integer login;

        @NonNull
        private final String password;
    }
}

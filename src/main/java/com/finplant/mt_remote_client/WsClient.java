package com.finplant.mt_remote_client;

import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

@Slf4j
class WsClient {

    private static final int BUFFER_SIZE = 50 * 1024 * 1024;

    private static final int IDLE_TIMEOUT_MS = 300_000;
    private static final int WRITE_TIMEOUT_MS = 10_000;

    private final WebSocketClient client;

    private final WsClientImpl implementation = new WsClientImpl();

    @SneakyThrows
    WsClient(InputStream keystoreStream, String keystorePassword, boolean hostnameVerification) {

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

        try (val stream = keystoreStream) {
            keystore.load(stream, keystorePassword.toCharArray());
        }
        KeyManagerFactory keyManagerFactory =
              KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, keystorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory =
              TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keystore);

        SslContextFactory ssl = new SslContextFactory.Client(true);
        SSLContext sslContext = SSLContext.getInstance("TLS");

        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        ssl.setSslContext(sslContext);
        ssl.setTrustAll(false);

        if (hostnameVerification) {
            ssl.setEndpointIdentificationAlgorithm("HTTPS");
        }

        client = new WebSocketClient(ssl);

        client.getPolicy().setMaxBinaryMessageBufferSize(BUFFER_SIZE);
        client.getPolicy().setMaxBinaryMessageSize(BUFFER_SIZE);
        client.getPolicy().setMaxTextMessageBufferSize(BUFFER_SIZE);
        client.getPolicy().setMaxTextMessageSize(BUFFER_SIZE);
        client.getPolicy().setIdleTimeout(IDLE_TIMEOUT_MS);
        client.getPolicy().setAsyncWriteTimeout(WRITE_TIMEOUT_MS);
    }

    WsClient() {
        client = new WebSocketClient();

        client.getPolicy().setMaxBinaryMessageBufferSize(BUFFER_SIZE);
        client.getPolicy().setMaxBinaryMessageSize(BUFFER_SIZE);
        client.getPolicy().setMaxTextMessageBufferSize(BUFFER_SIZE);
        client.getPolicy().setMaxTextMessageSize(BUFFER_SIZE);
        client.getPolicy().setIdleTimeout(IDLE_TIMEOUT_MS);
        client.getPolicy().setAsyncWriteTimeout(WRITE_TIMEOUT_MS);
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
}

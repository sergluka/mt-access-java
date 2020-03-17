package com.finplant.mt_remote_client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Objects;

@Slf4j
class WsClient {

    private static final int BUFFER_SIZE = 50 * 1024 * 1024;
    private static final int IDLE_TIMEOUT_MS = 300_000;
    private static final int WRITE_TIMEOUT_MS = 10_000;

    private final WebSocketClient client;

    private WsClientImpl implementation;

    WsClient(InputStream keystoreStream, String keystorePassword, boolean hostnameVerification) {
        SslContextFactory ssl = createSslContextFactory(keystoreStream, keystorePassword, hostnameVerification);
        client = new WebSocketClient(ssl);
        setupClient();
    }

    WsClient() {
        client = new WebSocketClient();
        setupClient();
    }

    Flux<Boolean> connection(URI uri, Map<String, String> headers) {

        return Flux.create(sink -> {
            implementation = new WsClientImpl() {

                @Override
                protected void onConnected() {
                    sink.next(true);
                }

                @Override
                protected void onDisconnected() {
                    sink.complete();
                }

                @Override
                protected void onConnectionLost(Throwable cause) {
                    sink.error(cause);
                }
            };
            sink.onDispose(() -> {
                implementation.disconnect();
                implementation = null;
            });

            doConnect(uri, headers);
        });
    }

    Flux<String> listen() {
        return getImplementation().messages();
    }

    Mono<Void> send(String message) {
        return getImplementation().send(message);
    }

    boolean isConnected() {
        return implementation != null;
    }

    @SneakyThrows
    private SslContextFactory createSslContextFactory(InputStream keystoreStream, String keystorePassword,
                                                      boolean hostnameVerification) {

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
        return ssl;
    }

    @SneakyThrows
    private void doConnect(URI uri, Map<String, String> headers) {
        client.start();

        ClientUpgradeRequest request = new ClientUpgradeRequest();
        headers.forEach(request::setHeader);

        client.connect(getImplementation(), uri, request);
    }

    private void setupClient() {
        client.getPolicy().setMaxBinaryMessageBufferSize(BUFFER_SIZE);
        client.getPolicy().setMaxBinaryMessageSize(BUFFER_SIZE);
        client.getPolicy().setMaxTextMessageBufferSize(BUFFER_SIZE);
        client.getPolicy().setMaxTextMessageSize(BUFFER_SIZE);
        client.getPolicy().setIdleTimeout(IDLE_TIMEOUT_MS);
        client.getPolicy().setAsyncWriteTimeout(WRITE_TIMEOUT_MS);
    }

    private WsClientImpl getImplementation() {
        Objects.requireNonNull(implementation, "Not connected");
        return implementation;
    }
}

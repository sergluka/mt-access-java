package com.finplant.mt_remote;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import reactor.core.publisher.Flux;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.util.Map;
import java.util.Objects;

@Slf4j
class WsClient implements AutoCloseable {

    private final SSLSocketFactory sslSockerFactory;

    private WsClientImpl implementation;

    WsClient(InputStream keystoreStream, String keystorePassword, boolean hostnameVerification) {
        sslSockerFactory = createSslContextFactory(keystoreStream, keystorePassword, hostnameVerification);
    }

    WsClient() {
        sslSockerFactory = null;
    }

    @Override
    @SneakyThrows
    public void close() {
        if (implementation != null) {
            implementation.closeBlocking();
        }
    }

    @SneakyThrows
    private SSLSocketFactory createSslContextFactory(InputStream keystoreStream, String keystorePassword,
                                                     boolean hostnameVerification) {

        val keystore = KeyStore.getInstance(KeyStore.getDefaultType());

        try (val stream = keystoreStream) {
            keystore.load(stream, keystorePassword.toCharArray());
        }
        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, keystorePassword.toCharArray());

        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keystore);

        val sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }

    private WsClientImpl getImplementation() {
        Objects.requireNonNull(implementation, "Not connected");
        return implementation;
    }

    Flux<Boolean> connection(URI uri, Map<String, String> headers) {

        return Flux.create(sink -> {

            implementation = new WsClientImpl(uri, headers) {

                @Override
                protected void onConnectionError(Throwable cause) {
                    sink.error(cause);
                }

                @Override
                protected void onConnected() {
                    sink.next(true);
                }

                @Override
                protected void onDisconnected() {
                    sink.complete();
                }
            };
            sink.onDispose(this::stopClient);

            implementation.setSocketFactory(sslSockerFactory);
            implementation.connect();

        });
    }

    @SneakyThrows
    private void stopClient() {
        implementation.close();
    }

    Flux<String> listen() {
        return getImplementation().messages();
    }

    void send(String message) {
        getImplementation().send(message);
    }

    boolean isConnected() {
        return implementation != null && implementation.isOpen();
    }
}

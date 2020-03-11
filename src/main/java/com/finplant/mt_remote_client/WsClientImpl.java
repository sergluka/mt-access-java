package com.finplant.mt_remote_client;

import java.nio.ByteBuffer;

import org.eclipse.jetty.websocket.api.CloseStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.WebSocketPingPongListener;
import org.eclipse.jetty.websocket.api.WriteCallback;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.*;

@Slf4j
public abstract class WsClientImpl implements WebSocketListener, WebSocketPingPongListener {

    private final DirectProcessor<String> messageProcessor = DirectProcessor.create();

    private Session session;

    protected abstract void onConnectionLost(Throwable cause);
    protected abstract void onConnected();
    protected abstract void onDisconnected();

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        log.warn("Got unexpected binary data");
    }

    @Override
    public void onWebSocketText(String message) {
        log.trace("=> {}", message);
        messageProcessor.onNext(message);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {

        try {
            log.info("Connection is closed. code={}, reason={}", statusCode, reason);

            if (statusCode != StatusCode.NORMAL) {
                onConnectionLost(new Errors.ConnectionLostError(statusCode, reason));
            }
            onDisconnected();
        } catch (Exception e) {
            log.error("Fail to handle connection closing", e);
        }

        session = null;
    }

    @Override
    public void onWebSocketConnect(Session newSession) {
        log.info("Connected");
        this.session = newSession;
        onConnected();
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        onConnectionLost(new Errors.ConnectionError(cause));
    }

    @Override
    public void onWebSocketPing(ByteBuffer payload) {
        log.trace("PING");
    }

    @Override
    public void onWebSocketPong(ByteBuffer payload) {
        log.trace("PONG");
    }

    public DirectProcessor<String> messages() {
        return messageProcessor;
    }

    public void disconnect() {
        if (session != null) {
            session.close(new CloseStatus(StatusCode.NORMAL, "Client is disconnected"));
        }
    }

    public Mono<Void> send(String message) {

        return Mono.create(sink -> {
            log.trace("<=: {}", message);

            if (session == null) {
                sink.error(new IllegalStateException("Not connected"));
                return;
            }

            session.getRemote().sendString(message, new WriteCallback() {

                @Override
                public void writeFailed(Throwable cause) {
                    sink.error(cause);
                }

                @Override
                public void writeSuccess() {
                    sink.success();
                }
            });
        });
    }
}

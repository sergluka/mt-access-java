package com.finplant.mt_remote_client;

import java.nio.ByteBuffer;

import org.eclipse.jetty.websocket.api.CloseStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.WebSocketPingPongListener;
import org.eclipse.jetty.websocket.api.WriteCallback;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.core.publisher.ReplayProcessor;

@Slf4j
public class WsClientImpl implements WebSocketListener, WebSocketPingPongListener {

    private final ReplayProcessor<Boolean> connectionProcessor = ReplayProcessor.cacheLastOrDefault(false);
    private final DirectProcessor<String> messageProcessor = DirectProcessor.create();

    private Session session;
    private MonoSink<Void> disconnectSink;

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

        log.info("Connection is closed. code={}, reason={}", statusCode, reason);

        if (statusCode != StatusCode.NORMAL && statusCode != StatusCode.NO_CLOSE) {
            log.warn("Connection closed because of error");
        }

        if (disconnectSink != null) {
            disconnectSink.success();
        }
        connectionProcessor.onNext(false);

        session = null;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        log.info("Connected");
        this.session = session;
        connectionProcessor.onNext(true);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        log.error("Websocket error", cause);
    }

    @Override
    public void onWebSocketPing(ByteBuffer payload) {
        log.trace("PING");
    }

    @Override
    public void onWebSocketPong(ByteBuffer payload) {
        log.trace("PONG");
    }

    public ReplayProcessor<Boolean> getConnectionProcessor() {
        return connectionProcessor;
    }

    public DirectProcessor<String> getMessageProcessor() {
        return messageProcessor;
    }

    public Mono<Void> disconnect() {

        return Mono.create(sink -> {
            disconnectSink = sink;

            if (session == null) {
                disconnectSink.success();
                return;
            }
            session.close(new CloseStatus(StatusCode.NORMAL, "Client disconnected"));
        });
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

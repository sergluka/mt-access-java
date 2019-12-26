package com.finplant.mtm_client;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.*;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.core.publisher.ReplayProcessor;

@Slf4j
public class WsClientImpl implements WebSocketListener {

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
        log.debug("=> {}", message);
        messageProcessor.onNext(message);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {

        log.info("Connection is closed. code={}, reason={}", statusCode, reason);
        connectionProcessor.onNext(false);

        if (disconnectSink != null) {
            disconnectSink.success();
        }

        if (statusCode == StatusCode.NO_CLOSE) {
            log.error("Connection closed abnormally");
            messageProcessor.onComplete();
        } else if (statusCode != StatusCode.NORMAL) {
            log.error("Connection closed because of error");
            messageProcessor.onError(new Errors.ConnectionUnexpectedCloseError(statusCode));
        } else {
            messageProcessor.onComplete();
        }

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
            session.close(new CloseStatus(StatusCode.NORMAL, "Client disconnects"));
        });
    }

    public Mono<Void> send(String message) {

        log.trace("<=: {}", message);

        if (session == null) {
            throw new IllegalStateException("Not connected");
        }

        return Mono.create(sink -> {
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

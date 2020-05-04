package lv.sergluka.mt_access;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import reactor.core.publisher.DirectProcessor;

import java.net.URI;
import java.util.Map;

@Slf4j
public abstract class WsClientImpl extends WebSocketClient {

    private final DirectProcessor<String> messageProcessor = DirectProcessor.create();

    protected abstract void onConnectionError(Throwable cause);
    protected abstract void onConnected();
    protected abstract void onDisconnected();

    WsClientImpl(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.debug("Connected from {} to {}", getLocalSocketAddress(), getRemoteSocketAddress());
        onConnected();
    }

    @Override
    public void onMessage(String message) {
        log.trace("=> {}", message);
        messageProcessor.onNext(message);
    }

    @Override
    public void onWebsocketPing(WebSocket conn, Framedata framedata) {
        super.onWebsocketPing(conn, framedata);
        log.trace("ping/pong");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        try {
            log.info("Connection is closed {}. reason={}, code={}", remote ? "by server" : "by client", reason, code);

            if (code != CloseFrame.NORMAL) {
                onConnectionError(new Errors.MtAccessConnectionError(code, reason));
            }
            onDisconnected();
        } catch (Exception e) {
            log.error("Fail to handle connection closing", e);
        }
    }

    @Override
    public void onError(Exception cause) {
        onConnectionError(cause);
    }

    DirectProcessor<String> messages() {
        return messageProcessor;
    }
}

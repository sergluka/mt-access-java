package com.finplant.mtm_client;

import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.exceptions.ApplicationError;
import io.crossbar.autobahn.wamp.types.CloseDetails;
import io.crossbar.autobahn.wamp.types.ExitInfo;
import io.crossbar.autobahn.wamp.types.SessionDetails;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;
import reactor.util.annotation.NonNull;

public class MtmClient implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(MtmClient.class);

    private Session session = new Session();
    private ReplayProcessor<Boolean> connectSubject = ReplayProcessor.create();

    public MtmClient() {
        session.addOnJoinListener(this::onJoin);
        session.addOnLeaveListener(this::onLeave);
    }

    @Override
    public void close() {
        session.leave();
    }

    public Mono<Void> connect(String realm, String url) {
        return Mono.defer(() -> {
            Client client = new Client(session, url, realm);
            Mono.fromCompletionStage(client.connect()).subscribe(none -> {}, this::onConnectError);
            return connectSubject.filter(status -> status).take(1).then();
        }).onErrorMap(ApplicationError.class, Errors::from);
    }

    private void onConnectError(Throwable throwable) {
        connectSubject.onError(throwable);
    }

    public Mono<Boolean> attach(String server, Integer login, String password) {
        return attach(server, login, password, Duration.ZERO);
    }

    public Mono<Boolean> attach(@NonNull String server, @NonNull Integer login, @NonNull String password,
                                @NonNull Duration reconnectDelay) {
        Map<String, Object> params = Map.of("server", server,
                                            "login", login,
                                            "password", password,
                                            "reconnect_delay", reconnectDelay.toMillis());
        return Mono.fromCompletionStage(session.call("connect", params))
                   .map(result -> (Boolean) result.kwresults.get("is_connected"));
    }

    private void onJoin(Session session, SessionDetails details) {
        log.info("Is connected to realm '{}' with id #{}", details.realm, details.sessionID);
        connectSubject.onNext(true);
    }

    private void onLeave(Session session, CloseDetails details) {
        log.info("Is disconnected");
        connectSubject.onNext(false);
    }
}

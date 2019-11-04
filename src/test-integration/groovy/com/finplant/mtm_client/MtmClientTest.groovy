package com.finplant.mtm_client

import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.Duration

class MtmClientTest extends Specification {

    @Shared
    @Subject
    private MtmClient client = new MtmClient()

    def setupSpec() {
        client.connect("mt4api", "ws://127.0.0.1:12344").subscribeOn(Schedulers.elastic()).block(Duration.ofSeconds(5))
        client.attach("127.0.0.1", 1, "manager")
                .filter { it }
                .timeout(Duration.ofSeconds(30), Mono.error(new Exception("Connection timeout")))
                .block()
    }

    def "smoke"() {
        expect:
        true
    }
}

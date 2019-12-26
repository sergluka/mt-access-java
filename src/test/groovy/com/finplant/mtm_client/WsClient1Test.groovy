package com.finplant.mtm_client

import reactor.test.StepVerifier
import spock.lang.Specification

class WsClient1Test extends Specification {

    def "Connect"() {
        given:
        def client = new WsClient1();

        when:
//        client.connect(URI.create("ws://127.0.0.1:12344")).block()
        client.connect(URI.create("ws://127.0.0.1:8888")).block()
        client.send("test").block()

        then:
        StepVerifier.create(client.listen().take(1))
            .expectNext("test")
            .verifyComplete()

        cleanup:
        client.disconnect()
    }
}

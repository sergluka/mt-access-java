package com.finplant.mt_remote_client

import com.finplant.mt_remote_client.common.BaseSpecification
import reactor.test.StepVerifier
import spock.lang.Ignore

import static org.assertj.core.api.Assertions.assertThat

@Ignore("Works only with MT server plugin with dummy `MtSrvManagerProtocol`")
class ExternalCommandTests extends BaseSpecification {

    @Override
    def autoConnect() {
        return true
    }

    def "Send external command as text"() {

        expect:
        StepVerifier.create(client.protocolExtensions().externalCommand("command!"))
                .expectNext("resp")
                .verifyComplete()
    }

    def "Send external command as bytes"() {

        given:
        byte[] bytes = [0x01, 0x02, 0xFF]

        expect:
        StepVerifier.create(client.protocolExtensions().externalCommand(bytes))
                .assertNext { assertThat(it).containsExactly(0x10, 0x11, 0x12) }
                .verifyComplete()
    }
}
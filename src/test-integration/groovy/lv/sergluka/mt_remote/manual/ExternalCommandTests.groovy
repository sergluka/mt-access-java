package lv.sergluka.mt_remote.manual

import lv.sergluka.mt_remote.common.BaseSpecification
import reactor.test.StepVerifier

import static org.assertj.core.api.Assertions.assertThat

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
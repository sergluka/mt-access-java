package lv.sergluka.mt_remote


import lv.sergluka.mt_remote.common.BaseSpecification

import java.time.Duration

class ConnectionTests extends BaseSpecification {

    @Override
    def autoConnect() {
        return false
    }

    def "Connection should persist"() {

        when:
        client.connection().take(Duration.ofMinutes(1)).blockLast()

        then:
        noExceptionThrown()
    }

    def "Connecting with wrong credentials should raise an error"() {

        given:
        def params = MtRemoteClient.ConnectionParameters.builder()
                .uri(MT_REMOTE_URL)
                .server(MT_URL)
                .login(MT_LOGIN)
                .password("wrong password")
                .build()

        def newClient = createClient(params)

        when:
        newClient.connection().blockLast()

        then:
        thrown(Errors.MtRemoteConnectionError)
    }
}

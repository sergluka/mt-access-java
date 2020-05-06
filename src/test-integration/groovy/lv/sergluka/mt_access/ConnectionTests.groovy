package lv.sergluka.mt_access


import lv.sergluka.mt_access.common.BaseSpecification

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
        def params = MtAccessClient.ConnectionParameters.builder()
                .uri(MT_ACCESS_URL)
                .server(MT_URL)
                .login(MT_LOGIN)
                .password("wrong password")
                .build()

        def newClient = createClient(params)

        when:
        newClient.connection().blockLast()

        then:
        thrown(Errors.MtAccessConnectionError)
    }
}

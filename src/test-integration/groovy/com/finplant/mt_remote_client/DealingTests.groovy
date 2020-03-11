package com.finplant.mt_remote_client

import com.finplant.mt_remote_client.common.BaseSpecification
import com.finplant.mt_remote_client.dto.mt4.Mt4TradeRequest
import com.finplant.mt_remote_client.dto.mt4.Mt4TradeTransaction
import com.finplant.mt_remote_client.procedures.DealingProcedures
import reactor.test.StepVerifier
import spock.lang.Ignore

import java.time.Duration

@Ignore("Works only with manual trading via Terminal and symbol with Market execution")
class DealingTests extends BaseSpecification {

    @Override
    def autoConnect() {
        return true
    }

    def "Start dealing and confirm requests"() {

        given:
        def login = addUser()

        Mt4TradeRequest request

        expect:
        StepVerifier.create(client.dealing().listen())
                .assertNext {
                    assert it.id > 0
                    assert it.login == login
                    assert it.manager == 1
                    assert it.balance > 0.0
                    assert it.trade.order == 0
                    assert it.trade.type == Mt4TradeTransaction.Type.ORDER_MK_OPEN

                    request = it
                }
                .thenCancel()
                .verify(Duration.ofMinutes(10))

        StepVerifier.create(client.orders().listen())
                .then { client.dealing().confirm(request.id, 2.11, 2.12, DealingProcedures.ConfirmMode.NORMAL).block() }
                .assertNext {
                    assert it.login == login
                    assert it.openPrice == 2.11 || it.openPrice == 2.12
                }
                .thenCancel()
                .verify(Duration.ofSeconds(10))
    }

    def "Start dealing and reject request"() {

        given:
        def login = addUser()

        Mt4TradeRequest request

        expect:
        StepVerifier.create(client.dealing().listen())
                .assertNext {
                    assert it.id > 0
                    assert it.login == login
                    assert it.manager == 1
                    assert it.balance > 0.0
                    assert it.trade.order == 0
                    assert it.trade.type == Mt4TradeTransaction.Type.ORDER_MK_OPEN

                    request = it
                }
                .thenCancel()
                .verify(Duration.ofMinutes(10))

        and:
        when:
        client.dealing().reject(request.id).block()

        then:
        noExceptionThrown()
    }

    def "Start dealing and reset requests"() {

        given:
        def login = addUser()

        Mt4TradeRequest request

        expect:
        StepVerifier.create(client.dealing().listen())
                .assertNext {
                    assert it.id > 0
                    assert it.login == login
                    assert it.manager == 1
                    assert it.balance > 0.0
                    assert it.trade.order == 0
                    assert it.trade.type == Mt4TradeTransaction.Type.ORDER_MK_OPEN

                    request = it
                }
                .thenCancel()
                .verify(Duration.ofMinutes(10))

        and:
        when:
        client.dealing().reset(request.id).block()

        then:
        noExceptionThrown()
    }

    def "Start dealing and requote requests for IE execution"() {

        given:
        def login = addUser()

        Mt4TradeRequest request

        expect:
        StepVerifier.create(client.dealing().listen())
                .assertNext {
                    assert it.id > 0
                    assert it.login == login
                    assert it.manager == 1
                    assert it.balance > 0.0
                    assert it.trade.order == 0
                    assert it.trade.type == Mt4TradeTransaction.Type.ORDER_IE_OPEN

                    request = it
                }
                .thenCancel()
                .verify(Duration.ofMinutes(10))

        and:
        when:
        client.dealing().requote(request.id, 1.0, 1.1).block()

        then:
        noExceptionThrown()
    }

}
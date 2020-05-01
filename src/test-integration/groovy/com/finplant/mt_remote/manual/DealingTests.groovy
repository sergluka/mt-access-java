package com.finplant.mt_remote.manual

import com.finplant.mt_remote.common.BaseSpecification
import com.finplant.mt_remote.dto.mt4.Mt4TradeTransaction
import com.finplant.mt_remote.procedures.DealingProcedures
import reactor.test.StepVerifier
import spock.lang.Ignore

import java.time.Duration

class DealingTests extends BaseSpecification {

    private Integer login

    @Override
    def autoConnect() {
        return true
    }

    def setup() {
        login = addUser()
    }

    def "Start dealing and confirm requests"() {

        when:
        def ordersListener = client.orders().listen().replay(1)
        def disposable = ordersListener.connect()

        StepVerifier.create(client.dealing().listen())
                .assertNext {
                    assert it.id > 0
                    assert it.login == login
                    assert it.manager == 1
                    assert it.balance > 0.0
                    assert it.trade.order == 0
                    assert it.trade.type == Mt4TradeTransaction.Type.ORDER_MK_OPEN

                    client.dealing().confirm(it.id, 2.11, 2.12, DealingProcedures.ConfirmMode.NORMAL).subscribe()
                }
                .thenCancel()
                .verify(Duration.ofMinutes(10))

        then:
        StepVerifier.create(ordersListener)
                .assertNext {
                    assert it.login == login
                    assert it.openPrice == 2.11 || it.openPrice == 2.12
                }
                .thenCancel()
                .verify(Duration.ofSeconds(10))

        cleanup:
        disposable.dispose()
    }

    def "Start dealing and reject request"() {

        when:
        StepVerifier.create(client.dealing().listen())
                .assertNext {
                    assert it.id > 0
                    assert it.login == login
                    assert it.manager == 1
                    assert it.balance > 0.0
                    assert it.trade.order == 0
                    assert it.trade.type == Mt4TradeTransaction.Type.ORDER_MK_OPEN

                    client.dealing().reject(it.id).subscribe()
                }
                .thenCancel()
                .verify(Duration.ofMinutes(10))

        then:
        noExceptionThrown()
    }

    def "Start dealing and reset requests"() {

        when:
        StepVerifier.create(client.dealing().listen())
                .assertNext {
                    assert it.id > 0
                    assert it.login == login
                    assert it.manager == 1
                    assert it.balance > 0.0
                    assert it.trade.order == 0
                    assert it.trade.type == Mt4TradeTransaction.Type.ORDER_MK_OPEN

                    client.dealing().reset(it.id).subscribe()
                }
                .thenCancel()
                .verify(Duration.ofMinutes(10))

        then:
        noExceptionThrown()
    }

    // TODO Add user in group with Instant Execution
    @Ignore
    def "Start dealing and requote requests for IE execution"() {

        when:
        StepVerifier.create(client.dealing().listen())
                .assertNext {
                    assert it.id > 0
                    assert it.login == login
                    assert it.manager == 1
                    assert it.balance > 0.0
                    assert it.trade.order == 0
                    assert it.trade.type == Mt4TradeTransaction.Type.ORDER_IE_OPEN

                    client.dealing().requote(it.id, 1.0, 1.1).subscribe()
                }
                .thenCancel()
                .verify(Duration.ofMinutes(10))

        then:
        noExceptionThrown()
    }
}
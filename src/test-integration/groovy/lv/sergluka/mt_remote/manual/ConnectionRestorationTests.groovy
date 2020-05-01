package lv.sergluka.mt_remote.manual

import lv.sergluka.mt_remote.common.BaseSpecification
import reactor.util.retry.Retry

import java.time.Duration

class ConnectionRestorationTests extends BaseSpecification {

    @Override
    def autoConnect() {
        return false
    }

    def "At manual MT Remote or MT server reconnects client is able to restore connection"() {
        expect:
        def disposable = client.connection()
                .doOnError { println "Connection lost: ${it.message}" }
                .retryWhen(Retry.fixedDelay(10, Duration.ofSeconds(1)))
                .subscribe(
                        { println "Connected" },
                        { println "Shouldn't be called: ${it}" },
                        { println "Disconnected" }
                )

        sleep(10000000)

        cleanup:
        disposable?.dispose()
    }
}

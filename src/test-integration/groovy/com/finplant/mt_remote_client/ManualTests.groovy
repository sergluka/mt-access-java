package com.finplant.mt_remote_client

import com.finplant.mt_remote_client.common.BaseSpecification
import spock.lang.Ignore

import java.time.Duration

@Ignore // comment to launch it manually
class ManualTests extends BaseSpecification {

    @Override
    def autoConnect() {
        return false
    }

    def "At manual MT Remote or MT server reconnects client is able to restore connection"() {
        expect:
        def disposable = client.connection()
                .doOnError { println "Connection lost: ${it.message}" }
                .retryBackoff(10, Duration.ofSeconds(1))
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

package com.finplant.mt_remote

import com.finplant.mt_remote.common.BaseSpecification
import reactor.core.publisher.Flux
import reactor.core.publisher.ParallelFlux
import reactor.core.scheduler.Schedulers

class LoadTests extends BaseSpecification {

    @Override
    def autoConnect() {
        return false
    }

    def "Many parallel reconnects won't raise an error"() {
        expect:

        Flux.just(createClient())
                .flatMap { it.connection() }
                .flatMap { it.config().common().get() }
                .take(1)
                .repeat(20)
                .blockLast()
    }

    def "Many parallel reconnects with many clients won't raise an error"() {
        expect:

        def reconnects = Flux.range(0, Runtime.getRuntime().availableProcessors())
                .map { createClient() }
                .flatMap { it.connection() }
                .flatMap { it.config().common().get() }
                .take(1)
                .repeat(20)

        ParallelFlux.from(reconnects)
                .runOn(Schedulers.parallel())
                .sequential()
                .blockLast()
    }
}

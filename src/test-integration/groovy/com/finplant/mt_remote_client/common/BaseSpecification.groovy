package com.finplant.mt_remote_client.common

import com.finplant.mt_remote_client.MtRemoteClient
import com.finplant.mt_remote_client.dto.mt4.Mt4TradeRecord
import com.finplant.mt_remote_client.dto.mt4.Mt4UserRecord
import com.finplant.mt_remote_client.procedures.OrderProcedures
import reactor.core.Disposable
import reactor.core.publisher.MonoProcessor
import reactor.tools.agent.ReactorDebugAgent
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.Duration

abstract class BaseSpecification extends Specification {

    protected static final URI URL = URI.create("wss://localhost:12344")
    protected static final String keystorePassword = "zHRNZfWcwzLMRE4b4wGaaRkQHVGMpJ7d"

    protected static final String MT_URL = "127.0.0.1"
    protected static final int MT_LOGIN = 1
    protected static final String MT_PASSWORD = "manager"

    private static final def params = MtRemoteClient.ConnectionParameters.builder()
            .uri(URL)
            .server(MT_URL)
            .login(MT_LOGIN)
            .password(MT_PASSWORD)
            .build()

    @Shared
    @Subject
    protected MtRemoteClient client =
            MtRemoteClient.createSecure(params,
                                        BaseSpecification.classLoader.getResourceAsStream("keystore.jks"),
                                        keystorePassword, true)

    @Shared
    private Disposable connectionDisposable

    abstract def autoConnect()

    def setupSpec() {
        ReactorDebugAgent.init()

        if (autoConnect()) {
            connect()
        }
    }

    def setup() {
        if (autoConnect() && connectionDisposable != null) {
            cleanupMt()
        }
    }

    def cleanupSpec() {
        if (autoConnect()) {
            disconnect()
        }
    }

    protected connect() {
        MonoProcessor blockingProcessor = MonoProcessor.create();
        connectionDisposable = client.connection().subscribe({ blockingProcessor.onNext(true) })
        blockingProcessor.timeout(Duration.ofSeconds(30)).block()
    }

    protected disconnect() {
        if (connectionDisposable != null) {
            connectionDisposable.dispose()
        }
    }

    protected reconnect() {
        disconnect()
        connect()
    }

    protected def addUser(def deposit = 1000000.0) {
        def user = Mt4UserRecord.builder()
                .login(100)
                .enable(true)
                .group("demoforex")
                .enableChangePassword(true)
                .readOnly(false)
                .enableOtp(false)
                .passwordPhone("PhonePass")
                .name("Johans Smits")
                .country("Latvia")
                .email("sergey.lukashevich@finplant.com")
                .comment("User comment")
                .leverage(10000)
                .sendReports(false)
                .password("Trader")
                .passwordInvestor("Investor")
                .build()
        def login = client.users().add(user).block(Duration.ofSeconds(3))

        if (deposit > 0.0) {
            def request = OrderProcedures.BalanceOrderParameters.builder()
                    .login(login)
                    .command(Mt4TradeRecord.Command.BALANCE)
                    .amount(1000000.0)
                    .comment("Initial deposit")
                    .build()
            client.orders().balance(request).block()
        }

        return login
    }

    // TODO: cleanup all managers, except "1"
    private def cleanupMt() {
        client.users().getAll()
                .filter { it.login != 1 }
                .flatMap { client.users().delete(it.login) }
                .blockLast(Duration.ofSeconds(30))
    }
}

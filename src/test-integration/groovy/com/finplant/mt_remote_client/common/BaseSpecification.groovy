package com.finplant.mt_remote_client.common

import com.finplant.mt_remote_client.MtRemoteClient
import com.finplant.mt_remote_client.dto.mt4.Mt4TradeRecord
import com.finplant.mt_remote_client.dto.mt4.Mt4UserRecord
import com.finplant.mt_remote_client.procedures.OrderProcedures
import reactor.tools.agent.ReactorDebugAgent
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.Duration

class BaseSpecification extends Specification {

    protected static final URI URL = URI.create("wss://localhost:12344")
    protected static final String keystorePassword = "zHRNZfWcwzLMRE4b4wGaaRkQHVGMpJ7d"

    protected static final String MT_URL = "127.0.0.1"
    protected static final int MT_LOGIN = 1
    protected static final String MT_PASSWORD = "manager"

    @Shared
    @Subject
    private MtRemoteClient client = MtRemoteClient.createSecure(URL,
            BaseSpecification.classLoader.getResourceAsStream("keystore.jks"), keystorePassword, true)

    def setupSpec() {
        ReactorDebugAgent.init()

        client.connect().block(Duration.ofSeconds(30))
        client.connectToMt(MT_URL, MT_LOGIN, MT_PASSWORD, Duration.ofSeconds(10)).block(Duration.ofSeconds(30))
    }

    def setup() {
        cleanupMt()
    }

    def cleanupSpec() {
        client.disconnect().block(Duration.ofSeconds(30))
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

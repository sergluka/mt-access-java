package com.finplant.mt_remote_client

import com.finplant.mt_remote_client.dto.*
import com.finplant.mt_remote_client.procedures.DealingProcedures
import com.finplant.mt_remote_client.procedures.OrderProcedures
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.tools.agent.ReactorDebugAgent
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.Duration
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset

import static org.assertj.core.api.Assertions.assertThat

class MtRemoteClientTest extends Specification {

    private static final URI URL = URI.create("wss://localhost:12344")
    private static final String keystorePassword = "zHRNZfWcwzLMRE4b4wGaaRkQHVGMpJ7d"

    private static final String MT_URL = "127.0.0.1"
    private static final int MT_LOGIN = 1
    private static final String MT_PASSWORD = "manager"

    @Shared
    @Subject
    private MtRemoteClient client = MtRemoteClient.createSecure(URL,
            MtRemoteClientTest.classLoader.getResourceAsStream("keystore.jks"), keystorePassword, true)

    def setupSpec() {
        ReactorDebugAgent.init();

        client.connect().block(Duration.ofSeconds(30))
        client.connectToMt(MT_URL, MT_LOGIN, MT_PASSWORD, Duration.ofSeconds(10)).block(Duration.ofSeconds(30))
    }

    def setup() {
        cleanupMt()
    }

    def cleanupSpec() {
        client.disconnect().block(Duration.ofSeconds(30))
    }

    // TODO: cleanup all managers, except "1"
    def cleanupMt() {
        client.users().getAll()
                .filter { it.login != 1 }
                .flatMap { client.users().delete(it.login) }
                .blockLast(Duration.ofSeconds(30))
    }

    def addUser(def deposit = 1000000.0) {
        def user = UserRecord.builder()
                .login(100)
                .enable(true)
                .group("miniforex")
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
                    .command(TradeRecord.Command.BALANCE)
                    .amount(1000000.0)
                    .comment("Initial deposit")
                    .build()
            client.orders().balance(request).block()
        }

        return login
    }

    def "smoke"() {
        expect:
        true
    }

    def "reconnect"() {
        when:
        client.disconnect().block(Duration.ofSeconds(10))
        client.connect(URI.create(URL)).block(Duration.ofSeconds(10))
        client.connectToMt(MT_URL, MT_LOGIN, MT_PASSWORD, Duration.ofSeconds(10)).block()

        then:
        notThrown()
    }

    def "Second connection to MT should cause an error"() {
        when:
        client.connectToMt(MT_URL, MT_LOGIN, MT_PASSWORD, Duration.ofSeconds(10)).block()

        then:
        thrown(Errors.MtmError)
    }

    def "Expect for MT connection status changes after disconnect/connect"() {

        expect:
        StepVerifier.create(client.remoteConnection())
                .expectSubscription()
                .expectNoEvent(Duration.ofSeconds(1))
                .then { client.disconnectFromMt().block(Duration.ofSeconds(10)) }
                .expectNext(false)
                .then { client.connectToMt(MT_URL, MT_LOGIN, MT_PASSWORD, Duration.ofSeconds(10)).block(Duration.ofSeconds(10)) }
                .expectNext(true)
                .thenCancel()
                .verify(Duration.ofSeconds(30))
    }

    def "Validate common config"() {
        when:
        def config1 = ConCommon.builder()
                .timeout(180)
                .accountUrl(URI.create("https://127.0.0.1"))
                .address(MT_URL)
                .name("Demo")
                .port(443)
                .typeOfDemo(ConCommon.TypeOfDemo.PROLONG)
                .timeOfDemoDays(60)
                .daylightCorrection(false)
                .timezone(ZoneOffset.UTC)
                .timesyncServer("")
                .feederTimeoutSeconds(30)
                .keepEmailsDays(30)
                .keepTicksDays(31)
                .statementWeekend(true)
                .endOfDayHour(12)
                .endOfDayMinute(0)
                .optimizationTimeMinutes("23:0")
                .overmonthLastMonth(Month.JANUARY)
                .antiflood(false)
                .antifloodMaxConnections(10)
                .webAdresses(List.of(MT_URL))
                .statementMode(ConCommon.StatementMode.END_DAY)
                .liveupdateMode(ConCommon.LiveUpdateMode.NO)
                .lastActivateTime(10)
                .stopLastTime(11)
                .monthlyStateMode(ConCommon.MonthlyStateMode.END_MONTH)
                .rolloversMode(ConCommon.RolloverChargingMode.ROLLOVER_NORMAL)
                .overnightLastDay(30)
                .overnightLastTime(31)
                .overnightPrevTime(32)
                .stopDelaySeconds(60)
                .stopReason(ConCommon.StopReason.SHUTDOWN)
                .build()

        client.config().common().set(config1).block(Duration.ofSeconds(10))
        def config2 = client.config().common().get().block(Duration.ofSeconds(10))

        then:
        assertThat(config2).isEqualToIgnoringNullFields(config1)
        assertThat(config2.owner).isNotEmpty()
        assertThat(config2.adapters).isNotEmpty()
        assertThat(config2.serverVersion).isPositive()
        assertThat(config2.serverBuild).isPositive()
        assertThat(config2.lastOrder).isPositive()
        assertThat(config2.lastLogin).isPositive()
        assertThat(config2.lostCommissionLogin).isPositive()
    }

    def "Validate group config"() {

        given:
        client.config().groups().delete("test").onErrorResume { Mono.empty() }.block()

        when:
        def group1 = ConGroup.builder()
                .group("test")
                .enable(true)
                .timeoutSeconds(60)
                .otpMode(ConGroup.OtpMode.DISABLED)
                .company("Company")
                .signature("Signature")
                .supportPage("localhost")
                .smtpServer(MT_URL)
                .smtpLogin("login")
                .smtpPassword("password")
                .supportEmail("a@a.lv")
                .templatesPath("c:/")
                .copies(10)
                .reports(true)
                .defaultLeverage(1000) // TODO
                .defaultDeposit(100000.0)
                .maxSymbols(0)
                .currency("EUR")
                .credit(100.0)
                .marginCall(0)
                .marginMode(ConGroup.MarginMode.USE_ALL)
                .marginStopout(0)
                .interestRate(0.0)
                .useSwap(true)
                .news(ConGroup.NewsMode.TOPICS)
                .rights(Set.of(ConGroup.Rights.EMAIL, ConGroup.Rights.TRAILING))
                .checkIePrices(false)
                .maxPositions(0)
                .closeReopen(true)
                .hedgeProhibited(true)
                .closeFifo(true)
                .hedgeLargeLeg(true)
                .marginType(ConGroup.MarginType.CURRENCY)
                .archivePeriod(90)
                .archiveMaxBalance(10)
                .stopoutSkipHedged(true)
                .archivePendingPeriod(true)
                .newsLanguages(Set.of("ru-RU", "en-EN"))
                .build()

        client.config().groups().add(group1).block(Duration.ofSeconds(10))

        client.disconnect().block(Duration.ofSeconds(10))
        client.connect(URI.create(URL)).block(Duration.ofSeconds(10))
        client.connectToMt(MT_URL, MT_LOGIN, MT_PASSWORD, Duration.ofSeconds(10)).block()

        def group2 = client.config().groups().get("test").block(Duration.ofSeconds(10))

        then:
        assertThat(group2).isEqualToIgnoringNullFields(group1)
    }

    def "Subscribe to groups"() {

        given:
        client.config().groups().delete("test").onErrorResume { Mono.empty() }.block()

        def group1 = ConGroup.builder()
                .group("test")
                .build()

        expect:
        StepVerifier.create(client.config().groups().listen())
                .then { client.config().groups().add(group1).block(Duration.ofSeconds(10)) }
                .assertNext { assert it.group == "test" }
                .thenCancel()
                .verify()
    }

    def "Validate user record"() {

        given:
        def user1 = UserRecord.builder()
                .login(100)
                .enable(true)
                .group("miniforex")
                .enableChangePassword(true)
                .readOnly(false)
                .enableOtp(false)
                .passwordPhone("PhonePass")
                .name("Johans Smits")
                .country("Latvia")
                .city("Riga")
                .state("n/a")
                .zipcode("LV-1063")
                .address("Maskavas 322 - 501")
                .leadSource("Source")
                .phone("+37100112233")
                .email("a@a.lv")
                .comment("User comment")
                .id("id1")
                .status("STATUS")
                .leverage(1000)
                .agentAccount(1)
                .taxes(30.33)
                .sendReports(false)
                .mqid(123456)
                .userColor(0xFF00FF)
                .apiData((0..15) as byte[])
                .password("Pass1")
                .passwordInvestor("Pass2")
                .build()

        when:
        def newLogin = client.users().add(user1).timeout(Duration.ofSeconds(3)).block()

        then:
        newLogin == 100

        and:
        when:
        user1.password = null
        user1.passwordInvestor = null

        def user2 = client.users().get(newLogin).timeout(Duration.ofSeconds(30)).block()

        then:
        assertThat(user2).isEqualToIgnoringNullFields(user1)

        assertThat(user2.lastDate).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(user2.registrationDate).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(user2.lastIp).isEqualTo("0.0.0.0")
        assertThat(user2.prevMonthBalance).isEqualTo(0.0)
        assertThat(user2.prevDayBalance).isEqualTo(0.0)
        assertThat(user2.prevMonthEquity).isEqualTo(0.0)
        assertThat(user2.prevDayEquity).isEqualTo(0.0)
        assertThat(user2.interestrate).isEqualTo(0.0)
        assertThat(user2.balance).isEqualTo(0.0)
        assertThat(user2.credit).isEqualTo(0.0)
    }

    def "Get event at user creation"() {
        given:
        def user1 = UserRecord.builder()
                .login(101)
                .enable(true)
                .group("miniforex")
                .enableChangePassword(true)
                .readOnly(false)
                .enableOtp(false)
                .passwordPhone("PhonePass")
                .name("Johans Smits")
                .country("Latvia")
                .city("Riga")
                .state("n/a")
                .zipcode("LV-1063")
                .address("Maskavas 322 - 501")
                .leadSource("Source")
                .phone("+37100112233")
                .email("a@a.lv")
                .comment("User comment")
                .id("id1")
                .status("STATUS")
                .leverage(1000)
                .agentAccount(1)
                .taxes(30.33)
                .sendReports(false)
                .mqid(123456)
                .userColor(0xFF00FF)
                .apiData((0..15) as byte[])
                .password("Pass1")
                .passwordInvestor("Pass2")
                .build()

        expect:
        StepVerifier.create(client.users().listen())
                .then { client.users().add(user1).timeout(Duration.ofSeconds(3)).block() }
                .assertNext { it.login == 101 }
                .thenCancel()
                .verify(Duration.ofSeconds(10))
    }

    def "Validate managers"() {

        given:
        client.config().managers().delete(123).onErrorResume { Mono.empty() }.block()

        def forexSecurity = ConManagerSecurity.builder()
                .enable(true)
                .maximumLots(100)
                .minimumLots(2)
                .build()
        def cfdSecurity = ConManagerSecurity.builder()
                .enable(false)
                .maximumLots(0)
                .minimumLots(0)
                .build()

        def manager1 = ConManager.builder()
                .login(123)
                .name("Manager1")
                .manager(true)
                .money(true)
                .online(true)
                .riskman(true)
                .broker(true)
                .admin(true)
                .logs(true)
                .reports(true)
                .trades(true)
                .marketWatch(true)
                .email(true)
                .userDetails(true)
                .seeTrades(true)
                .news(true)
                .plugins(true)
                .serverReports(true)
                .techSupport(true)
                .market(true)
                .notifications(true)
                .ipFilter(true)
                .ipFrom("128.0.0.0")
                .ipTo("128.255.255.255")
                .mailbox("MAILBOX")
                .groups(List.of("manager", "mini*", "*"))
                .infoDepth(10)
                .securities(["Forex": forexSecurity])
                .build()

        when:
        client.config().managers().add(manager1).timeout(Duration.ofSeconds(30)).block()
        def manager2 = client.config().managers().get(123).timeout(Duration.ofSeconds(30)).block()

        then:
        assertThat(manager2).isEqualToIgnoringGivenFields(manager1, "securities")
        assertThat(manager2.securities["Forex"]).isEqualToComparingFieldByField(forexSecurity)
        assertThat(manager2.securities["CFD"]).isEqualToComparingFieldByField(cfdSecurity)
    }

    def "Get last prices for EURUSD"() {

        given:
        client.symbols().showSymbol("EURUSD").block()

        expect:
        sleep(1000)
        StepVerifier.create(client.market().ticksGet("EURUSD"))
                .assertNext {
                    assert it.time != null
                    assert it.ask > 0.0
                    assert it.bid > 0.0
                }
                .verifyComplete()

        cleanup:
        client.symbols().hideSymbol("EURUSD").block()
    }

    def "Subscribe to EURUSD tick"() {

        given:
        client.symbols().showSymbol("EURUSD").block()

        sleep(1000)
        def tick = client.market().ticksGet("EURUSD").blockFirst()
        def newBid = tick.bid + 0.0001
        def newAsk = tick.ask + 0.0001

        expect:
        StepVerifier.create(client.symbols().listen())
                .then { client.symbols().addTick("EURUSD", newBid, newAsk).block() }
                .assertNext {
                    assert it.time != null
                    assert it.bid == newBid
                    assert it.ask == newAsk
                }
                .thenCancel()
                .verify()

        cleanup:
        client.symbols().hideSymbol("EURUSD").block()
    }

    // TODO: prepare users, symbols and etc before tests.
    def "Open order"() {

        given:
        def login = addUser()

        def openParams = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(TradeRecord.Command.BUY)
                .volume(0.01)
                .price(2.0)
                .build()

        when:
        def order = client.orders().open(openParams).block()

        then:
        sleep(1000)
        def trade = client.orders().get(order).block()

        trade.order == order
        trade.login == login
        trade.symbol == "EURUSD"
        trade.command == TradeRecord.Command.BUY
        trade.volume == 0.01
        trade.openPrice == 2.0
        trade.openTime != null
        trade.closeTime == null
    }

    def "Open and modify order"() {

        given:
        def login = addUser()

        def openParams = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(TradeRecord.Command.BUY_STOP)
                .volume(0.01)
                .price(2.0)
                .build()

        def modifyParams = OrderProcedures.ModifyOrderParameters.builder()
                .price(2.1)
                .stopLoss(1.0)
                .takeProfit(1000.0)
                .expiration(LocalDateTime.of(2025, 12, 1, 23, 0, 0))
                .comment("new comment")
                .build()

        def order = client.orders().open(openParams).block()

        when:
        def modifyParams2 = modifyParams.toBuilder().order(order).build()
        client.orders().modify(modifyParams2).block()

        then:
        sleep(1000)
        def trade = client.orders().get(order).block()

        trade.openPrice == 2.1
        trade.stopLoss == 1.0
        trade.takeProfit == 1000.0
        trade.expiration == LocalDateTime.of(2025, 12, 1, 23, 0, 0)
        trade.comment == "new comment"
    }

    def "Open and close order"() {

        given:
        def login = addUser()

        def openParams = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(TradeRecord.Command.SELL)
                .volume(0.02)
                .price(2.0)
                .build()

        def order = client.orders().open(openParams).block()

        when:
        client.orders().close(order, 2.0, 0.02).block()

        then:
        def trade = client.orders().getHistory(login, null, null).blockLast()

        trade.order == order
        trade.profit == 0.0
        trade.closeTime != null
    }

    def "Open and cancel LMT order"() {

        given:
        def login = addUser()

        def openParams = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(TradeRecord.Command.SELL_LIMIT)
                .volume(0.02)
                .price(2.0)
                .build()

        def order = client.orders().open(openParams).block()

        when:
        client.orders().delete(order, TradeRecord.Command.SELL_LIMIT).block()

        then:
        def trade = client.orders().getHistory(login, null, null).blockLast()

        trade.order == order
        trade.profit == 0.0
        trade.closeTime != null
        trade.comment == "cancelled by dealer"
    }

    def "Open and activate LMT order"() {

        given:
        def login = addUser()

        def openParams = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(TradeRecord.Command.SELL_LIMIT)
                .volume(0.02)
                .price(2.0)
                .build()

        def order = client.orders().open(openParams).block()

        when:
        client.orders().activate(order, 10.0).block()

        then:
        sleep(1000)
        def trade = client.orders().get(order).block()

        trade.command == TradeRecord.Command.SELL;
        trade.openPrice == 10.0
    }

    def "Add balance to user"() {

        given:
        def login = addUser(0.0)
        def balance = OrderProcedures.BalanceOrderParameters.builder()
                .login(login)
                .command(TradeRecord.Command.BALANCE)
                .amount(111.11)
                .build()

        when:
        client.orders().balance(balance).block()

        then:
        client.users().get(login).block().balance == 111.11
        client.users().get(login).block().credit == 0.0
    }

    def "Add credit to user"() {

        given:
        def login = addUser(0.0)
        def balance = OrderProcedures.BalanceOrderParameters.builder()
                .login(login)
                .command(TradeRecord.Command.CREDIT)
                .expiration(LocalDateTime.of(2030, 1, 1, 12, 0, 0))
                .amount(111.11)
                .build()

        when:
        sleep(1000)
        client.orders().balance(balance).block()

        then:
        client.users().get(login).block().balance == 0.0
        client.users().get(login).block().credit == 111.11
    }

    def "Close-by orders"() {

        given:
        def login = addUser()

        def openParams1 = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(TradeRecord.Command.BUY)
                .volume(0.02)
                .price(2.0)
                .build()
        def openParams2 = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(TradeRecord.Command.SELL)
                .volume(0.05)
                .price(2.0)
                .build()

        def order1 = client.orders().open(openParams1).block()
        def order2 = client.orders().open(openParams2).block()

        when:
        client.orders().closeBy(order1, order2).block()

        then:
        def closedTrades = client.orders().getHistory(login, null, null).takeLast(2).collectList().block()

        closedTrades[0].with {
            assert it.order == order1
            assert it.volume == 0.02
            assert it.command == TradeRecord.Command.BUY
            assert it.comment == "partial close"
        }
        closedTrades[1].with {
            assert it.order == order2
            assert it.volume == 0.00
            assert it.command == TradeRecord.Command.SELL
            assert it.comment == "close hedge by #${order1}"
        }

        def trade2 = client.orders().get(order2 + 1).block()

        trade2.order == order2 + 1
        trade2.volume == 0.03
        trade2.command == TradeRecord.Command.SELL
        trade2.comment == "from #${order1}"
    }

    def "Close all orders"() {

        given:
        def login = addUser()

        def openParams1 = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(TradeRecord.Command.BUY)
                .volume(0.07)
                .price(2.0)
                .build()
        def openParams2 = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(TradeRecord.Command.SELL)
                .volume(0.05)
                .price(2.0)
                .build()
        def openParams3 = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(TradeRecord.Command.SELL)
                .volume(0.10)
                .price(2.0)
                .build()

        def order1 = client.orders().open(openParams1).block()
        def order2 = client.orders().open(openParams2).block()
        def order3 = client.orders().open(openParams3).block()

        when:
        client.orders().closeAll(login, "EURUSD").block()

        then:
        sleep(1000)
        def closedTrades = client.orders().getHistory(100, null, null)
                .takeLast(3)
                .collectSortedList { o1, o2 -> o1.order <=> o2.order }
                .block()

        closedTrades[0].with {
            assert it.order == order2
            assert it.volume == 0.00
            assert it.command == TradeRecord.Command.SELL
            assert it.comment == "close hedge by #${order1}"
        }
        closedTrades[1].with {
            assert it.order == order3
            assert it.volume == 0.02
            assert it.command == TradeRecord.Command.SELL
            assert it.comment == "partial close"
        }
        closedTrades[2].with {
            assert it.order == order3 + 1
            assert it.volume == 0.00
            assert it.command == TradeRecord.Command.BUY
            assert it.comment == "close hedge by #${order3}"
        }

        def openedTrades = client.orders().getAll().collectList().block()
        openedTrades.size() == 1

        openedTrades[0].with {
            assert it.order == order3 + 2
            assert it.volume == 0.08
            assert it.command == TradeRecord.Command.SELL
            assert it.comment == "partial close"
        }
    }

    def "Get all open orders"() {

        given:
        def login = addUser()

        def openParams = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(TradeRecord.Command.SELL)
                .volume(0.02)
                .price(2.0)
                .build()

        def order1 = client.orders().open(openParams).block()
        def order2 = client.orders().open(openParams).block()

        when:
        sleep(1000)
        def openOrders = client.orders().getAll().collectList().block()

        then:
        assertThat(openOrders).extracting("order").contains(order1, order2)
    }

    def "Get all open orders for user"() {

        given:
        def login = addUser()

        def openParams = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(TradeRecord.Command.SELL)
                .volume(0.02)
                .price(2.0)
                .build()

        def order1 = client.orders().open(openParams).block()
        def order2 = client.orders().open(openParams).block()

        when:
        sleep(1000)
        def openOrders = client.orders().getByLogin(login, "miniforex").collectList().block()

        then:
        assertThat(openOrders).extracting("order").containsExactly(order1, order2)
    }

    def "Subscribe to trades"() {

        given:
        def login = addUser()

//         Flush all incoming events from previous tests, if exists
        client.orders().listen().timeout(Duration.ofSeconds(1), Mono.empty()).blockLast()

        def openParams = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(TradeRecord.Command.SELL)
                .volume(0.02)
                .price(2.0)
                .build()

        expect:
        def order

        StepVerifier.create(client.orders().listen())
                .then { order = client.orders().open(openParams).block() }
                .assertNext {
                    assert it.order == order
                }
                .thenCancel()
                .verify()
    }

    @Ignore("Requires manual MT server restarts")
    def "Long connection"() {

        expect:
        sleep(1000000)
    }

    @Ignore("Works only with plugin with dummy `MtSrvManagerProtocol`")
    def "Send external command as text"() {

        expect:
        StepVerifier.create(client.protocolExtensions().externalCommand("command!"))
                .expectNext("resp")
                .verifyComplete()
    }

    @Ignore("Works only with plugin with dummy `MtSrvManagerProtocol`")
    def "Send external command as bytes"() {

        given:
        byte[] bytes = [0x01, 0x02, 0xFF]

        expect:
        StepVerifier.create(client.protocolExtensions().externalCommand(bytes))
                .assertNext { assertThat(it).containsExactly(0x10, 0x11, 0x12) }
                .verifyComplete()
    }

    @Ignore("Works only with manual trading via Terminal and symbol with Market execution")
    def "Start dealing and confirm requests"() {

        given:
        def login = addUser()

        TradeRequest request

        expect:
        StepVerifier.create(client.dealing().listen())
                .assertNext {
                    assert it.id > 0
                    assert it.login == login
                    assert it.manager == 1
                    assert it.balance > 0.0
                    assert it.trade.order == 0
                    assert it.trade.type == TradeTransaction.Type.ORDER_MK_OPEN

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

    @Ignore("Works only with manual trading via Terminal")
    def "Start dealing and reject requests"() {

        given:
        def login = addUser()

        TradeRequest request

        expect:
        StepVerifier.create(client.dealing().listen())
                .assertNext {
                    assert it.id > 0
                    assert it.login == login
                    assert it.manager == 1
                    assert it.balance > 0.0
                    assert it.trade.order == 0
                    assert it.trade.type == TradeTransaction.Type.ORDER_IE_OPEN

                    request = it
                }
                .thenCancel()
                .verify(Duration.ofMinutes(10))

        and:
        when:
        client.dealing().reject(request.id).block()

        then:
        notThrown()
    }

    @Ignore("Works only with manual trading via Terminal and symbol with Instant execution")
    def "Start dealing and requote requests for IE execution"() {

        given:
        def login = addUser()

        TradeRequest request

        expect:
        StepVerifier.create(client.dealing().listen())
                .assertNext {
                    assert it.id > 0
                    assert it.login == login
                    assert it.manager == 1
                    assert it.balance > 0.0
                    assert it.trade.order == 0
                    assert it.trade.type == TradeTransaction.Type.ORDER_IE_OPEN

                    request = it
                }
                .thenCancel()
                .verify(Duration.ofMinutes(10))

        and:
        when:
        client.dealing().requote(request.id, 1.0, 1.1).block()

        then:
        notThrown()
    }
}

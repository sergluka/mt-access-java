package com.finplant.mt_remote_client

import com.finplant.mt_remote_client.common.BaseSpecification
import com.finplant.mt_remote_client.dto.mt4.*
import com.finplant.mt_remote_client.procedures.OrderProcedures
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Ignore

import java.time.Duration
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset

import static org.assertj.core.api.Assertions.assertThat

class MtRemoteClientTest extends BaseSpecification {

    def "smoke"() {
        expect:
        true
    }

    def "reconnect"() {
        when:
        client.disconnect().block(Duration.ofSeconds(10))
        client.connect().block(Duration.ofSeconds(10))
        client.connectToMt(MT_URL, MT_LOGIN, MT_PASSWORD, Duration.ofSeconds(10)).block()

        then:
        noExceptionThrown()
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
        def config1 = Mt4ConCommon.builder()
                .timeout(180)
                .accountUrl(URI.create("https://127.0.0.1"))
                .address(MT_URL)
                .name("Demo")
                .port(443)
                .typeOfDemo(Mt4ConCommon.TypeOfDemo.PROLONG)
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
                .statementMode(Mt4ConCommon.StatementMode.END_DAY)
                .liveupdateMode(Mt4ConCommon.LiveUpdateMode.NO)
                .lastActivateTime(10)
                .stopLastTime(11)
                .monthlyStateMode(Mt4ConCommon.MonthlyStateMode.END_MONTH)
                .rolloversMode(Mt4ConCommon.RolloverChargingMode.ROLLOVER_NORMAL)
                .overnightLastDay(30)
                .overnightLastTime(31)
                .overnightPrevTime(32)
                .stopDelaySeconds(60)
                .stopReason(Mt4ConCommon.StopReason.SHUTDOWN)
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

    def "New group creation"() {

        given:
        client.config().groups().delete("test").onErrorResume { Mono.empty() }.block()

        def group1 = Mt4ConGroup.builder()
                .group("test")
                .enable(true)
                .timeoutSeconds(60)
                .otpMode(Mt4ConGroup.OtpMode.DISABLED)
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
                .defaultLeverage(1000)
                .defaultDeposit(100000.0)
                .maxSymbols(0)
                .currency("EUR")
                .credit(100.0)
                .marginCall(0)
                .marginMode(Mt4ConGroup.MarginMode.USE_ALL)
                .marginStopout(0)
                .interestRate(0.0)
                .useSwap(true)
                .news(Mt4ConGroup.NewsMode.TOPICS)
                .rights(Set.of(Mt4ConGroup.Rights.EMAIL, Mt4ConGroup.Rights.TRAILING))
                .checkIePrices(false)
                .maxPositions(0)
                .closeReopen(true)
                .hedgeProhibited(true)
                .closeFifo(true)
                .hedgeLargeLeg(true)
                .marginType(Mt4ConGroup.MarginType.CURRENCY)
                .archivePeriod(90)
                .archiveMaxBalance(10)
                .stopoutSkipHedged(true)
                .archivePendingPeriod(true)
                .newsLanguages(Set.of("ru-RU", "en-EN"))
                .symbols(Map.of("EURUSD", new Mt4ConGroupMargin(0.1, 0.2, 10.0)))
                .build()

        when:
        client.config().groups().add(group1).block(Duration.ofSeconds(10))

        // New group is visible only after reconnect
        client.disconnect().block(Duration.ofSeconds(10))
        client.connect().block(Duration.ofSeconds(10))
        client.connectToMt(MT_URL, MT_LOGIN, MT_PASSWORD, Duration.ofSeconds(10)).block()

        def group2 = client.config().groups().get("test").block(Duration.ofSeconds(10))

        then:
        assertThat(group2.symbols["EURUSD"]).isEqualToComparingFieldByField(group1.symbols["EURUSD"])

        and:
        when:
        group1.symbols = null

        then:
        assertThat(group2).isEqualToIgnoringNullFields(group1)
    }

    def "Existing group update"() {

        given:
        client.config().groups().delete("test").onErrorResume { Mono.empty() }.block()

        def group = Mt4ConGroup.builder()
                .group("test")
                .enable(true)
                .build()

        def groupDisabled = Mt4ConGroup.builder()
                .group("test")
                .enable(false)
                .build()

        client.config().groups().add(group).block(Duration.ofSeconds(10))

        client.disconnect().block(Duration.ofSeconds(10))
        client.connect().block(Duration.ofSeconds(10))
        client.connectToMt(MT_URL, MT_LOGIN, MT_PASSWORD, Duration.ofSeconds(10)).block()

        when:
        client.config().groups().set(groupDisabled).block(Duration.ofSeconds(10))
        sleep(1000)
        def group1 = client.config().groups().get("test").block(Duration.ofSeconds(10))

        then:
        !group1.enable
    }

    def "Subscribe to groups"() {

        given:
        client.config().groups().delete("test").onErrorResume { Mono.empty() }.block()

        def group1 = Mt4ConGroup.builder()
                .group("test")
                .build()

        expect:
        StepVerifier.create(client.config().groups().listen())
                .then { client.config().groups().add(group1).block(Duration.ofSeconds(10)) }
                .assertNext { assert it.group == "test" }
                .thenCancel()
                .verify()
    }

    def "Create new user"() {

        given:
        def user1 = Mt4UserRecord.builder()
                .login(100)
                .enable(true)
                .group("demoforex")
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
                .apiData(new byte[0])
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

    def "Update existing user"() {

        given:
        def user1 = Mt4UserRecord.builder()
                .login(100)
                .group("demoforex")
                .enableChangePassword(true)
                .readOnly(false)
                .enableOtp(false)
                .name("Johans Smits")
                .password("Pass1")
                .build()

        def user2 = Mt4UserRecord.builder()
                .login(100)
                .readOnly(true)
                .build()

        client.users().add(user1).timeout(Duration.ofSeconds(3)).block()

        when:
        client.users().set(user2).timeout(Duration.ofSeconds(3)).block()
        def user3 = client.users().get(100).timeout(Duration.ofSeconds(30)).block()

        then:
        user3.readOnly
    }

    def "Get event at user creation"() {
        given:
        def user1 = Mt4UserRecord.builder()
                .login(101)
                .enable(true)
                .group("demoforex")
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

        def forexSecurity = Mt4ConManagerSecurity.builder()
                .enable(true)
                .maximumLots(100.0)
                .minimumLots(2.0)
                .build()
        def cfdSecurity = Mt4ConManagerSecurity.builder()
                .enable(false)
                .maximumLots(0.0)
                .minimumLots(0.0)
                .build()

        def manager1 = Mt4ConManager.builder()
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
        client.symbols().show("EURUSD").block()

        expect:
        sleep(1000)
        StepVerifier.create(client.market().get("EURUSD"))
                .assertNext {
                    assert it.time != null
                    assert it.ask > 0.0
                    assert it.bid > 0.0
                }
                .verifyComplete()

        cleanup:
        client.symbols().hide("EURUSD").block()
    }

    def "Subscribe to EURUSD tick"() {

        given:
        client.symbols().show("EURUSD").block()
        client.market().add("EURUSD", 0.21, 0.31).block()
        //  Flush all previous events
        client.market().listen().timeout(Duration.ofSeconds(1), Mono.empty()).blockLast()

        expect:
        StepVerifier.create(client.market().listen())
                .then {
                    client.market().add("EURUSD", 0.22, 0.32).block()
                }
                .assertNext {
                    assertThat(it.time).isNotNull()
                    assertThat(it.bid).isEqualTo(0.22)
                    assertThat(it.ask).isEqualTo(0.32)
                }
                .thenCancel()
                .verify(Duration.ofSeconds(1000))

        cleanup:
        client.symbols().hide("EURUSD").block()
    }

    // TODO: prepare users, symbols and etc before tests.
    def "Open order"() {

        given:
        def login = addUser()

        def openParams = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(Mt4TradeRecord.Command.BUY)
                .volume(0.1)
                .price(2.0)
                .build()

        when:
        def order = client.orders().open(openParams).block(Duration.ofSeconds(1))

        then:
        sleep(1000)
        def trade = client.orders().get(order).block(Duration.ofSeconds(1))

        trade.order == order
        trade.login == login
        trade.symbol == "EURUSD"
        trade.command == Mt4TradeRecord.Command.BUY
        trade.volume == 0.1
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
                .command(Mt4TradeRecord.Command.BUY_STOP)
                .volume(0.10)
                .price(2.2)
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
                .command(Mt4TradeRecord.Command.SELL)
                .volume(0.20)
                .price(2.0)
                .build()

        def order = client.orders().open(openParams).block()

        when:
        client.orders().close(order, 2.0, 0.2).block()

        then:
        def trade = client.orders().getHistory(login,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2030, 1, 1, 0, 0, 0)).blockLast()

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
                .command(Mt4TradeRecord.Command.SELL_LIMIT)
                .volume(0.20)
                .price(2.2)
                .build()

        def order = client.orders().open(openParams).block()

        when:
        client.orders().cancel(order, Mt4TradeRecord.Command.SELL_LIMIT).block()

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
                .command(Mt4TradeRecord.Command.SELL_LIMIT)
                .volume(0.20)
                .price(2.2)
                .build()

        def order = client.orders().open(openParams).block()

        when:
        client.orders().activate(order, 10.0).block()

        then:
        sleep(1000)
        def trade = client.orders().get(order).block()

        trade.command == Mt4TradeRecord.Command.SELL
        trade.openPrice == 10.0
    }

    def "Add balance to user"() {

        given:
        def login = addUser(0.0)
        def balance = OrderProcedures.BalanceOrderParameters.builder()
                .login(login)
                .command(Mt4TradeRecord.Command.BALANCE)
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
                .command(Mt4TradeRecord.Command.CREDIT)
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
                .command(Mt4TradeRecord.Command.BUY)
                .volume(0.20)
                .price(2.0)
                .build()
        def openParams2 = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(Mt4TradeRecord.Command.SELL)
                .volume(0.50)
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
            assert it.volume == 0.2
            assert it.command == Mt4TradeRecord.Command.BUY
            assert it.comment == "partial close"
        }
        closedTrades[1].with {
            assert it.order == order2
            assert it.volume == 0.00
            assert it.command == Mt4TradeRecord.Command.SELL
            assert it.comment == "close hedge by #${order1}"
        }

        def trade2 = client.orders().get(order2 + 1).block()

        trade2.order == order2 + 1
        trade2.volume == 0.3
        trade2.command == Mt4TradeRecord.Command.SELL
        trade2.comment == "from #${order1}"
    }

    def "Close all orders"() {

        given:
        def login = addUser()

        def openParams1 = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(Mt4TradeRecord.Command.BUY)
                .volume(0.7)
                .price(2.0)
                .build()
        def openParams2 = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(Mt4TradeRecord.Command.SELL)
                .volume(0.5)
                .price(2.0)
                .build()
        def openParams3 = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(Mt4TradeRecord.Command.SELL)
                .volume(1.0)
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
            assert it.command == Mt4TradeRecord.Command.SELL
            assert it.comment == "close hedge by #${order1}"
        }
        closedTrades[1].with {
            assert it.order == order3
            assert it.volume == 0.2
            assert it.command == Mt4TradeRecord.Command.SELL
            assert it.comment == "partial close"
        }
        closedTrades[2].with {
            assert it.order == order3 + 1
            assert it.volume == 0.00
            assert it.command == Mt4TradeRecord.Command.BUY
            assert it.comment == "close hedge by #${order3}"
        }

        def openedTrades = client.orders().getAll().collectList().block()
        openedTrades.size() == 1

        openedTrades[0].with {
            assert it.order == order3 + 2
            assert it.volume == 0.8
            assert it.command == Mt4TradeRecord.Command.SELL
            assert it.comment == "partial close"
        }
    }

    def "Get all open orders"() {

        given:
        def login = addUser()

        def openParams = OrderProcedures.OpenOrderParameters.builder()
                .login(login)
                .symbol("EURUSD")
                .command(Mt4TradeRecord.Command.SELL)
                .volume(0.20)
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
                .command(Mt4TradeRecord.Command.SELL)
                .volume(0.20)
                .price(2.0)
                .build()

        def order1 = client.orders().open(openParams).block()
        def order2 = client.orders().open(openParams).block()

        when:
        sleep(1000)
        def openOrders = client.orders().getByLogin(login, "demoforex").collectList().block()

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
                .command(Mt4TradeRecord.Command.SELL)
                .volume(0.20)
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
}

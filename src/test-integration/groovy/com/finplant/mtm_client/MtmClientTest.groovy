package com.finplant.mtm_client

import com.finplant.mtm_client.dto.ConCommon
import com.finplant.mtm_client.dto.ConGroup
import com.finplant.mtm_client.dto.UserRecord
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.Duration
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset

import static org.assertj.core.api.Assertions.assertThat

class MtmClientTest extends Specification {

    public static final String URL = "ws://127.0.0.1:12344"
    public static final String MT_URL = "127.0.0.1"
    public static final int MT_LOGIN = 1
    public static final String MT_PASSWORD = "manager"

    @Shared
    @Subject
    private MtmClient client = new MtmClient()

    def setupSpec() {
        client.connect(URI.create(URL)).block(Duration.ofSeconds(30))
        client.connectToMt(MT_URL, MT_LOGIN, MT_PASSWORD, Duration.ofSeconds(10)).block(Duration.ofSeconds(30))
    }

    def cleanupSpec() {
        client.disconnect().block(Duration.ofSeconds(30))
    }

    def "smoke"() {
        expect:
        true
    }

    def "Expect for connection status after connect"() {
        setup:
        true

        expect:
        Flux.merge(client.connectionStatus(), client.connectToMt(MT_URL, MT_LOGIN, MT_PASSWORD, Duration.ofSeconds(10)))
                .blockFirst()
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

        client.config().setCommon(config1).block(Duration.ofSeconds(10))
        def config2 = client.config().getCommon().block(Duration.ofSeconds(10))

        then:
        assertThat(config2).isEqualToIgnoringNullFields(config1)
        assertThat(config2.owner).isNotEmpty()
        assertThat(config2.adapters).isNotEmpty()
        assertThat(config2.optimizationLastTime).isNotNull()
        assertThat(config2.serverVersion).isPositive()
        assertThat(config2.serverBuild).isPositive()
        assertThat(config2.lastOrder).isPositive()
        assertThat(config2.lastLogin).isPositive()
        assertThat(config2.lostCommissionLogin).isPositive()
    }

    def "Validate group config"() {

        given:
        client.config().deleteGroup("test").onErrorResume { Mono.empty() }.block()

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

        client.config().addGroup(group1).block(Duration.ofSeconds(10))

//        sleep(60000)

        client.disconnect().block(Duration.ofSeconds(10))
        Flux.merge(client.connection().next(), client.connectToMt(MT_URL, 1, "manager", Duration.ofSeconds(10)))
                .blockFirst()

        def group2 = client.config().getGroup("test").block(Duration.ofSeconds(10))

        then:
        assertThat(group2).isEqualToIgnoringNullFields(group1)
    }

    def "Validate user record"() {

        given:
        def user1 = UserRecord.builder()
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
        newLogin > 0

        and:
        when:
        user1.password = null
        user1.passwordInvestor = null

        def user2 = client.users().get(newLogin).timeout(Duration.ofSeconds(300)).block()

        then:
        assertThat(user2).isEqualToIgnoringNullFields(user1)

        assertThat(user2.lastDate).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(user2.registrationDate).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(user2.timestamp).isBeforeOrEqualTo(LocalDateTime.now())
        assertThat(user2.lastIp).isEqualTo("0.0.0.0")
        assertThat(user2.prevMonthBalance).isEqualTo(0.0)
        assertThat(user2.prevDayBalance).isEqualTo(0.0)
        assertThat(user2.prevMonthEquity).isEqualTo(0.0)
        assertThat(user2.prevDayEquity).isEqualTo(0.0)
        assertThat(user2.interestrate).isEqualTo(0.0)
        assertThat(user2.balance).isEqualTo(0.0)
        assertThat(user2.credit).isEqualTo(0.0)
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

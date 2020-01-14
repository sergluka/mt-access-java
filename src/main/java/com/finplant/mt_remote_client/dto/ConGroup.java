package com.finplant.mt_remote_client.dto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConGroup {

    public enum OtpMode {
        DISABLED,
        TOTP_SHA256
    }

    public enum MarginMode {
        DONT_USE,
        USE_ALL,
        USE_PROFIT,
        USE_LOSS,
    }

    public enum NewsMode {
        NO,
        TOPICS,
        FULL,
    }

    public enum Rights {
        EMAIL("email"),
        TRAILING("trailing"),
        ADVISOR("advisor"),
        EXPIRATION("expiration"),
        SIGNALS_ALL("signals_all"),
        SIGNALS_OWN("signals_own"),
        RISK_WARNING("risk_warning"),
        FORCED_OTP_USAGE("forced_otp");

        @JsonValue
        private final String value;

        Rights(String value) {
            this.value = value;
        }
    }

    public enum MarginType {
        PERCENT,
        CURRENCY
    }

    @JsonProperty("group")
    private String group;
    @JsonProperty("enable")
    private Boolean enable;
    @JsonProperty("timeout_seconds")
    private Integer timeoutSeconds;
    @JsonProperty("otp_mode")
    private OtpMode otpMode;
    @JsonProperty("company")
    private String company;
    @JsonProperty("signature")
    private String signature;
    @JsonProperty("support_page")
    private String supportPage;
    @JsonProperty("smtp_server")
    private String smtpServer;
    @JsonProperty("smtp_login")
    private String smtpLogin;
    @JsonProperty("smtp_password")
    private String smtpPassword;
    @JsonProperty("support_email")
    private String supportEmail;
    @JsonProperty("templates_path")
    private String templatesPath;
    @JsonProperty("copies")
    private Integer copies;
    @JsonProperty("reports")
    private Boolean reports;
    @JsonProperty("default_leverage")
    private Integer defaultLeverage;
    @JsonProperty("default_deposit")
    private BigDecimal defaultDeposit;
    @JsonProperty("max_symbols")
    private Integer maxSymbols;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("credit")
    private BigDecimal credit;
    @JsonProperty("margin_call")
    private Integer marginCall;
    @JsonProperty("margin_mode")
    private MarginMode marginMode;
    @JsonProperty("margin_stopout")
    private Integer marginStopout;
    @JsonProperty("interest_rate")
    private BigDecimal interestRate;
    @JsonProperty("use_swap")
    private Boolean useSwap;
    @JsonProperty("news")
    private NewsMode news;
    @JsonProperty("rights")
    private Set<Rights> rights;
    @JsonProperty("check_ie_prices")
    private Boolean checkIePrices;
    @JsonProperty("max_positions")
    private Integer maxPositions;
    @JsonProperty("close_reopen")
    private Boolean closeReopen;
    @JsonProperty("hedge_prohibited")
    private Boolean hedgeProhibited;
    @JsonProperty("close_fifo")
    private Boolean closeFifo;
    @JsonProperty("hedge_large_leg")
    private Boolean hedgeLargeLeg;
    @JsonProperty("margin_type")
    private MarginType marginType;
    @JsonProperty("archive_period")
    private Integer archivePeriod;
    @JsonProperty("archive_max_balance")
    private Integer archiveMaxBalance;
    @JsonProperty("stopout_skip_hedged")
    private Boolean stopoutSkipHedged;
    @JsonProperty("archive_pending_period")
    private Boolean archivePendingPeriod;
    @JsonProperty("news_languages")
    private Set<String> newsLanguages;

    @JsonProperty("securities")
    private Map<String, ConGroupSecurity> securities;

    @JsonProperty("symbols")
    private Map<String, ConGroupMargin> symbols;

    @Builder(toBuilder = true)
    public ConGroup(String group, Boolean enable, Integer timeoutSeconds,
                    OtpMode otpMode, String company, String signature, String supportPage, String smtpServer,
                    String smtpLogin, String smtpPassword, String supportEmail, String templatesPath,
                    Integer copies, Boolean reports, Integer defaultLeverage, BigDecimal defaultDeposit,
                    Integer maxSymbols, String currency, BigDecimal credit, Integer marginCall,
                    MarginMode marginMode, Integer marginStopout, BigDecimal interestRate, Boolean useSwap,
                    NewsMode news, Set<Rights> rights, Boolean checkIePrices, Integer maxPositions,
                    Boolean closeReopen, Boolean hedgeProhibited, Boolean closeFifo, Boolean hedgeLargeLeg,
                    MarginType marginType, Integer archivePeriod, Integer archiveMaxBalance,
                    Boolean stopoutSkipHedged, Boolean archivePendingPeriod, Set<String> newsLanguages,
                    Map<String, ConGroupSecurity> securities, Map<String, ConGroupMargin> symbols) {
        this.group = group;
        this.enable = enable;
        this.timeoutSeconds = timeoutSeconds;
        this.otpMode = otpMode;
        this.company = company;
        this.signature = signature;
        this.supportPage = supportPage;
        this.smtpServer = smtpServer;
        this.smtpLogin = smtpLogin;
        this.smtpPassword = smtpPassword;
        this.supportEmail = supportEmail;
        this.templatesPath = templatesPath;
        this.copies = copies;
        this.reports = reports;
        this.defaultLeverage = defaultLeverage;
        this.defaultDeposit = defaultDeposit;
        this.maxSymbols = maxSymbols;
        this.currency = currency;
        this.credit = credit;
        this.marginCall = marginCall;
        this.marginMode = marginMode;
        this.marginStopout = marginStopout;
        this.interestRate = interestRate;
        this.useSwap = useSwap;
        this.news = news;
        this.rights = rights;
        this.checkIePrices = checkIePrices;
        this.maxPositions = maxPositions;
        this.closeReopen = closeReopen;
        this.hedgeProhibited = hedgeProhibited;
        this.closeFifo = closeFifo;
        this.hedgeLargeLeg = hedgeLargeLeg;
        this.marginType = marginType;
        this.archivePeriod = archivePeriod;
        this.archiveMaxBalance = archiveMaxBalance;
        this.stopoutSkipHedged = stopoutSkipHedged;
        this.archivePendingPeriod = archivePendingPeriod;
        this.newsLanguages = newsLanguages;
        this.securities = securities;
        this.symbols = symbols;
    }
}



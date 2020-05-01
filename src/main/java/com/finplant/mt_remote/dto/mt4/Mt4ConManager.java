package com.finplant.mt_remote.dto.mt4;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class Mt4ConManager {

    @JsonProperty("login")
    private Integer login;

    @JsonProperty("name")
    private String name;

    @JsonProperty("manager")
    private Boolean manager;

    @JsonProperty("money")
    private Boolean money;

    @JsonProperty("online")
    private Boolean online;

    @JsonProperty("riskman")
    private Boolean riskman;

    @JsonProperty("broker")
    private Boolean broker;

    @JsonProperty("admin")
    private Boolean admin;

    @JsonProperty("logs")
    private Boolean logs;

    @JsonProperty("reports")
    private Boolean reports;

    @JsonProperty("trades")
    private Boolean trades;

    @JsonProperty("market_watch")
    private Boolean marketWatch;

    @JsonProperty("email")
    private Boolean email;

    @JsonProperty("user_details")
    private Boolean userDetails;

    @JsonProperty("see_trades")
    private Boolean seeTrades;

    @JsonProperty("news")
    private Boolean news;

    @JsonProperty("plugins")
    private Boolean plugins;

    @JsonProperty("server_reports")
    private Boolean serverReports;

    @JsonProperty("techsupport")
    private Boolean techSupport;

    @JsonProperty("market")
    private Boolean market;

    @JsonProperty("notifications")
    private Boolean notifications;

    @JsonProperty("ip_filter")
    private Boolean ipFilter;

    @JsonProperty("ip_from")
    private String ipFrom;

    @JsonProperty("ip_to")
    private String ipTo;

    @JsonProperty("mailbox")
    private String mailbox;

    @JsonProperty("groups")
    private List<String> groups;

    @JsonProperty("info_depth")
    private Integer infoDepth;

    @JsonProperty("securities")
    private Map<String, Mt4ConManagerSecurity> securities;

    @Builder(toBuilder = true)
    public Mt4ConManager(Integer login, String name, Boolean manager, Boolean money, Boolean online, Boolean riskman,
                         Boolean broker, Boolean admin, Boolean logs, Boolean reports, Boolean trades,
                         Boolean marketWatch, Boolean email, Boolean userDetails, Boolean seeTrades, Boolean news,
                         Boolean plugins, Boolean serverReports, Boolean techSupport, Boolean market,
                         Boolean notifications, Boolean ipFilter, String ipFrom, String ipTo, String mailbox,
                         List<String> groups, Integer infoDepth, Map<String, Mt4ConManagerSecurity> securities) {
        this.login = login;
        this.name = name;
        this.manager = manager;
        this.money = money;
        this.online = online;
        this.riskman = riskman;
        this.broker = broker;
        this.admin = admin;
        this.logs = logs;
        this.reports = reports;
        this.trades = trades;
        this.marketWatch = marketWatch;
        this.email = email;
        this.userDetails = userDetails;
        this.seeTrades = seeTrades;
        this.news = news;
        this.plugins = plugins;
        this.serverReports = serverReports;
        this.techSupport = techSupport;
        this.market = market;
        this.notifications = notifications;
        this.ipFilter = ipFilter;
        this.ipFrom = ipFrom;
        this.ipTo = ipTo;
        this.mailbox = mailbox;
        this.groups = groups;
        this.infoDepth = infoDepth;
        this.securities = securities;
    }
}

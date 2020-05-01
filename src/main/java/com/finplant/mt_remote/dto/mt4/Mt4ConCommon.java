package com.finplant.mt_remote.dto.mt4;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Mt4ConCommon {

    public enum TypeOfDemo {
        DISABLED,
        PROLONG,
        FIXED;
    }

    public enum StatementMode {
        END_DAY,
        START_DAY
    }

    public enum LiveUpdateMode {
        NO,
        ALL,
        NO_SERVER
    }

    public enum MonthlyStateMode {
        END_MONTH,
        START_MONTH
    }

    public enum RolloverChargingMode {
        ROLLOVER_NORMAL,
        ROLLOVER_REOPEN_BY_CLOSE_PRICE,
        ROLLOVER_REOPEN_BY_BID
    }

    public enum StopReason {
        NONE,
        RESTART,
        SHUTDOWN,
        LIVEUPDATE
    }

    @Getter
    @JsonProperty("adapters")
    private String adapters;

    @Getter
    @JsonProperty("optimization_last_time")
    private LocalDateTime optimizationLastTime;

    @Getter
    @JsonProperty("server_version")
    private Integer serverVersion;

    @Getter
    @JsonProperty("server_build")
    private Integer serverBuild;

    @Getter
    @JsonProperty("last_order")
    private Integer lastOrder;

    @Getter
    @JsonProperty("last_login")
    private Integer lastLogin;

    @Getter
    @JsonProperty("lost_commission_login")
    private Integer lostCommissionLogin;

    @Getter
    @JsonProperty("owner")
    private String owner;

    @JsonProperty("name")
    private String name;

    @JsonProperty("address")
    private String address;

    @JsonProperty("port")
    private Integer port;

    @JsonProperty("timeout")
    private Integer timeout;

    @JsonProperty("type_of_demo")
    private TypeOfDemo typeOfDemo;

    @JsonProperty("time_of_demo_days")
    private Integer timeOfDemoDays;

    @JsonProperty("daylight_correction")
    private Boolean daylightCorrection;

    @JsonProperty("timezone")
    private ZoneOffset timezone;

    @JsonProperty("timesync_server")
    private String timesyncServer;

    @JsonProperty("feeder_timeout_seconds")
    private Integer feederTimeoutSeconds;

    @JsonProperty("keep_emails_days")
    private Integer keepEmailsDays;

    @JsonProperty("keep_ticks_days")
    private Integer keepTicksDays;

    @JsonProperty("statement_weekend")
    private Boolean statementWeekend;

    @JsonProperty("end_of_day_hour")
    private Integer endOfDayHour;

    @JsonProperty("end_of_day_minute")
    private Integer endOfDayMinute;

    @JsonProperty("optimization_time_minutes")
    private String optimizationTimeMinutes;

    @JsonProperty("overmonth_last_month")
    private Month overmonthLastMonth;

    @JsonProperty("antiflood")
    private Boolean antiflood;

    @JsonProperty("antiflood_max_connections")
    private Integer antifloodMaxConnections;

    @JsonProperty("bind_adresses")
    private List<String> bindAdresses;

    @JsonProperty("web_adresses")
    private List<String> webAdresses;

    @JsonProperty("statement_mode")
    private StatementMode statementMode;

    @JsonProperty("liveupdate_mode")
    private LiveUpdateMode liveupdateMode;

    @JsonProperty("last_activate_time")
    private Integer lastActivateTime;

    @JsonProperty("stop_last_time")
    private Integer stopLastTime;

    @JsonProperty("monthly_state_mode")
    private MonthlyStateMode monthlyStateMode;

    @JsonProperty("rollovers_mode")
    private RolloverChargingMode rolloversMode;

    @JsonProperty("path_database")
    private String pathDatabase;

    @JsonProperty("path_history")
    private String pathHistory;

    @JsonProperty("path_log")
    private String pathLog;

    @JsonProperty("overnight_last_day")
    private Integer overnightLastDay;

    @JsonProperty("overnight_last_time")
    private Integer overnightLastTime;

    @JsonProperty("overnight_prev_time")
    private Integer overnightPrevTime;

    @JsonProperty("stop_delay_seconds")
    private Integer stopDelaySeconds;

    @JsonProperty("stop_reason")
    private StopReason stopReason;

    @JsonProperty("account_url")
    private URI accountUrl;

    @Builder(toBuilder = true)
    public Mt4ConCommon(String owner, String name, String address, Integer port, Integer timeout, TypeOfDemo typeOfDemo,
                        Integer timeOfDemoDays, Boolean daylightCorrection, ZoneOffset timezone, String timesyncServer,
                        Integer feederTimeoutSeconds, Integer keepEmailsDays, Integer keepTicksDays,
                        Boolean statementWeekend, Integer endOfDayHour, Integer endOfDayMinute,
                        String optimizationTimeMinutes, Month overmonthLastMonth, Boolean antiflood,
                        Integer antifloodMaxConnections, List<String> bindAdresses,
                        List<String> webAdresses, StatementMode statementMode, LiveUpdateMode liveupdateMode,
                        Integer lastActivateTime, Integer stopLastTime, MonthlyStateMode monthlyStateMode,
                        RolloverChargingMode rolloversMode, String pathDatabase, String pathHistory, String pathLog,
                        Integer overnightLastDay, Integer overnightLastTime, Integer overnightPrevTime,
                        Integer stopDelaySeconds, StopReason stopReason, URI accountUrl) {
        this.owner = owner;
        this.name = name;
        this.address = address;
        this.port = port;
        this.timeout = timeout;
        this.typeOfDemo = typeOfDemo;
        this.timeOfDemoDays = timeOfDemoDays;
        this.daylightCorrection = daylightCorrection;
        this.timezone = timezone;
        this.timesyncServer = timesyncServer;
        this.feederTimeoutSeconds = feederTimeoutSeconds;
        this.keepEmailsDays = keepEmailsDays;
        this.keepTicksDays = keepTicksDays;
        this.statementWeekend = statementWeekend;
        this.endOfDayHour = endOfDayHour;
        this.endOfDayMinute = endOfDayMinute;
        this.optimizationTimeMinutes = optimizationTimeMinutes;
        this.overmonthLastMonth = overmonthLastMonth;
        this.antiflood = antiflood;
        this.antifloodMaxConnections = antifloodMaxConnections;
        this.bindAdresses = bindAdresses;
        this.webAdresses = webAdresses;
        this.statementMode = statementMode;
        this.liveupdateMode = liveupdateMode;
        this.lastActivateTime = lastActivateTime;
        this.stopLastTime = stopLastTime;
        this.monthlyStateMode = monthlyStateMode;
        this.rolloversMode = rolloversMode;
        this.pathDatabase = pathDatabase;
        this.pathHistory = pathHistory;
        this.pathLog = pathLog;
        this.overnightLastDay = overnightLastDay;
        this.overnightLastTime = overnightLastTime;
        this.overnightPrevTime = overnightPrevTime;
        this.stopDelaySeconds = stopDelaySeconds;
        this.stopReason = stopReason;
        this.accountUrl = accountUrl;
        adapters = null;
        optimizationLastTime = null;
        serverVersion = null;
        serverBuild = null;
        lastOrder = null;
        lastLogin = null;
        lostCommissionLogin = null;
    }
}
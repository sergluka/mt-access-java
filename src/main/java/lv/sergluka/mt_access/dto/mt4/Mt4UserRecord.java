package lv.sergluka.mt_access.dto.mt4;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Mt4UserRecord {

    @JsonProperty("login")
    private Integer login;

    @JsonProperty("group")
    private String group;

    @JsonProperty("enable")
    private Boolean enable;

    @JsonProperty("enable_change_password")
    private Boolean enableChangePassword;

    @JsonProperty("read_only")
    private Boolean readOnly;

    @JsonProperty("enable_otp")
    private Boolean enableOtp;

    @JsonProperty("password_phone")
    private String passwordPhone;

    @JsonProperty("name")
    private String name;

    @JsonProperty("country")
    private String country;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("zipcode")
    private String zipcode;

    @JsonProperty("address")
    private String address;

    @JsonProperty("lead_source")
    private String leadSource;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("id")
    private String id;

    @JsonProperty("status")
    private String status;

    @JsonProperty("leverage")
    private Integer leverage;

    @JsonProperty("agent_account")
    private Integer agentAccount;

    @JsonProperty("taxes")
    private BigDecimal taxes;

    @JsonProperty("send_reports")
    private Boolean sendReports;

    @JsonProperty("mqid")
    private Integer mqid;

    @JsonProperty("user_color")
    private Long userColor;

    @JsonProperty("api_data")
    private byte[] apiData;

    @Getter(AccessLevel.NONE)
    @Setter
    @JsonProperty("password")
    private String password;

    @Setter
    @JsonProperty("password_investor")
    private String passwordInvestor;

    @Getter
    @Setter(AccessLevel.NONE)
    @JsonProperty("last_date")
    private LocalDateTime lastDate;

    @Getter
    @Setter(AccessLevel.NONE)
    @JsonProperty("reg_date")
    private LocalDateTime registrationDate;

    @Getter
    @Setter(AccessLevel.NONE)
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @Getter
    @Setter(AccessLevel.NONE)
    @JsonProperty("last_ip")
    private String lastIp;

    @Getter
    @Setter(AccessLevel.NONE)
    @JsonProperty("prev_month_balance")
    private BigDecimal prevMonthBalance;

    @Getter
    @Setter(AccessLevel.NONE)
    @JsonProperty("prev_day_balance")
    private BigDecimal prevDayBalance;

    @Getter
    @Setter(AccessLevel.NONE)
    @JsonProperty("prev_month_equity")
    private BigDecimal prevMonthEquity;

    @Getter
    @Setter(AccessLevel.NONE)
    @JsonProperty("prev_day_equity")
    private BigDecimal prevDayEquity;

    @Getter
    @Setter(AccessLevel.NONE)
    @JsonProperty("interestrate")
    private BigDecimal interestrate;

    @Getter
    @Setter(AccessLevel.NONE)
    @JsonProperty("balance")
    private BigDecimal balance;

    @Getter
    @Setter(AccessLevel.NONE)
    @JsonProperty("credit")
    private BigDecimal credit;

    @Builder(toBuilder = true)
    public Mt4UserRecord(Integer login, String group, Boolean enable, Boolean enableChangePassword, Boolean readOnly,
                         Boolean enableOtp, String passwordPhone, String name, String country, String city,
                         String state, String zipcode, String address, String leadSource, String phone, String email,
                         String comment, String id, String status, Integer leverage, Integer agentAccount,
                         BigDecimal taxes, Boolean sendReports, Integer mqid,
                         Long userColor, byte[] apiData, String password, String passwordInvestor) {
        this.login = login;
        this.group = group;
        this.enable = enable;
        this.enableChangePassword = enableChangePassword;
        this.readOnly = readOnly;
        this.enableOtp = enableOtp;
        this.passwordPhone = passwordPhone;
        this.name = name;
        this.country = country;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.address = address;
        this.leadSource = leadSource;
        this.phone = phone;
        this.email = email;
        this.comment = comment;
        this.id = id;
        this.status = status;
        this.leverage = leverage;
        this.agentAccount = agentAccount;
        this.taxes = taxes;
        this.sendReports = sendReports;
        this.mqid = mqid;
        this.userColor = userColor;
        this.apiData = apiData;
        this.password = password;
        this.passwordInvestor = passwordInvestor;
    }
}

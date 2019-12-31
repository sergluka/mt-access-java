package com.finplant.mtm_client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserRecord {

    @JsonProperty("login")
    Integer login;
    @JsonProperty("group")
    String group;
    @JsonProperty("enable")
    Boolean enable;
    @JsonProperty("enable_change_password")
    Boolean enableChangePassword;
    @JsonProperty("read_only")
    Boolean readOnly;
    @JsonProperty("enable_otp")
    Boolean enableOtp;
    @JsonProperty("password_phone")
    String passwordPhone;
    @JsonProperty("name")
    String name;
    @JsonProperty("country")
    String country;
    @JsonProperty("city")
    String city;
    @JsonProperty("state")
    String state;
    @JsonProperty("zipcode")
    String zipcode;
    @JsonProperty("address")
    String address;
    @JsonProperty("lead_source")
    String leadSource;
    @JsonProperty("phone")
    String phone;
    @JsonProperty("email")
    String email;
    @JsonProperty("comment")
    String comment;
    @JsonProperty("id")
    String id;
    @JsonProperty("status")
    String status;
    @JsonProperty("leverage")
    Integer leverage;
    @JsonProperty("agent_account")
    Integer agentAccount;
    @JsonProperty("taxes")
    BigDecimal taxes;
    @JsonProperty("send_reports")
    Boolean sendReports;
    @JsonProperty("mqid")
    Integer mqid;
    @JsonProperty("user_color")
    Long userColor;
    @JsonProperty("api_data")
    byte[] apiData;

    @Setter
    @JsonProperty("password")
    String password;
    @Setter
    @JsonProperty("password_investor")
    String passwordInvestor;

    @Getter
    @JsonProperty("last_date")
    LocalDateTime lastDate;
    @Getter
    @JsonProperty("reg_date")
    LocalDateTime registrationDate;
    @Getter
    @JsonProperty("timestamp")
    LocalDateTime timestamp;
    @Getter
    @JsonProperty("last_ip")
    String lastIp;
    @Getter
    @JsonProperty("prev_month_balance")
    BigDecimal prevMonthBalance;
    @Getter
    @JsonProperty("prev_day_balance")
    BigDecimal prevDayBalance;
    @Getter
    @JsonProperty("prev_month_equity")
    BigDecimal prevMonthEquity;
    @Getter
    @JsonProperty("prev_day_equity")
    BigDecimal prevDayEquity;
    @Getter
    @JsonProperty("interestrate")
    BigDecimal interestrate;
    @Getter
    @JsonProperty("balance")
    BigDecimal balance;
    @Getter
    @JsonProperty("credit")
    BigDecimal credit;

    @Builder(toBuilder = true)
    public UserRecord(Integer login, String group, Boolean enable, Boolean enableChangePassword, Boolean readOnly,
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

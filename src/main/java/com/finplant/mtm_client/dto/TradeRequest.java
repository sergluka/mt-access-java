package com.finplant.mtm_client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Getter
public class TradeRequest {

    public enum Status {
        EMPTY("empty"),
        REQUEST("request"),
        LOCKED("locked"),
        ANSWERED("answered"),
        RESETED("reseted"),
        CANCELED("canceled");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("time")
    private LocalDateTime time;

    @JsonProperty("manager")
    private Integer manager;

    @JsonProperty("login")
    private Integer login;

    @JsonProperty("group")
    private String group;

    @JsonProperty("balance")
    private BigDecimal balance;

    @JsonProperty("credit")
    private BigDecimal credit;

    @JsonProperty("prices")
    private List<BigDecimal> prices;

    @JsonProperty("trade")
    private TradeTransaction trade;
}

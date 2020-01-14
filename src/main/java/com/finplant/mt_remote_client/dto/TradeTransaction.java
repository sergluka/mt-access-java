package com.finplant.mt_remote_client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@NoArgsConstructor
public class TradeTransaction {

    public enum Type {
        PRICES_GET(0),
        PRICES_REQUOTE(1),
        ORDER_IE_OPEN(64),
        ORDER_REQ_OPEN(65),
        ORDER_MK_OPEN(66),
        ORDER_PENDING_OPEN(67),

        ORDER_IE_CLOSE(68),
        ORDER_REQ_CLOSE(69),
        ORDER_MK_CLOSE(70),

        ORDER_MODIFY(71),
        ORDER_DELETE(72),
        ORDER_CLOSE_BY(73),
        ORDER_CLOSE_ALL(74);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        public static Type forCode(int code) {
            for (Type element : values()) {
                if (element.value == code) {
                    return element;
                }
            }
            throw new IllegalArgumentException(String.format(String.format("Unknown type: %d", code)));
        }

        @JsonCreator
        public static Type forValue(String v) {
            return Type.forCode(Integer.parseInt(v));
        }
    }

    @JsonProperty("type")
    private Type type;

    @JsonProperty("cmd")
    private TradeRecord.Command command;

    @JsonProperty("order")
    private Integer order;

    @JsonProperty("order_by")
    private Integer orderBy;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("volume")
    private BigDecimal volume;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("sl")
    private BigDecimal stopLoss;

    @JsonProperty("tp")
    private BigDecimal takeProfit;

    @JsonProperty("ie_deviation")
    private Integer ie_deviation;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("expiration")
    private LocalDateTime expiration;

    @JsonProperty("crc")
    private Integer crc;
}

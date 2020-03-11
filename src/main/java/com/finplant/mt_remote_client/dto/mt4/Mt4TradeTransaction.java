package com.finplant.mt_remote_client.dto.mt4;

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
public class Mt4TradeTransaction {

    public enum Type {
        PRICES_GET("prices_get"),
        PRICES_REQUOTE("prices_requote"),
        ORDER_IE_OPEN("order_ie_open"),
        ORDER_REQ_OPEN("order_req_open"),
        ORDER_MK_OPEN("order_mk_open"),
        ORDER_PENDING_OPEN("order_pending_open"),

        ORDER_IE_CLOSE("order_ie_close"),
        ORDER_REQ_CLOSE("order_req_close"),
        ORDER_MK_CLOSE("order_mk_close"),

        ORDER_MODIFY("order_modify"),
        ORDER_DELETE("order_delete"),
        ORDER_CLOSE_BY("order_close_by"),
        ORDER_CLOSE_ALL("order_close_all");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }

    public enum Flags {
        TT_FLAG_NONE("none"),
        TT_FLAG_SIGNAL("signal"),
        TT_FLAG_EXPERT("expert"),
        TT_FLAG_GATEWAY("gateway"),
        TT_FLAG_MOBILE("mobile"),
        TT_FLAG_WEB("web"),
        TT_FLAG_API("api");

        private final String value;

        Flags(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }

    @JsonProperty("type")
    private Type type;

    @JsonProperty("flags")
    private Flags flags;

    @JsonProperty("cmd")
    private Mt4TradeRecord.Command command;

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
    private Integer ieDeviation;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("expiration")
    private LocalDateTime expiration;

    @JsonProperty("crc")
    private Integer crc;
}

package lv.sergluka.mt_access.dto.mt4;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Mt4TradeRecord {

    public enum Command {
        BUY("buy"),
        SELL("sell"),
        BUY_LIMIT("buy_limit"),
        BUY_STOP("buy_stop"),
        SELL_LIMIT("sell_limit"),
        SELL_STOP("sell_stop"),
        BALANCE("balance"),
        CREDIT("credit");

        private final String value;

        Command(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }

    public enum Reason {

        CLIENT("client"),
        EXPERT("expert"),
        DEALER("dealer"),
        SIGNAL("signal"),
        GATEWAY("gateway");

        private final String value;

        Reason(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }

    @JsonProperty("order")
    private Integer order;
    @JsonProperty("login")
    private Integer login;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("digits")
    private Integer digits;
    @JsonProperty("cmd")
    private Command command;
    @JsonProperty("reason")
    private Reason reason;
    @JsonProperty("volume")
    private BigDecimal volume;
    @JsonProperty("open_time")
    private LocalDateTime openTime;
    @JsonProperty("close_time")
    private LocalDateTime closeTime;
    @JsonProperty("expiration")
    private LocalDateTime expiration;
    @JsonProperty("open_price")
    private BigDecimal openPrice;
    @JsonProperty("close_price")
    private BigDecimal closePrice;
    @JsonProperty("sl")
    private BigDecimal stopLoss;
    @JsonProperty("tp")
    private BigDecimal takeProfit;
    @JsonProperty("conv_rates")
    private List<BigDecimal> conversionRates;
    @JsonProperty("commission")
    private BigDecimal commission;
    @JsonProperty("commission_agent")
    private Integer commissionAgent;
    @JsonProperty("storage")
    private BigDecimal storage;
    @JsonProperty("profit")
    private BigDecimal profit;
    @JsonProperty("taxes")
    private BigDecimal taxes;
    @JsonProperty("magic")
    private Integer magic;
    @JsonProperty("comment")
    private String comment;
    @JsonProperty("margin_rate")
    private BigDecimal marginRate;
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    @JsonProperty("api_data")
    private byte[] apiData;
}

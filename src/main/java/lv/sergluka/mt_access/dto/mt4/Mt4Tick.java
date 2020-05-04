package lv.sergluka.mt_access.dto.mt4;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
public class Mt4Tick {

    private final String symbol;
    private final LocalDateTime time;
    private final BigDecimal bid;
    private final BigDecimal ask;

    @Builder(toBuilder = true)
    public Mt4Tick(@JsonProperty("symbol") String symbol,
                   @JsonProperty("time") LocalDateTime time,
                   @JsonProperty("bid") BigDecimal bid,
                   @JsonProperty("ask") BigDecimal ask) {
        this.symbol = symbol;
        this.time = time;
        this.bid = bid;
        this.ask = ask;
    }
}

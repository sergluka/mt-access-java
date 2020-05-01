package com.finplant.mt_remote.dto.mt4;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Mt4ConManagerSecurity {

    @JsonProperty("enable")
    private Boolean enable;

    @JsonProperty("minimum_lots")
    private BigDecimal minimumLots;

    @JsonProperty("maximum_lots")
    private BigDecimal maximumLots;

    @Builder(toBuilder = true)
    public Mt4ConManagerSecurity(Boolean enable, BigDecimal minimumLots, BigDecimal maximumLots) {
        this.enable = enable;
        this.minimumLots = minimumLots;
        this.maximumLots = maximumLots;
    }
}

package com.finplant.mt_remote_client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConManagerSecurity {

    @JsonProperty("enable")
    private Boolean enable;

    @JsonProperty("minimum_lots")
    private Integer minimumLots;

    @JsonProperty("maximum_lots")
    private Integer maximumLots;

    @Builder(toBuilder = true)
    public ConManagerSecurity(Boolean enable, Integer minimumLots, Integer maximumLots) {
        this.enable = enable;
        this.minimumLots = minimumLots;
        this.maximumLots = maximumLots;
    }
}

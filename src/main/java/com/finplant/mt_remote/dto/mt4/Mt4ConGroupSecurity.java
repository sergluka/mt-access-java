package com.finplant.mt_remote.dto.mt4;

import java.math.BigDecimal;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Mt4ConGroupSecurity {

    public enum ExecutionMode {
        MANUAL,
        AUTO,
        ACTIVITY
    }

    public enum CommissionUnits {
        MONEY,
        PIPS,
        PERCENT
    }

    public enum CommissionCalculationMode {
        PER_LOT,
        PER_DEAL
    }

    public enum TradeRight {
        DenyCloseBy("deny-close-by"),
        DenyMultipleCloseBy("deny-multiple-close-by");

        @JsonValue
        private final String value;

        TradeRight(String value) {
            this.value = value;
        }
    }

    public enum AutoCloseoutMode {
        NONE,
        HIHI,
        LOLO,
        HILO,
        LOHI,
        FIFO,
        LIFO,
        INTRDAY_FIFO
    }

    public enum FreeMarginMode {
        RECHECK,
        NONE,
    }

    @JsonProperty("show")
    private Boolean show;

    @JsonProperty("trade")
    private Boolean trade;

    @JsonProperty("execution_mode")
    private ExecutionMode executionMode;

    @JsonProperty("commission_base")
    private BigDecimal commissionBase;

    @JsonProperty("commission_type")
    private CommissionUnits commissionType;

    @JsonProperty("commission_lots_mode")
    private CommissionCalculationMode commissionLotsMode;

    @JsonProperty("commission_agent")
    private BigDecimal commissionAgent;

    @JsonProperty("commission_agent_type")
    private CommissionUnits commissionAgentType;

    @JsonProperty("spread_diff")
    private Integer spreadDiff;

    @JsonProperty("lot_min")
    private BigDecimal lotMin;

    @JsonProperty("lot_max")
    private BigDecimal lotMax;

    @JsonProperty("lot_step")
    private BigDecimal lotStep;

    @JsonProperty("ie_deviation")
    private Integer ieDeviation;

    @JsonProperty("request_confirmation")
    private Boolean requestConfirmation;

    @JsonProperty("trade_rights")
    private Set<TradeRight> tradeRights;

    @JsonProperty("ie_quick_mode")
    private Boolean ieQuickMode;

    @JsonProperty("auto_closeout_mode")
    private AutoCloseoutMode autoCloseoutMode;

    @JsonProperty("commission_tax")
    private BigDecimal commissionTax;

    @JsonProperty("commission_agent_mode")
    private CommissionCalculationMode commissionAgentMode;

    @JsonProperty("free_margin_mode")
    private FreeMarginMode freeMarginMode;

    @Builder(toBuilder = true)
    public Mt4ConGroupSecurity(Boolean show, Boolean trade,
                               ExecutionMode executionMode, BigDecimal commissionBase,
                               CommissionUnits commissionType,
                               CommissionCalculationMode commissionLotsMode, BigDecimal commissionAgent,
                               CommissionUnits commissionAgentType, Integer spreadDiff, BigDecimal lotMin,
                               BigDecimal lotMax, BigDecimal lotStep, Integer ieDeviation,
                               Boolean requestConfirmation,
                               Set<TradeRight> tradeRights, Boolean ieQuickMode,
                               AutoCloseoutMode autoCloseoutMode, BigDecimal commissionTax,
                               CommissionCalculationMode commissionAgentMode,
                               FreeMarginMode freeMarginMode) {
        this.show = show;
        this.trade = trade;
        this.executionMode = executionMode;
        this.commissionBase = commissionBase;
        this.commissionType = commissionType;
        this.commissionLotsMode = commissionLotsMode;
        this.commissionAgent = commissionAgent;
        this.commissionAgentType = commissionAgentType;
        this.spreadDiff = spreadDiff;
        this.lotMin = lotMin;
        this.lotMax = lotMax;
        this.lotStep = lotStep;
        this.ieDeviation = ieDeviation;
        this.requestConfirmation = requestConfirmation;
        this.tradeRights = tradeRights;
        this.ieQuickMode = ieQuickMode;
        this.autoCloseoutMode = autoCloseoutMode;
        this.commissionTax = commissionTax;
        this.commissionAgentMode = commissionAgentMode;
        this.freeMarginMode = freeMarginMode;
    }
}


package com.finplant.mt_remote_client.procedures;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;
import com.finplant.mt_remote_client.RpcClient;
import com.finplant.mt_remote_client.dto.mt4.Mt4TradeRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DealingProcedures {

    public enum ConfirmMode {
        NORMAL("normal"),
        ADD_PRICES("add_prices"),
        PACKET("packet");

        private final String value;

        ConfirmMode(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }

    private final RpcClient client;

    public DealingProcedures(RpcClient client) {
        this.client = client;
    }

    public Mono<Void> confirm(int request_id, BigDecimal bid, BigDecimal ask, ConfirmMode mode) {
        return client.call("request.confirm",
                           Map.of("request_id", request_id, "bid", bid, "ask", ask, "mode", mode));
    }

    public Mono<Void> requote(int request_id, BigDecimal bid, BigDecimal ask) {
        return client.call("request.requote", Map.of("request_id", request_id, "bid", bid, "ask", ask));
    }

    public Mono<Void> reject(Integer requestId) {
        return client.call("request.reject", Map.of("request_id", requestId));
    }

    public Mono<Void> reset(Integer requestId) {
        return client.call("request.reset", Map.of("request_id", requestId));
    }

    public Flux<Mt4TradeRequest> listen() {
        return client.subscribe("trade_request", Mt4TradeRequest.class);
    }
}

package com.finplant.mt_remote_client.procedures;

import com.fasterxml.jackson.annotation.JsonValue;
import com.finplant.mt_remote_client.RpcClient;
import com.finplant.mt_remote_client.dto.mt4.Mt4TradeRequest;
import lombok.val;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;

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

    public Mono<Void> confirm(int requestId, BigDecimal bid, BigDecimal ask, ConfirmMode mode) {
        val params = new HashMap<String, Object>();
        params.put("request_id", requestId);
        params.put("bid", bid);
        params.put("ask", ask);
        params.put("mode", mode);
        return client.call("request.confirm", params);
    }

    public Mono<Void> requote(int requestId, BigDecimal bid, BigDecimal ask) {
        val params = new HashMap<String, Object>();
        params.put("request_id", requestId);
        params.put("bid", bid);
        params.put("ask", ask);
        return client.call("request.requote", params);
    }

    public Mono<Void> reject(Integer requestId) {
        val params = new HashMap<String, Object>();
        params.put("request_id", requestId);
        return client.call("request.reject", params);
    }

    public Mono<Void> reset(Integer requestId) {
        val params = new HashMap<String, Object>();
        params.put("request_id", requestId);
        return client.call("request.reset", params);
    }

    public Flux<Mt4TradeRequest> listen() {
        return client.subscribe("trade_request", Mt4TradeRequest.class);
    }
}

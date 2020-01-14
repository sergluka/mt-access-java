package com.finplant.mt_remote_client.procedures;

import java.math.BigDecimal;
import java.util.Map;

import com.finplant.mt_remote_client.RpcClient;
import com.finplant.mt_remote_client.dto.TradeRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DealingProcedures {

    public enum ConfirmMode {
        NORMAL,
        ADD_PRICES,
        PACKET
    }

    private final RpcClient client;

    public DealingProcedures(RpcClient client) {
        this.client = client;
    }

    public Mono<Void> confirm(int request_id, BigDecimal bid, BigDecimal ask, ConfirmMode mode) {
        return client.call("request.confirm",
                           Map.of("request_id", request_id, "bid", bid, "ask", ask, "mode", mode.ordinal()));
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

    public Flux<TradeRequest> listen() {
        return client.subscribe("trade_request", TradeRequest.class);
    }
}

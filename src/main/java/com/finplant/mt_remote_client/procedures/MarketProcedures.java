package com.finplant.mt_remote_client.procedures;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.finplant.mt_remote_client.RpcClient;
import com.finplant.mt_remote_client.dto.mt4.Mt4Tick;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MarketProcedures {

    private final RpcClient client;

    public MarketProcedures(RpcClient client) {
        this.client = client;
    }

    public Mono<Void> add(String symbol, BigDecimal bid, BigDecimal ask) {
        return client.call("tick.add", Map.of("symbol", symbol, "bid", bid, "ask", ask));
    }

    public Flux<Mt4Tick> get(String symbol) {
        return client.call("ticks.get", Map.of("symbol", symbol), new TypeReference<List<Mt4Tick>>() {})
                     .flatMapMany(Flux::fromIterable);
    }

    public Flux<Mt4Tick> listen() {
        return client.subscribe("tick", Mt4Tick.class);
    }
}

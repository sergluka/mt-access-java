package com.finplant.mt_remote_client.procedures;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.finplant.mt_remote_client.RpcClient;
import com.finplant.mt_remote_client.dto.mt4.Mt4Tick;

import lombok.val;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MarketProcedures {

    private final RpcClient client;

    public MarketProcedures(RpcClient client) {
        this.client = client;
    }

    public Mono<Void> add(String symbol, BigDecimal bid, BigDecimal ask) {
        val params = new HashMap<String, Object>();
        params.put("symbol", symbol);
        return client.call("ticks.get", params);
    }

    public Flux<Mt4Tick> get(String symbol) {
        val params = new HashMap<String, Object>();
        params.put("symbol", symbol);
        return client.call("ticks.get", params, new TypeReference<List<Mt4Tick>>() {}).flatMapMany(Flux::fromIterable);
    }

    public Flux<Mt4Tick> listen() {
        return client.subscribe("tick", Mt4Tick.class);
    }
}

package com.finplant.mt_remote_client.procedures;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.finplant.mt_remote_client.RpcClient;
import com.finplant.mt_remote_client.dto.Tick;

import reactor.core.publisher.Flux;

public class MarketProcedures {

    private final RpcClient client;

    public MarketProcedures(RpcClient client) {
        this.client = client;
    }

    public Flux<Tick> ticksGet(String symbol) {
        return client.call("ticks.get", Map.of("symbol", symbol), new TypeReference<List<Tick>>() {})
                     .flatMapMany(Flux::fromIterable);
    }
}

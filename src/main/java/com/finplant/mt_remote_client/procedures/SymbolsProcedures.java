package com.finplant.mt_remote_client.procedures;

import com.finplant.mt_remote_client.RpcClient;
import com.finplant.mt_remote_client.dto.mt4.Mt4Tick;
import reactor.core.publisher.Mono;

import java.util.Map;

public class SymbolsProcedures {

    private final RpcClient client;

    public SymbolsProcedures(RpcClient client) {
        this.client = client;
    }

    public Mono<Mt4Tick> show(String symbol) {
        return client.call("symbol.show", Map.of("symbol", symbol), Mt4Tick.class);
    }

    public Mono<Mt4Tick> hide(String symbol) {
        return client.call("symbol.hide", Map.of("symbol", symbol), Mt4Tick.class);
    }
}

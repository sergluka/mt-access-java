package com.finplant.mt_remote_client.procedures;

import com.finplant.mt_remote_client.RpcClient;
import lombok.val;
import reactor.core.publisher.Mono;

import java.util.HashMap;

public class SymbolsProcedures {
    private final RpcClient client;

    public SymbolsProcedures(RpcClient client) {
        this.client = client;
    }

    public Mono<Void> show(String symbol) {
        val params = new HashMap<String, Object>();
        params.put("symbol", symbol);
        return client.call("symbol.show", params);
    }

    public Mono<Void> hide(String symbol) {
        val params = new HashMap<String, Object>();
        params.put("symbol", symbol);
        return client.call("symbol.hide", params);
    }
}

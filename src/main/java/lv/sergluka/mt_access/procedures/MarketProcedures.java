package lv.sergluka.mt_access.procedures;

import com.fasterxml.jackson.core.type.TypeReference;
import lv.sergluka.mt_access.RpcClient;
import lv.sergluka.mt_access.dto.mt4.Mt4Tick;
import lombok.val;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class MarketProcedures {

    private final RpcClient client;

    public MarketProcedures(RpcClient client) {
        this.client = client;
    }

    public Mono<Void> add(String symbol, BigDecimal bid, BigDecimal ask) {
        val params = new HashMap<String, Object>();
        params.put("symbol", symbol);
        params.put("bid", bid);
        params.put("ask", ask);
        return client.call("tick.add", params);
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

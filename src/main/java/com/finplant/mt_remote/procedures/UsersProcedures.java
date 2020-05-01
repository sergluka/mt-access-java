package com.finplant.mt_remote.procedures;

import com.fasterxml.jackson.core.type.TypeReference;
import com.finplant.mt_remote.RpcClient;
import com.finplant.mt_remote.dto.mt4.Mt4UserRecord;

import lombok.val;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersProcedures {
    private final RpcClient client;

    public UsersProcedures(RpcClient client) {
        this.client = client;
    }

    public Mono<Integer> add(Mt4UserRecord user) {
        return client.call("user.add", user, Map.class).map(response -> (Integer) response.get("login"));
    }

    public Mono<Void> set(Mt4UserRecord user) {
        return client.call("user.set", user);
    }

    public Flux<Mt4UserRecord> get(List<Integer> logins) {
        val params = new HashMap<String, Object>();
        params.put("logins", logins);
        return client.call("users.get", params, new TypeReference<List<Mt4UserRecord>>() {})
                     .flatMapMany(Flux::fromIterable);
    }

    public Flux<Mt4UserRecord> getAll() {
        return client.call("users.get.all", new TypeReference<List<Mt4UserRecord>>() {})
                     .flatMapMany(Flux::fromIterable);
    }

    public Mono<Mt4UserRecord> get(Integer login) {
        return get(Collections.singletonList(login)).single();
    }

    public Mono<Void> delete(List<Integer> logins) {
        val params = new HashMap<String, Object>();
        params.put("logins", logins);
        return client.call("users.del", params);
    }

    public Mono<Void> delete(Integer login) {
        return delete(Collections.singletonList(login));
    }

    public Flux<Mt4UserRecord> listen() {
        return client.subscribe("user", Mt4UserRecord.class);
    }
}

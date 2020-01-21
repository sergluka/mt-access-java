package com.finplant.mt_remote_client.procedures;

import com.fasterxml.jackson.core.type.TypeReference;
import com.finplant.mt_remote_client.RpcClient;
import com.finplant.mt_remote_client.dto.mt4.Mt4UserRecord;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        return client.call("users.get", Map.of("logins", logins), new TypeReference<List<Mt4UserRecord>>() {})
                     .flatMapMany(Flux::fromIterable);
    }

    public Flux<Mt4UserRecord> getAll() {
        return client.call("users.get.all", new TypeReference<List<Mt4UserRecord>>() {}).flatMapMany(Flux::fromIterable);
    }

    public Mono<Mt4UserRecord> get(Integer login) {
        return get(List.of(login)).single();
    }

    public Mono<Void> delete(List<Integer> logins) {
        return client.call("users.del", Map.of("logins", logins));
    }

    public Mono<Void> delete(Integer login) {
        return delete(List.of(login));
    }

    public Flux<Mt4UserRecord> listen() {
        return client.subscribe("user", Mt4UserRecord.class);
    }
}

package com.finplant.mtm_client.procedures;

import com.fasterxml.jackson.core.type.TypeReference;
import com.finplant.mtm_client.RpcClient;
import com.finplant.mtm_client.dto.UserRecord;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public class UsersProcedures {
    private final RpcClient client;

    public UsersProcedures(RpcClient client) {
        this.client = client;
    }

    public Mono<Integer> add(UserRecord user) {
        return client.call("user.add", user, Map.class).map(response -> (Integer) response.get("login"));
    }

    public Mono<Void> set(UserRecord user) {
        return client.call("user.set", user);
    }

    public Flux<UserRecord> get(List<Integer> logins) {
        return client.call("users.get", Map.of("logins", logins), new TypeReference<List<UserRecord>>() {})
                     .flatMapMany(Flux::fromIterable);
    }

    public Flux<UserRecord> getAll() {
        return client.call("users.get.all", new TypeReference<List<UserRecord>>() {}).flatMapMany(Flux::fromIterable);
    }

    public Mono<UserRecord> get(Integer login) {
        return get(List.of(login)).single();
    }

    public Mono<Void> delete(List<Integer> logins) {
        return client.call("users.del", Map.of("logins", logins));
    }

    public Mono<Void> delete(Integer login) {
        return delete(List.of(login));
    }

    public Flux<UserRecord> listen() {
        return client.subscribe("user", UserRecord.class);
    }
}

package com.finplant.mtm_client.requests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.finplant.mtm_client.RpcClient;
import com.finplant.mtm_client.dto.UserRecord;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public class UserProcedures {
    private final RpcClient client;

    public UserProcedures(RpcClient client) {
        this.client = client;
    }

    public Mono<Integer> add(UserRecord user) {
        return client.request("user.add", user, Map.class).map(response -> (Integer) response.get("login"));
    }

    public Mono<Void> set(UserRecord user) {
        return client.request("user.set", user);
    }

    public Flux<UserRecord> get(List<Integer> logins) {
        return client.request("users.get", Map.of("logins", logins), new TypeReference<List<UserRecord>>() {})
                     .flatMapMany(Flux::fromIterable);
    }

    public Mono<UserRecord> get(Integer login) {
        return get(List.of(login)).single();
    }

    public Mono<Void> del(List<Integer> logins) {
        return client.request("users.del", Map.of("logins", logins));
    }

    public Mono<Void> del(Integer login) {
        return del(List.of(login));
    }

    public Flux<UserRecord> subscribe() {
        return client.subscribe("user", UserRecord.class);
    }
}

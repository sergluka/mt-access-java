package com.finplant.mtm_client.procedures;

import java.util.Map;

import com.finplant.mtm_client.RpcClient;
import com.finplant.mtm_client.dto.ConCommon;
import com.finplant.mtm_client.dto.ConGroup;
import com.finplant.mtm_client.dto.ConManager;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ConfigProcedures {

    private final ConfigCommonProcedures configCommonProcedures;
    private final ConfigGroupsProcedures configGroupsProcedures;
    private final ConfigManagersProcedures configManagersProcedures;

    public ConfigProcedures(RpcClient client) {
        configCommonProcedures = new ConfigCommonProcedures(client);
        configGroupsProcedures = new ConfigGroupsProcedures(client);
        configManagersProcedures = new ConfigManagersProcedures(client);
    }

    public ConfigCommonProcedures common() {
        return configCommonProcedures;
    }

    public ConfigGroupsProcedures groups() {
        return configGroupsProcedures;
    }

    public ConfigManagersProcedures managers() {
        return configManagersProcedures;
    }

    private static class ConfigCommonProcedures {
        private final RpcClient client;

        public ConfigCommonProcedures(RpcClient client) {
            this.client = client;
        }

        public Mono<ConCommon> get() {
            return client.call("config.common.get", ConCommon.class);
        }

        public Mono<Void> set(ConCommon common) {
            return client.call("config.common.set", common);
        }
    }

    private static class ConfigGroupsProcedures {
        private final RpcClient client;

        public ConfigGroupsProcedures(RpcClient client) {
            this.client = client;
        }

        public Mono<Void> add(ConGroup group) {
            return client.call("config.group.add", group);
        }

        public Mono<ConGroup> get(String group) {
            return client.call("config.group.get", Map.of("group", group), ConGroup.class);
        }

        public Mono<Void> set(ConGroup group) {
            return client.call("config.group.set", group);
        }

        public Mono<Void> delete(String group) {
            return client.call("config.group.del", Map.of("group", group));
        }

        public Flux<ConGroup> subscribe() {
            return client.subscribe("group", ConGroup.class);
        }
    }

    private static class ConfigManagersProcedures {
        private final RpcClient client;

        public ConfigManagersProcedures(RpcClient client) {
            this.client = client;
        }

        public Mono<Void> add(ConManager manager) {
            return client.call("config.manager.set", manager);
        }

        public Mono<ConManager> get(Integer login) {
            return client.call("config.manager.get", Map.of("login", login), ConManager.class);
        }

        public Mono<Void> delete(Integer login) {
            return client.call("config.manager.del", Map.of("login", login));
        }
    }
}

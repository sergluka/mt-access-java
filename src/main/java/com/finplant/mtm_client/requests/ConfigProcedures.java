package com.finplant.mtm_client.requests;

import java.util.Map;

import com.finplant.mtm_client.RpcClient;
import com.finplant.mtm_client.dto.ConCommon;
import com.finplant.mtm_client.dto.ConGroup;

import com.finplant.mtm_client.dto.ConManager;
import reactor.core.publisher.Mono;

public class ConfigProcedures {

    private final ConfigCommonProcedures configCommonProcedures;
    private final ConfigGroupProcedures configGroupProcedures;
    private final ConfigManagerProcedures configManagerProcedures;

    public ConfigProcedures(RpcClient client) {
        configCommonProcedures = new ConfigCommonProcedures(client);
        configGroupProcedures = new ConfigGroupProcedures(client);
        configManagerProcedures = new ConfigManagerProcedures(client);
    }

    public ConfigCommonProcedures common() {
        return configCommonProcedures;
    }

    public ConfigGroupProcedures group() {
        return configGroupProcedures;
    }

    public ConfigManagerProcedures manager() {
        return configManagerProcedures;
    }

    private static class ConfigCommonProcedures {
        private final RpcClient client;

        public ConfigCommonProcedures(RpcClient client) {
            this.client = client;
        }

        public Mono<ConCommon> get() {
            return client.request("config.common.get", ConCommon.class);
        }

        public Mono<Void> set(ConCommon common) {
            return client.request("config.common.set", common);
        }
    }

    private static class ConfigGroupProcedures {
        private final RpcClient client;

        public ConfigGroupProcedures(RpcClient client) {
            this.client = client;
        }

        public Mono<Void> add(ConGroup group) {
            return client.request("config.group.add", group);
        }

        public Mono<ConGroup> get(String group) {
            return client.request("config.group.get", Map.of("group", group), ConGroup.class);
        }

        public Mono<Void> set(ConGroup group) {
            return client.request("config.group.set", group);
        }

        public Mono<Void> delete(String group) {
            return client.request("config.group.del", Map.of("group", group));
        }
    }

    private static class ConfigManagerProcedures {
        private final RpcClient client;

        public ConfigManagerProcedures(RpcClient client) {
            this.client = client;
        }

        public Mono<Void> add(ConManager manager) {
            return client.request("config.manager.set", manager);
        }

        public Mono<ConManager> get(Integer login) {
            return client.request("config.manager.get", Map.of("login", login), ConManager.class);
        }

        public Mono<Void> delete(Integer login) {
            return client.request("config.manager.del", Map.of("login", login));
        }
    }
}

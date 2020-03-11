package com.finplant.mt_remote_client.procedures;

import com.finplant.mt_remote_client.RpcClient;
import com.finplant.mt_remote_client.dto.mt4.Mt4ConCommon;
import com.finplant.mt_remote_client.dto.mt4.Mt4ConGroup;
import com.finplant.mt_remote_client.dto.mt4.Mt4ConManager;
import lombok.val;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;

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

        ConfigCommonProcedures(RpcClient client) {
            this.client = client;
        }

        public Mono<Mt4ConCommon> get() {
            return client.call("config.common.get", Mt4ConCommon.class);
        }

        public Mono<Void> set(Mt4ConCommon common) {
            return client.call("config.common.set", common);
        }
    }

    private static class ConfigGroupsProcedures {
        private final RpcClient client;

        ConfigGroupsProcedures(RpcClient client) {
            this.client = client;
        }

        public Mono<Void> add(Mt4ConGroup group) {
            return client.call("config.group.add", group);
        }

        public Mono<Mt4ConGroup> get(String group) {
            val params = new HashMap<String, Object>();
            params.put("group", group);
            return client.call("config.group.get", params, Mt4ConGroup.class);
        }

        public Mono<Void> set(Mt4ConGroup group) {
            return client.call("config.group.set", group);
        }

        public Mono<Void> delete(String group) {
            val params = new HashMap<String, Object>();
            params.put("group", group);
            return client.call("config.group.del", params);
        }

        public Flux<Mt4ConGroup> listen() {
            return client.subscribe("group", Mt4ConGroup.class);
        }
    }

    private static class ConfigManagersProcedures {
        private final RpcClient client;

        ConfigManagersProcedures(RpcClient client) {
            this.client = client;
        }

        public Mono<Void> add(Mt4ConManager manager) {
            return client.call("config.manager.set", manager);
        }

        public Mono<Mt4ConManager> get(Integer login) {
            val params = new HashMap<String, Object>();
            params.put("login", login);
            return client.call("config.manager.get", params, Mt4ConManager.class);
        }

        public Mono<Void> delete(Integer login) {
            val params = new HashMap<String, Object>();
            params.put("login", login);
            return client.call("config.manager.del", params);
        }
    }
}

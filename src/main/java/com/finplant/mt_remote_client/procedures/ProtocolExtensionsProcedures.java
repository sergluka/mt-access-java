package com.finplant.mt_remote_client.procedures;

import com.finplant.mt_remote_client.RpcClient;
import lombok.val;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.HashMap;

public class ProtocolExtensionsProcedures {
    private final RpcClient client;

    public ProtocolExtensionsProcedures(RpcClient client) {
        this.client = client;
    }

    public Mono<String> externalCommand(String command) {
        val params = new HashMap<String, Object>();
        params.put("command", command);
        params.put("encoding", "plain");
        return client.call("external.command", params, String.class);
    }

    public Mono<byte[]> externalCommand(byte[] command) {

        val base64 = Base64.getEncoder().encodeToString(command);

        val params = new HashMap<String, Object>();
        params.put("command", base64);
        params.put("encoding", "base64");

        return client.call("external.command", params, String.class)
                     .map(response -> Base64.getDecoder().decode(response));
    }
}

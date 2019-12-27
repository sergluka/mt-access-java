package com.finplant.mtm_client.requests;

import com.finplant.mtm_client.RpcClient;
import lombok.val;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Map;

public class ProtocolExtensionsProcedures {
    private final RpcClient client;

    public ProtocolExtensionsProcedures(RpcClient client) {
        this.client = client;
    }

    public Mono<String> externalCommand(String command) {
        return client.request("external.command", Map.of("command", command, "encoding", "plain"), String.class);
    }

    public Mono<byte[]> externalCommand(byte[] command) {

        val base64 = Base64.getEncoder().encodeToString(command);

        return client.request("external.command", Map.of("command", base64, "encoding", "base64"), String.class)
                     .map(response -> Base64.getDecoder().decode(response));
    }
}

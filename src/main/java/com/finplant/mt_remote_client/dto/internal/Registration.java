package com.finplant.mt_remote_client.dto.internal;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class Registration {

    @JsonProperty(value = "server", required = true)
    String server;

    @JsonProperty(value = "login", required = true)
    Integer login;

    @JsonProperty(value = "password", required = true)
    String password;

    @JsonProperty(value = "reconnect_delay", required = true)
    Long reconnect_delay;

    public Registration(String server, Integer login, String password, Duration reconnect_delay) {
        this.server = server;
        this.login = login;
        this.password = password;
        this.reconnect_delay = reconnect_delay.toMillis();
    }
}

package com.finplant.mtm_client.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Response<T> {

    private final long id;
    private final T data;
    private final String error;

    public Response(@JsonProperty(value = "id", required = true) long id,
                    @JsonProperty("data") T data,
                    @JsonProperty("error") String error) {
        this.id = id;
        this.data = data;
        this.error = error;
    }
}

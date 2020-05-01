package com.finplant.mt_remote.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"id", "data", "error"})
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

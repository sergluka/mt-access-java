package com.finplant.mtm_client.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NonNull;

@Data
public class Request<T> {

    @JsonProperty("name")
    private final String name;

    @JsonProperty("id")
    private final long id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("data")
    private final T data;

    public Request(@NonNull String name, long id, T data) {
        this.name = name;
        this.id = id;
        this.data = data;
    }
}

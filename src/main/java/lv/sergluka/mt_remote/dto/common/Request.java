package lv.sergluka.mt_remote.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonPropertyOrder({"name", "id", "data"})
public class Request<T> {

    @JsonProperty("name")
    private final String name;

    @JsonProperty("id")
    private final String id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("data")
    private final T data;

    public Request(@NonNull String name, String id, T data) {
        this.name = name;
        this.id = id;
        this.data = data;
    }
}

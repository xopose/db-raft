package com.raft.server.database.database.new_db.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Setter
public class AnswerActionDTO {
    @NotNull
    private final String target;
    @NotNull
    private final String action;
    @NotNull
    private  final  Map<String, String> data;
    @NotNull
    private final HttpStatus statusCode;

    @JsonCreator
    AnswerActionDTO(@JsonProperty("target") String target,
                    @JsonProperty("action") String action,
                    @JsonProperty("data") Map<String, String> data) {
        this.target = target;
        this.action = action;
        this.data = data;
        this.statusCode = HttpStatus.OK;
    }

    AnswerActionDTO(String target, Map<String, String> data, HttpStatus statusCode) {
        this.target = target;
        this.data = data;
        this.statusCode = statusCode;
        this.action = null;
    }
}

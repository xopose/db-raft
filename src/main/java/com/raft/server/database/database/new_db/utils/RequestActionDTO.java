package com.raft.server.database.database.new_db.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import com.raft.server.database.database.new_db.Record;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

@Getter
public class RequestActionDTO {

    @NotNull
    private final String target;

    @NotNull
    private final String action;
    @Nullable
    private final String record;
    @NotNull
    private final String tableName;
    @Nullable
    private final Long recordId;
    @JsonCreator
    public RequestActionDTO(@JsonProperty("target") String target,
                            @JsonProperty("action") String action,
                            @JsonProperty("record") String record,
                            @JsonProperty("tableName") String tableName,
                            @JsonProperty("recordId") Long recordId
    ) {
        this.target = target;
        this.action = action;
        this.record = record;
        this.tableName = tableName;
        this.recordId = recordId;
    }
}

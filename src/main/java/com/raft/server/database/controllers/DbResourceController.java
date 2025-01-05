package com.raft.server.database.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.network.http.Http;
import com.raft.server.context.Context;
import com.raft.server.database.database.new_db.Database;
import com.raft.server.database.database.new_db.InMemoryDatabase;
import com.raft.server.database.database.new_db.Table;
import com.raft.server.database.database.new_db.exceptions.CantCreateDatabaseException;
import com.raft.server.database.database.new_db.exceptions.IncorrectCommandException;
import com.raft.server.database.database.new_db.exceptions.TableNotFoundException;
import com.raft.server.database.database.new_db.sql_processor.Starter;
import com.raft.server.database.database.new_db.utils.RequestActionDTO;
import com.raft.server.node.peers.Peer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.raft.server.node.State.LEADER;

@ComponentScan(basePackages = "com.raft.server.database")

@RestController
@RequestMapping(value = "/database",produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(tags="Database")
@RequiredArgsConstructor
@Slf4j
public class DbResourceController {
    private Database database;
    private Starter starter;

    private final Http http;

    private final Context context;
    @PostConstruct
    public void init() {
        this.database = new InMemoryDatabase();
        this.starter = new Starter(this.database, this.context, this.http);
    }
    @PostMapping("/execute")
    public ResponseEntity<String> index(@RequestBody Map<String, String> payload) throws CantCreateDatabaseException, TableNotFoundException, IncorrectCommandException {
        String command = payload.get("command");
        log.debug("New command" + command);

        if (command == null || command.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Command cannot be empty");
        }
        String result = starter.execute(command).toString();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/append")
    @ApiOperation(value = "Append to operations")
    public ResponseEntity<String> appendRequest(@Valid @RequestBody RequestActionDTO payload) throws Exception {
        if(context.getActive()){
            System.out.println("New replication command (JSON): " +
                    "\ntarget " + payload.getTarget() +
                    "\naction " + payload.getAction() +
                    "\nrecord " + payload.getRecord() +
                    "\ntableName " + payload.getTableName() +
                    "\nrecordId " + payload.getRecordId()
            );
            switch (payload.getAction()){
                case "CREATE" :
                    database.createTable(payload.getTableName());
                    break;
                case "INSERT" :
                    database.getTable(payload.getTableName()).addRecordFromJson(payload.getRecord());
                    break;
                case "DELETE" :
                    switch (payload.getTarget()) {
                        case "TABLE" -> database.deleteTable(payload.getTableName());
                        case "RECORD" -> {
                            if (payload.getRecordId() != null) {
                                database.getTable(payload.getTableName()).deleteRecord(payload.getRecordId());
                            }
                            else throw new IllegalArgumentException("id must be not null");
                        }
                    }
                    break;
                case "UPDATE" :
                    database.getTable(payload.getTableName()).updateRecordFromJson(payload.getRecordId() ,payload.getRecord());
                    break;
                default:
                    throw new IncorrectCommandException("Unknown target action: " + payload.getTarget());
            }
        }
        return ResponseEntity.ok("");
    }

    @GetMapping("/send_data/{peerId}")
    public void appendMissing(@PathVariable Long peerId) {
        Map<String, Table> tables = database.getAllTables();
        Set<String> tableNames = tables.keySet();
        for (String tableName : tableNames) {
            RequestActionDTO requestActionDTO = new RequestActionDTO("TABLE", "CREATE", null, tableName, null);
            http.callPost(peerId.toString(), Object.class, requestActionDTO, "database", "append");
            try {
                Table table = database.getTable(tableName);
                for(long id = 0; id < table.countRecords(); id++){
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonString = mapper.writeValueAsString(table.getRecord(id));
                    if(jsonString != null && !jsonString.equals("null")){
                        requestActionDTO = new RequestActionDTO("TABLE", "INSERT", jsonString, tableName, null);
                        http.callPost(peerId.toString(), Object.class, requestActionDTO, "database", "append");
                    }
                }
            } catch (TableNotFoundException | JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @PostMapping("/delete_all")
    public ResponseEntity<String> deleteAll() {
        this.database = new InMemoryDatabase();
        return ResponseEntity.ok("");
    }
}

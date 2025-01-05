package com.raft.server.database.database.new_db.sql_processor;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.network.http.Http;
import com.raft.server.context.Context;
import com.raft.server.database.database.new_db.Database;
import com.raft.server.database.database.new_db.Table;
import com.raft.server.database.database.new_db.exceptions.IncorrectCommandException;
import com.raft.server.database.database.new_db.exceptions.TableNotFoundException;
import com.raft.server.database.database.new_db.utils.InMemoryCriteria;
import com.raft.server.database.database.new_db.utils.QueryTokenParser;
import com.raft.server.database.database.new_db.utils.RequestActionDTO;
import com.raft.server.node.peers.Peer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.raft.server.database.database.helper.Helper.getInMemoryCriteria;
import static com.raft.server.database.database.new_db.utils.QueryTokenParser.parseQuery;

public class Delete {

    public static void deleteFrom(String[] tokens, Database database, Map<String, String> executionData, Context context, Http http) {
        List<Integer> peersIds = context.getPeers().stream().map(Peer::getId).collect(Collectors.toList());
        try {
            List<String> tokenList = Arrays.asList(tokens);
            QueryTokenParser result = parseQuery(tokenList);

            try{
                Table table = database.getTable(result.tableName());
                InMemoryCriteria criteria = getInMemoryCriteria(result);
                for (Object[] entry : table.queryRecords(criteria)) {
                    table.deleteRecord((long)entry[0]);
                    peersIds.stream()
                            .map(i -> {
                                RequestActionDTO requestActionDTO = new RequestActionDTO("RECORD", "DELETE", null, result.tableName(), (long)entry[0]);
                                return http.callPost(i.toString(), Object.class,
                                        requestActionDTO, "database", "append");
                            })
                            .collect(Collectors.toList());
                }
                executionData.put("success", "delete passed");
            } catch (TableNotFoundException e) {
                executionData.put("error", "Table " + result.tableName() + " does not exists");
            }
        } catch (IllegalArgumentException e) {
            executionData.put("error", e.getMessage());
        }
    }

    public static void deleteTable(String tableName, Database database, Context context, Http http) throws TableNotFoundException {
        List<Integer> peersIds = context.getPeers().stream().map(Peer::getId).collect(Collectors.toList());
        database.getTable(tableName);
        database.deleteTable(tableName);
        peersIds.stream()
                .map(i -> {
                    RequestActionDTO requestActionDTO = new RequestActionDTO("TABLE", "DELETE", null, tableName, null);
                    return http.callPost(i.toString(), Object.class,
                            requestActionDTO, "database", "append");


                })
                .collect(Collectors.toList());
    }

    public static Map<String, String> delete(String[] tokens, Database database, Context context, Http http) throws IncorrectCommandException, TableNotFoundException {
        Map<String, String> executionData = new HashMap<String, String>();
        String command = tokens[1].toUpperCase();
        switch (command) {
            case "TABLE":
                deleteTable(tokens[2], database, context, http);
                break;
            case "FROM":
                deleteFrom(tokens, database, executionData, context, http);
                break;
            default:
                executionData.put("error", "Unknown command: " + command);
        };
        return executionData;
    }
}

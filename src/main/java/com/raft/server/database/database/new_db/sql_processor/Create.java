package com.raft.server.database.database.new_db.sql_processor;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.network.http.Http;
import com.raft.server.context.Context;
import com.raft.server.database.database.new_db.Database;
import com.raft.server.database.database.new_db.exceptions.CantCreateDatabaseException;
import com.raft.server.database.database.new_db.exceptions.IncorrectCommandException;
import com.raft.server.database.database.new_db.exceptions.TableNotFoundException;
import com.raft.server.database.database.new_db.utils.RequestActionDTO;
import com.raft.server.node.peers.Peer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Create {

    public static Map<String, String> create(String[] tokens, Database database, Context context, Http http) throws CantCreateDatabaseException {
        Map<String, String> executionData = new HashMap<String, String>();
        List<Integer> peersIds = context.getPeers().stream().map(Peer::getId).collect(Collectors.toList());
        switch (tokens[1]){
            case ("table"):
                try{
                    database.getTable(tokens[2]);
                }
                catch (TableNotFoundException e){
                    database.createTable(tokens[2]);
                    peersIds.stream()
                            .map(i -> {
                                System.out.println(i.toString());
                                RequestActionDTO requestActionDTO = new RequestActionDTO("TABLE", "CREATE", null, tokens[2], null);
                                return http.callPost(i.toString(), Object.class,
                                        requestActionDTO, "database", "append");
                            })
                            .collect(Collectors.toList());
                }
                break;
            default:
                executionData.put("error", "Invalid command syntax: " + String.join(" ", tokens));
                return executionData;
        }
        return executionData;
    }
}

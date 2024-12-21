package com.raft.server.database.database.new_db.sql_processor;


import com.raft.server.database.database.new_db.Database;
import com.raft.server.database.database.new_db.exceptions.CantCreateDatabaseException;
import com.raft.server.database.database.new_db.exceptions.IncorrectCommandException;
import com.raft.server.database.database.new_db.exceptions.TableNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class Create {

    public static Map<String, String> create(String[] tokens, Database database) throws CantCreateDatabaseException {
        Map<String, String> executionData = new HashMap<String, String>();
        switch (tokens[1]){
            case ("table"):
                try{
                    database.getTable(tokens[2]);
                }
                catch (TableNotFoundException e){
                    database.createTable(tokens[2]);
                }
                break;
            default:
                executionData.put("error", "Invalid command syntax: " + String.join(" ", tokens));
                return executionData;
        }
        return executionData;
    }
}

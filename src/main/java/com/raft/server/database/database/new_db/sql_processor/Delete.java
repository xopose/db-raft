package com.raft.server.database.database.new_db.sql_processor;


import com.raft.server.database.database.new_db.Database;
import com.raft.server.database.database.new_db.Table;
import com.raft.server.database.database.new_db.exceptions.IncorrectCommandException;
import com.raft.server.database.database.new_db.exceptions.TableNotFoundException;
import com.raft.server.database.database.new_db.utils.InMemoryCriteria;
import com.raft.server.database.database.new_db.utils.QueryTokenParser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.raft.server.database.database.helper.Helper.getInMemoryCriteria;
import static com.raft.server.database.database.new_db.utils.QueryTokenParser.parseQuery;

public class Delete {
    public static void deleteFrom(String[] tokens, Database database, Map<String, String> executionData) {
        try {
            List<String> tokenList = Arrays.asList(tokens);
            QueryTokenParser result = parseQuery(tokenList);

            try{
                Table table = database.getTable(result.tableName());
                InMemoryCriteria criteria = getInMemoryCriteria(result);
                for (Object[] entry : table.queryRecords(criteria)) {
                    table.deleteRecord((long)entry[0]);
                }
                executionData.put("success", "delete passed");
            } catch (TableNotFoundException e) {
                executionData.put("error", "Table " + result.tableName() + " does not exists");
            }
        } catch (IllegalArgumentException e) {
            executionData.put("error", e.getMessage());
        }
    }

    public static void deleteTable(String tableName, Database database) throws TableNotFoundException {
            database.getTable(tableName);
            database.deleteTable(tableName);
    }

    public static Map<String, String> delete(String[] tokens, Database database) throws IncorrectCommandException, TableNotFoundException {
        Map<String, String> executionData = new HashMap<String, String>();
        String command = tokens[1].toUpperCase();
        switch (command) {
            case "TABLE":
                deleteTable(tokens[2], database);
                break;
            case "FROM":
                deleteFrom(tokens, database, executionData);
                break;
            default:
                executionData.put("error", "Unknown command: " + command);
        };
        return executionData;
    }
}

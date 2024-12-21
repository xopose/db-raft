package com.raft.server.database.database.new_db.sql_processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raft.server.database.database.new_db.Database;
import com.raft.server.database.database.new_db.Field;
import com.raft.server.database.database.new_db.Table;
import com.raft.server.database.database.new_db.exceptions.TableNotFoundException;
import com.raft.server.database.database.new_db.utils.InMemoryCriteria;
import com.raft.server.database.database.new_db.utils.QueryTokenParser;
import com.raft.server.database.database.new_db.Record;

import java.util.*;

import static com.raft.server.database.database.helper.Helper.getInMemoryCriteria;
import static com.raft.server.database.database.new_db.utils.QueryTokenParser.parseQuery;


public class Select {
    public static Map<String, String> select(String[] tokens, Database database) {
        Map<String, String> executionData = new HashMap<String, String>();
        try {
            List<String> tokenList = Arrays.asList(tokens);
            QueryTokenParser result = parseQuery(tokenList);
            try{
                Table table = database.getTable(result.tableName());
                InMemoryCriteria criteria = getInMemoryCriteria(result);
                ObjectMapper mapper = new ObjectMapper();
                List<List> entryList = new ArrayList<>();
                for (Object[] entry : table.queryRecords(criteria)) {
                    entryList.clear();
                    List<String> rowData = new ArrayList<>();
                    long id = (long)entry[0];
                    Record record = (Record)entry[1];
                    for(Field field: record.getFields().values()){
                        if(result.columns().contains(field.getName()) || result.columns().contains("*")){
                            rowData.add(field.getName() + ": " + field.getValue());
                        }
                    }
                    entryList.add(rowData);
                    executionData.put(String.valueOf(id), mapper.writeValueAsString(entryList));
                }
            } catch (TableNotFoundException e) {
                throw new TableNotFoundException("Table " + result.tableName() + " does not exists");
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } catch (IllegalArgumentException | TableNotFoundException e) {
            executionData.put("error", e.getMessage());
        }
        return executionData;
    }
}

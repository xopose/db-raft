package com.raft.server.database.database.new_db.sql_processor;

import com.raft.server.database.database.new_db.Database;
import com.raft.server.database.database.new_db.InMemoryRecord;
import com.raft.server.database.database.new_db.Table;
import com.raft.server.database.database.new_db.exceptions.TableNotFoundException;
import com.raft.server.database.database.new_db.Record;

import java.util.*;

public class Insert {
    public static Map<String, String> insert(String[] tokens, Database database) {
        Map<String, String> executionData = new HashMap<String, String>();
        try {
            List<String> tokenList = Arrays.asList(tokens);
            try{
                Table table = database.getTable(tokenList.get(2));
                if(tokenList.get(3).equalsIgnoreCase("values")){
                    List<String> values = tokenList.subList(4, tokenList.size());
                    List<List<String>> result = getValues(values);

                    if(!result.isEmpty()){
                        Record record = new InMemoryRecord();
                        for (List<String> value: result){
                            record.setField(value.get(0), value.get(1));
                        }
                        table.addRecord(record);
                    }
                }
                executionData.put("success", "insert passed");
            } catch (TableNotFoundException e){
                executionData.put("error", e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            executionData.put("error", e.getMessage());
        }
        return executionData;
    }

    public static List<List<String>> getValues(List<String> values) {
        List<List<String>> result = new ArrayList<>();
        for (int i = 0; i < values.size() - 1; i += 2) {
            String first = values.get(i).replaceAll("[(),]", "").trim();
            String second = values.get(i + 1).replaceAll("[(),]", "").trim();
            List<String> pair = List.of(first, second);
            result.add(pair);
        }
        return result;
    }
}

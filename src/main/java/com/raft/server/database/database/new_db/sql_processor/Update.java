package com.raft.server.database.database.new_db.sql_processor;

import com.raft.server.database.database.new_db.Database;
import com.raft.server.database.database.new_db.InMemoryRecord;
import com.raft.server.database.database.new_db.Record;
import com.raft.server.database.database.new_db.Table;
import com.raft.server.database.database.new_db.exceptions.TableNotFoundException;
import com.raft.server.database.database.new_db.utils.InMemoryCriteria;
import com.raft.server.database.database.new_db.utils.QueryTokenParser;

import java.util.*;
import java.util.regex.Pattern;

import static com.raft.server.database.database.helper.Helper.getInMemoryCriteria;


public class Update {
    public static Map<String, String> update(String[] tokens, Database database) {
        Map<String, String> executionData = new HashMap<String, String>();
        try {
            List<String> tokenList = Arrays.asList(tokens);
            Table table = database.getTable(tokenList.get(1));
            getWhereCondition(tokenList);
            InMemoryCriteria criteria = getInMemoryCriteria(
                    new QueryTokenParser(
                            null, null, getWhereCondition(tokenList)
                    ));
            List<List<String>> parsedValues = Insert.getValues(extractValues(tokenList));
            for (Object[] entry : table.queryRecords(criteria)) {
                if(!parsedValues.isEmpty()){
                    Record record = new InMemoryRecord();
                    for (List<String> value: parsedValues){
                        record.setField(value.get(0), value.get(1));
                    }
                    table.addRecordById((long)entry[0], record);
                }

            }
            executionData.put("success", "update passed");
        } catch (IllegalArgumentException | TableNotFoundException e) {
            executionData.put("error", e.getMessage());
        }
        return executionData;
    }

    public static List<String> getWhereCondition(List<String> tokenList) {
        List<String> whereConditions = new ArrayList<>();
        String query = String.join(" ", tokenList);
        if (Pattern.compile(Pattern.quote("where"), Pattern.CASE_INSENSITIVE).matcher(query).find()) {
            int whereIndex = -1;
            for (int i = 0; i < tokenList.size(); i++) {

                if (tokenList.get(i).equalsIgnoreCase("where")) {
                    whereIndex = i;
                    break;
                }
            }
            if (whereIndex != -1) {
                for (int i = whereIndex + 1; i < tokenList.size(); i++) {
                    String element = tokenList.get(i);
                    if (!element.equals(",") && !element.isEmpty()) {
                        whereConditions.add(element);
                    }
                }
            }
        }
        return whereConditions;
    }

    private static List<String> extractValues(List<String> queryArray) {
        int setIndex = -1;
        int whereIndex = queryArray.size();

        for (int i = 0; i < queryArray.size(); i++) {
            if (queryArray.get(i).equalsIgnoreCase("set")) {
                setIndex = i;
                break;
            }
        }

        for (int i = setIndex + 1; i < queryArray.size(); i++) {
            if (queryArray.get(i).equalsIgnoreCase("where")) {
                whereIndex = i;
                break;
            }
        }
        if (setIndex != -1 && setIndex + 1 < whereIndex) {
            return queryArray.subList(setIndex + 1, whereIndex);
        } else {
            return new ArrayList<>();
        }
    }
}

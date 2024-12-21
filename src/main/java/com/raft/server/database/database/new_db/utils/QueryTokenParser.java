package com.raft.server.database.database.new_db.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public record QueryTokenParser(List<String> columns, String tableName, List<String> whereConditions) {

    public static QueryTokenParser parseQuery(List<String> queryArray) {
        if (!queryArray.contains("FROM") && !queryArray.contains("from")) {
            throw new IllegalArgumentException("Отсутствует ключевое слово 'FROM'.");
        }

        int selectIndex = queryArray.indexOf("Select");
        int fromIndex = Math.max(queryArray.indexOf("FROM"), queryArray.indexOf("from"));
        List<String> columns = new ArrayList<>();

        for (int i = selectIndex + 1; i < fromIndex; i++) {
            String element = queryArray.get(i);
            if (!element.equals(",") && !element.isEmpty()) {
                columns.add(element.replaceAll(",$", ""));
            }
        }

        String tableName = queryArray.get(fromIndex + 1);

        List<String> whereConditions = new ArrayList<>();
        String query = String.join(" ", queryArray);

        if (Pattern.compile(Pattern.quote("where"), Pattern.CASE_INSENSITIVE).matcher(query).find()) {
            int whereIndex = -1;
            for (int i = 0; i < queryArray.size(); i++) {
                if (queryArray.get(i).equalsIgnoreCase("where")) {
                    whereIndex = i;
                    break;
                }
            }
            if (whereIndex != -1) {
                for (int i = whereIndex + 1; i < queryArray.size(); i++) {
                    String element = queryArray.get(i);
                    if (!element.equals(",") && !element.isEmpty()) {
                        whereConditions.add(element);
                    }
                }
            }
        }

        return new QueryTokenParser(columns, tableName, whereConditions);
    }
}

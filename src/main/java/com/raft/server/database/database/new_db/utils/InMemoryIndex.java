package com.raft.server.database.database.new_db.utils;


import com.raft.server.database.database.new_db.Field;
import com.raft.server.database.database.new_db.Record;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryIndex implements Index {
    private String fieldName;
    private Map<Object, Set<Record>> indexMap;

    public InMemoryIndex(String fieldName) {
        this.fieldName = fieldName;
        this.indexMap = new ConcurrentHashMap<>();
    }

    @Override
    public void indexRecord(Record record) {
        Field field = record.getField(fieldName);
        if (field != null) {
            Object key = field.getValue();
            indexMap.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(record);
        }
    }

    @Override
    public void removeRecord(Record record) {
        Field field = record.getField(fieldName);
        if (field != null) {
            Object key = field.getValue();
            Set<Record> records = indexMap.get(key);
            if (records != null) {
                records.remove(record);
                if (records.isEmpty()) {
                    indexMap.remove(key);
                }
            }
        }
    }

    @Override
    public Collection<Record> search(Object value) {
        return indexMap.getOrDefault(value, Collections.emptySet());
    }

    @Override
    public void updateRecord(Record oldRecord, Record newRecord) {
        removeRecord(oldRecord);
        indexRecord(newRecord);
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public void optimize() {
    }

    @Override
    public void drop() {
        indexMap.clear();
    }
}

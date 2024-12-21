package com.raft.server.database.database.new_db;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raft.server.database.database.new_db.exceptions.SerializationException;
import com.raft.server.database.database.new_db.utils.ComparisonCriteria;
import com.raft.server.database.database.new_db.utils.ComparisonResult;

import java.io.IOException;
import java.util.*;

public class InMemoryRecord implements Record {
    private Map<String, Field> fields;

    public InMemoryRecord() {
        this.fields = new HashMap<>();
    }

    public InMemoryRecord(Map<String, Field> fields) {
        this.fields = new HashMap<>(fields);
    }

    @Override
    public Field getField(String fieldName) {
        return fields.get(fieldName);
    }

    @Override
    public void setField(String fieldName, Object value) {
        Field field = fields.get(fieldName);
        if (field != null) {
            field.setValue(value);
        } else {
            field = new InMemoryField(fieldName, value);
            fields.put(fieldName, field);
        }
    }

    @Override
    public Map<String, Field> getFields() {
        return new HashMap<>(fields);
    }

    @Override
    public void updateFields(Map<String, Object> values) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            setField(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<String> getFieldNames() {
        return fields.keySet();
    }

    @Override
    public Collection<Field> getFieldValues() {
        return fields.values();
    }

    @Override
    public boolean containsField(String fieldName) {
        return fields.containsKey(fieldName);
    }

    @Override
    public boolean removeField(String fieldName) {
        return fields.remove(fieldName) != null;
    }

    @Override
    public Record clone() {
        return new InMemoryRecord(this.fields);
    }

    @Override
    public String serialize(List<String> recordFields) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> fieldMap = new HashMap<>();
            for (Map.Entry<String, Field> entry : fields.entrySet()) {
                if(recordFields.contains(entry.getKey()) || recordFields.isEmpty() || recordFields.contains("*")){
                    fieldMap.put(entry.getKey(), entry.getValue().getValue());
                }
            }
            return mapper.writeValueAsString(fieldMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public ComparisonResult compare(Record other, ComparisonCriteria criteria) {
        return criteria.compare(null, this, other);
    }

    public static Record deserialize(String data) throws SerializationException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> fieldMap = mapper.readValue(data, new TypeReference<Map<String, Object>>() {});
            InMemoryRecord record = new InMemoryRecord();
            for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
                record.setField(entry.getKey(), entry.getValue());
            }
            return record;
        } catch (IOException e) {
            throw new SerializationException("Deserialization failed", e);
        }
    }
}

package com.raft.server.database.database.new_db.deserializers;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raft.server.database.database.new_db.InMemoryRecord;
import com.raft.server.database.database.new_db.Record;
import java.io.IOException;
import java.util.Map;

public class RecordDeserializer extends JsonDeserializer<Record> {
    @Override
    public Record deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        Map<String, Object> fieldMap = mapper.readValue(p, new TypeReference<Map<String, Object>>() {});
        InMemoryRecord record = new InMemoryRecord();
        for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
            record.setField(entry.getKey(), entry.getValue());
        }
        return record;
    }
}

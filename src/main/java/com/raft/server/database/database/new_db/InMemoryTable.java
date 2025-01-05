package com.raft.server.database.database.new_db;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raft.server.database.database.new_db.exceptions.RecordNotFoundException;
import com.raft.server.database.database.new_db.utils.*;
import lombok.extern.slf4j.Slf4j;
import com.raft.server.database.database.new_db.Record;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
@Slf4j
public class InMemoryTable implements Table {
    private final Map<Long, Record> records = new ConcurrentHashMap<>();
    private final Map<String, Index> indexes = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final Stack<Long> freeIds = new Stack<>();

    private AtomicLong commitIndex = new AtomicLong(0);
    @Override
    public void addRecord(Record record) {
        records.put(nextRecordId(), record);
        indexes.values().forEach(index -> index.indexRecord(record));
        incrementCommitIndex();
        log.debug("Table commit Index {}, operation addRecord", getCommitIndex());
    }

    @Override
    public void addRecordById(Long id,Record record) {
        records.put(id, record);
        indexes.values().forEach(index -> index.indexRecord(record));
        incrementCommitIndex();
        log.debug("Table commit Index {}, operation addRecordById", getCommitIndex());
    }

    @Override
    public Record getRecord(long id) {
        return records.get(id);
    }

    @Override
    public void updateRecord(long id, Record record) throws RecordNotFoundException {
        Record oldRecord = records.get(id);
        if (oldRecord == null) {
            throw new RecordNotFoundException("Record with ID " + id + " not found.");
        }
        records.put(id, record);
        incrementCommitIndex();
        log.debug("Table commit Index {}, operation updateRecord", getCommitIndex());
        indexes.values().forEach(index -> index.updateRecord(oldRecord, record));
    }

    @Override
    public boolean deleteRecord(long id) {
        Record removed = records.remove(id);
        if (removed != null) {
            freeIds.add(id);
            indexes.values().forEach(index -> index.removeRecord(removed));
            incrementCommitIndex();
            log.debug("Table commit Index {}, operation deleteRecord", getCommitIndex());
            return true;
        }
        return false;
    }

    @Override
    public List<Object[]> queryRecords(Criteria criteria) {
        List<Object[]> result = new ArrayList<>();

        if (criteria instanceof InMemoryCriteria) {
            InMemoryCriteria inMemCriteria = (InMemoryCriteria) criteria;
            for (InMemoryCriteria.Condition condition : inMemCriteria.getConditions()) {
                if ((condition.getOperator() == InMemoryCriteria.Operator.EQUALS ||
                        condition.getOperator() == InMemoryCriteria.Operator.IN) &&
                        indexes.containsKey(condition.getFieldName())) {
                    Index index = indexes.get(condition.getFieldName());
                    Collection<Record> indexedRecords = index.search(condition.getValue());

                    // Собираем индекс и запись в массивы и добавляем в результат
                    records.entrySet().stream()
                            .filter(entry -> indexedRecords.contains(entry.getValue())
                                    && criteria.matches(entry.getValue()))
                            .forEach(entry -> result.add(new Object[]{entry.getKey(), entry.getValue()}));
                    return result;
                }
            }
        }

        // Если индексы не применимы, выполнить полный перебор и вернуть записи с их индексами
        records.entrySet().stream()
                .filter(entry -> criteria.matches(entry.getValue()))
                .forEach(entry -> result.add(new Object[]{entry.getKey(), entry.getValue()}));

        return result;
    }

    @Override
    public Number sumField(String fieldName) {
        return records.values().stream()
                .map(record -> record.getField(fieldName))
                .filter(java.util.Objects::nonNull)
                .map(Field::asNumber)
                .filter(java.util.Objects::nonNull)
                .mapToDouble(Number::doubleValue)
                .sum();
    }

    @Override
    public Number averageField(String fieldName) {
        return records.values().stream()
                .map(record -> record.getField(fieldName))
                .filter(java.util.Objects::nonNull)
                .map(Field::asNumber)
                .filter(java.util.Objects::nonNull)
                .mapToDouble(Number::doubleValue)
                .average()
                .orElse(Double.NaN);
    }

    @Override
    public long countRecords() {
        return records.size();
    }

    @Override
    public long nextRecordId() {
        if(!freeIds.isEmpty())
            return freeIds.pop();
        return records.size()+1;
    }

    @Override
    public Transaction beginTransaction() {
        return new InMemoryTransaction(this);
    }

    @Override
    public Index createIndex(String fieldName) {
        if (indexes.containsKey(fieldName)) {
            return indexes.get(fieldName);
        }
        Index index = new InMemoryIndex(fieldName);
        indexes.put(fieldName, index);
        incrementCommitIndex();
        log.debug("Table commit Index {}, operation createIndex", getCommitIndex());
        records.values().forEach(index::indexRecord);
        return index;
    }

    @Override
    public Index getIndex(String fieldName) {
        return indexes.get(fieldName);
    }

    @Override
    public long getCommitIndex(){
        return commitIndex.get();
    }

    @Override
    public long incrementCommitIndex(){
        return commitIndex.addAndGet(1);
    }


    @Override
    public void addRecordFromJson(String data) throws JsonProcessingException {
        Record record = new InMemoryRecord();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(data);
        JsonNode fieldValuesNode = rootNode.get("fieldValues");
        for (JsonNode fieldNode : fieldValuesNode) {
            String name = fieldNode.get("name").asText();
            String value = fieldNode.get("value").asText();
            record.setField(name, value);
        }
        records.put(nextRecordId(), record);
        indexes.values().forEach(index -> index.indexRecord(record));
        incrementCommitIndex();
        log.debug("Table commit Index {}, operation addRecord", getCommitIndex());
    }

    @Override
    public void updateRecordFromJson(Long id, String data) throws JsonProcessingException {
        Record record = new InMemoryRecord();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(data);
        JsonNode fieldValuesNode = rootNode.get("fieldValues");
        for (JsonNode fieldNode : fieldValuesNode) {
            String name = fieldNode.get("name").asText();
            String value = fieldNode.get("value").asText();
            record.setField(name, value);
        }
        records.put(id, record);
        indexes.values().forEach(index -> index.indexRecord(record));
        incrementCommitIndex();
        log.debug("Table commit Index {}, operation updateRecord", getCommitIndex());
    }
}

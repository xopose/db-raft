package com.raft.server.database.database.new_db.utils;

import com.raft.server.database.database.new_db.Record;
import com.raft.server.database.database.new_db.Table;
import com.raft.server.database.database.new_db.exceptions.RecordNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class InMemoryTransaction implements Transaction {
    private final Table table;
    private final Map<Long, Record> addedRecords;
    private final Map<Long, Record> updatedRecords;
    private final Map<Long, Record> deletedRecords;
    private boolean active;

    public InMemoryTransaction(Table table) {
        this.table = table;
        this.addedRecords = new HashMap<>();
        this.updatedRecords = new HashMap<>();
        this.deletedRecords = new HashMap<>();
        this.active = true;
    }

    @Override
    public void addRecord(Record record) {
        if (!active) throw new IllegalStateException("Transaction is not active");
        addedRecords.put(table.nextRecordId(), record);
    }

    @Override
    public void updateRecord(long id, Record record) throws RecordNotFoundException {
        if (!active) throw new IllegalStateException("Transaction is not active");
        Record existing = table.getRecord(id);
        if (existing == null) {
            throw new RecordNotFoundException("Record with ID " + id + " not found.");
        }
        updatedRecords.put(id, record);
    }

    @Override
    public boolean deleteRecord(long id) {
        if (!active) throw new IllegalStateException("Transaction is not active");
        Record existing = table.getRecord(id);
        if (existing != null) {
            deletedRecords.put(id, existing);
            return true;
        }
        return false;
    }

    @Override
    public void commit() {
        if (!active) throw new IllegalStateException("Transaction is not active");

        for (Map.Entry<Long, Record> entry : addedRecords.entrySet()) {
            table.addRecordById(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Long, Record> entry : updatedRecords.entrySet()) {
            try {
                table.updateRecord(entry.getKey(), entry.getValue());
            } catch (RecordNotFoundException e) {
                e.printStackTrace();
            }
        }

        for (Long id : deletedRecords.keySet()) {
            table.deleteRecord(id);
        }
        active = false;
    }

    @Override
    public void rollback() {
        if (!active) throw new IllegalStateException("Transaction is not active");

        addedRecords.clear();
        updatedRecords.clear();
        deletedRecords.clear();
        active = false;
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
package com.raft.server.database.database.new_db;



import com.raft.server.database.database.new_db.exceptions.CantCreateDatabaseException;
import com.raft.server.database.database.new_db.exceptions.TableNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
@Slf4j
public class InMemoryDatabase implements Database {
    private Map<String, Table> tables = new HashMap<>();
    private AtomicLong commitIndex = new AtomicLong(0);
    @Override
    public void createTable(String tableName) throws CantCreateDatabaseException {
        if (!tables.containsKey(tableName)) {
            tables.put(tableName, new InMemoryTable());
            incrementCommitIndex();
            log.debug("Database commit Index {}, operation createTable", getCommitIndex());
        } else {
            throw new CantCreateDatabaseException("Таблица с таким именем уже существует.");
        }
    }

    @Override
    public void deleteTable(String tableName) throws TableNotFoundException {
        if (tables.containsKey(tableName)) {
            tables.remove(tableName);
            incrementCommitIndex();
            log.debug("Database commit Index {}, operation deleteTable", getCommitIndex());
        } else {
            throw new TableNotFoundException("Таблица не найдена: " + tableName);
        }
    }

    @Override
    public Table getTable(String tableName) throws TableNotFoundException {
        if (tables.containsKey(tableName)) {
            return tables.get(tableName);
        } else {
            throw new TableNotFoundException("Таблица не найдена: " + tableName);
        }
    }

    @Override
    public void updateTable(String tableName,Table table) throws TableNotFoundException {
        Table exist = getTable(tableName);
        if (exist != table) {
            tables.put(tableName, table);
            incrementCommitIndex();
            log.debug("Database commit Index {}, operation updateTable", getCommitIndex());
        }
    }

    @Override
    public Map<String, Table> getAllTables() {
        return tables;
    }

    @Override
    public long getCommitIndex(){
        return commitIndex.get();
    }

    @Override
    public long incrementCommitIndex(){
        return commitIndex.addAndGet(1);
    }
}

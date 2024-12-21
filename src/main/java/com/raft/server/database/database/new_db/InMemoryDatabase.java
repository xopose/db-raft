package com.raft.server.database.database.new_db;



import com.raft.server.database.database.new_db.exceptions.CantCreateDatabaseException;
import com.raft.server.database.database.new_db.exceptions.TableNotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryDatabase implements Database {
    private Map<String, Table> tables = new HashMap<>();

    @Override
    public void createTable(String tableName) throws CantCreateDatabaseException {
        if (!tables.containsKey(tableName)) {
            tables.put(tableName, new InMemoryTable());
        } else {
            throw new CantCreateDatabaseException("Таблица с таким именем уже существует.");
        }
    }

    @Override
    public void deleteTable(String tableName) throws TableNotFoundException {
        if (tables.containsKey(tableName)) {
            tables.remove(tableName);
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
        if (exist != table) tables.put(tableName, table);
    }

    @Override
    public Collection<Table> getAllTables() {
        return tables.values();
    }
}

package com.raft.server.database.database.new_db;

import com.raft.server.database.database.new_db.exceptions.CantCreateDatabaseException;
import com.raft.server.database.database.new_db.exceptions.TableNotFoundException;

import java.util.Collection;

public interface Database {

    /**
     * Создает таблицу с указанным именем
     * @param tableName имя таблицы
     */
    void createTable(String tableName) throws CantCreateDatabaseException;

    /**
     * Удаляет таблицу с указанным именем
     * @param tableName имя таблицы
     */
    void deleteTable(String tableName) throws TableNotFoundException;

    /**
     * Возвращает таблицу по ее имени.
     * @param tableName имя таблицы
     */
    Table getTable(String tableName) throws TableNotFoundException;

    /**
     * Обновляет таблицу в базе данных
     * @param tableName
     * @param table
     * @return
     */
    void updateTable(String tableName,Table table) throws TableNotFoundException;

    /**
     * Возвращает список всех таблиц в базе данных.
     */
    Collection<Table> getAllTables();
}

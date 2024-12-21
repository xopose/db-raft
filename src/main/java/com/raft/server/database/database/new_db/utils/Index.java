package com.raft.server.database.database.new_db.utils;

import com.raft.server.database.database.new_db.Record;

import java.util.Collection;

public interface Index {

    /**
     * Индексирует запись по указанному полю.
     *
     * @param record Запись для индексирования.
     */
    void indexRecord(Record record);

    /**
     * Удаляет запись из индекса.
     *
     * @param record Запись для удаления.
     */
    void removeRecord(Record record);

    /**
     * Поиск записей по значению поля.
     *
     * @param value Значение для поиска.
     * @return Коллекция записей, соответствующих значению.
     */
    Collection<Record> search(Object value);

    /**
     * Обновляет индекс при изменении значения поля в записи.
     *
     * @param oldRecord Запись до изменения.
     * @param newRecord Запись после изменения.
     */
    void updateRecord(Record oldRecord, Record newRecord);

    /**
     * Возвращает имя поля, по которому создан индекс.
     *
     * @return Имя поля.
     */
    String getFieldName();

    /**
     * Оптимизирует индекс для повышения производительности.
     */
    void optimize();

    /**
     * Удаляет индекс.
     */
    void drop();
}

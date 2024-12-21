package com.raft.server.database.database.new_db.utils;

public interface ComparisonCriteria {

    /**
     * Определяет, как сравнивать значения полей.
     *
     * @param fieldName  Имя поля для сравнения.
     * @param value1    Первое значение.
     * @param value2    Второе значение.
     * @return Результат сравнения.
     */
    ComparisonResult compare(String fieldName, Object value1, Object value2);
}

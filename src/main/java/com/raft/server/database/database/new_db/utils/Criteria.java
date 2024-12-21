package com.raft.server.database.database.new_db.utils;

import com.raft.server.database.database.new_db.Record;

import java.util.Collection;

public interface Criteria {

    /**
     * Условие равенства.
     *
     * @param fieldName Имя поля.
     * @param value     Значение для сравнения.
     * @return Обновленная Criteria
     */
    Criteria equals(String fieldName, Object value);

    /**
     * Условие неравенства.
     *
     * @param fieldName Имя поля.
     * @param value     Значение для сравнения.
     * @return Обновленная Criteria
     */
    Criteria notEquals(String fieldName, Object value);

    /**
     * Условие больше чем.
     *
     * @param fieldName Имя поля.
     * @param value     Значение для сравнения.
     * @return Обновленная Criteria
     */
    Criteria greaterThan(String fieldName, Comparable<?> value);

    /**
     * Условие меньше чем.
     *
     * @param fieldName Имя поля.
     * @param value     Значение для сравнения.
     * @return Обновленная Criteria
     */
    Criteria lessThan(String fieldName, Comparable<?> value);

    /**
     * Условие LIKE для строкового поля.
     *
     * @param fieldName Имя поля.
     * @param pattern   Шаблон для поиска.
     * @return Обновленная Criteria
     */
    Criteria like(String fieldName, String pattern);

    /**
     * Добавляет логическое условие AND.
     *
     * @param other Другой объект критериев.
     * @return Обновленная Criteria
     */
    Criteria and(Criteria other);

    /**
     * Добавляет логическое условие OR.
     *
     * @param other Другой объект критериев.
     * @return Обновленная Criteria
     */
    Criteria or(Criteria other);

    /**
     * Условие IN.
     *
     * @param fieldName Имя поля.
     * @param values    Коллекция возможных значений.
     * @return Обновленная Criteria
     */
    Criteria in(String fieldName, Collection<?> values);

    /**
     * Условие BETWEEN для числового поля.
     *
     * @param fieldName Имя поля.
     * @param lower     Нижняя граница.
     * @param upper     Верхняя граница.
     * @return Обновленная Criteria
     */
    Criteria between(String fieldName, Comparable<?> lower, Comparable<?> upper);

    /**
     * Выполняет проверку соответствия записи критериям.
     *
     * @param record Запись для проверки.
     * @return Результат соответствия записи критериям.
     */
    boolean matches(Record record);
}

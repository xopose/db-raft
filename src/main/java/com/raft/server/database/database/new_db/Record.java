package com.raft.server.database.database.new_db;


import java.util.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.raft.server.database.database.new_db.deserializers.RecordDeserializer;
import com.raft.server.database.database.new_db.exceptions.SerializationException;
import com.raft.server.database.database.new_db.utils.ComparisonCriteria;
import com.raft.server.database.database.new_db.utils.ComparisonResult;
@JsonDeserialize(using = RecordDeserializer.class)
public interface Record {

    /**
     * Получает поле по его имени.
     *
     * @param fieldName Имя поля.
     * @return Объект поля или NULL, если поле не найдено.
     */
    Field getField(String fieldName);

    /**
     * Устанавливает значение поля.
     *
     * @param fieldName Имя поля.
     * @param value     Новое значение поля.
     */
    void setField(String fieldName, Object value);

    /**
     * Возвращает карту всех полей записи.
     *
     * @return Карта полей.
     */
    Map<String, Field> getFields();

    /**
     * Обновляет несколько полей записи одновременно.
     *
     * @param values Карта имен полей и их новых значений.
     */
    void updateFields(Map<String, Object> values);

    /**
     * Возвращает набор имен всех полей записи.
     *
     * @return Набор имен полей.
     */
    Set<String> getFieldNames();

    /**
     * Возвращает коллекцию всех полей записи.
     *
     * @return Коллекция полей.
     */
    Collection<Field> getFieldValues();

    /**
     * Проверяет, содержит ли запись указанное поле.
     *
     * @param fieldName Имя поля.
     * @return Результат поиска записи.
     */
    boolean containsField(String fieldName);

    /**
     * Удаляет поле из записи.
     *
     * @param fieldName Имя поля.
     * @return Результат удаления поля.
     */
    boolean removeField(String fieldName);

    /**
     * Клонирует текущую запись.
     *
     * @return Клонированная запись.
     */
    Record clone();

    /**
     * Сериализует запись в строку (например, в JSON).
     *
     * @return Строковое представление записи.
     */
    String serialize(List<String> recordFields);

    /**
     * Десериализует запись из строки.
     *
     * @param data Строковое представление записи.
     * @return Десериализованная запись.
     * @throws SerializationException Если десериализация не удалась.
     */
    static Record deserialize(String data) throws SerializationException {
        throw new UnsupportedOperationException("Не реализовано");
    }

    /**
     * Валидация записи.
     *
     * @return Результат валидации.
     */
    boolean isValid();

    /**
     * Сравнивает текущую запись с другой по определенному критерию.
     *
     * @param other    Другая запись для сравнения.
     * @param criteria Критерии сравнения.
     * @return Результат сравнения.
     */
    ComparisonResult compare(Record other, ComparisonCriteria criteria);
}

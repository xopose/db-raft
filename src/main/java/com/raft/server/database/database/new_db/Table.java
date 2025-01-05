package com.raft.server.database.database.new_db;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.raft.server.database.database.new_db.exceptions.RecordNotFoundException;
import com.raft.server.database.database.new_db.utils.Criteria;
import com.raft.server.database.database.new_db.utils.Index;
import com.raft.server.database.database.new_db.utils.Transaction;

public interface Table {

    /**
     * Добавляет новую запись в базу данных по ее id.
     *
     * @param id     Идентификатор записи.
     * @param record Объект записи.
     */
    void addRecordById(Long id, Record record);
    /**
     * Добавляет новую запись в базу данных.
     *
     * @param record Объект записи.
     */
    void addRecord(Record record);

    /**
     * Получает запись по ее идентификатору.
     *
     * @param id Идентификатор записи.
     * @return Объект записи или NULL, если запись не найдена.
     */
    Record getRecord(long id);

    /**
     * Обновляет существующую запись.
     *
     * @param id     Идентификатор записи.
     * @param record Обновленные данные записи.
     * @throws RecordNotFoundException Если запись с указанным ID не найдена.
     */
    void updateRecord(long id, Record record) throws RecordNotFoundException;

    /**
     * Удаляет запись по ее идентификатору.
     *
     * @param id Идентификатор записи.
     * @return Результат удаления записи.
     */
    boolean deleteRecord(long id);

    /**
     * Выполняет запрос с заданными критериями и возвращает соответствующие записи.
     *
     * @param criteria Объект критериев запроса.
     * @return Коллекция записей, соответствующих критериям.
     */
    public List<Object[]> queryRecords(Criteria criteria);

    /**
     * Возвращает сумму значений указанного числового поля по всем записям.
     *
     * @param fieldName Имя поля.
     * @return Сумма значений или NULL, если поле не существует или не числовое.
     */
    Number sumField(String fieldName);

    /**
     * Возвращает среднее значение указанного числового поля по всем записям.
     *
     * @param fieldName Имя поля.
     * @return Среднее значение или NULL, если поле не существует или не числовое.
     */
    Number averageField(String fieldName);

    /**
     * Возвращает общее количество записей в базе данных.
     *
     * @return Количество записей.
     */
    long countRecords();

    /**
     * Возвращает следующий id записи.
     *
     * @return Количество записей.
     */
    long nextRecordId();

    /**
     * Начинает новую транзакцию.
     *
     * @return Объект транзакции.
     */
    Transaction beginTransaction();

    /**
     * Создает индекс на указанном поле.
     *
     * @param fieldName Имя поля для индексации.
     * @return Объект индекса.
     */
    Index createIndex(String fieldName);

    /**
     * Получает индекс по имени поля.
     *
     * @param fieldName Имя поля.
     * @return Объект индекса или NULL, если индекс не существует.
     */
    Index getIndex(String fieldName);

    //Get commit Index
    long getCommitIndex();

    //Increment commit Index
    long incrementCommitIndex();

    /**
     * Добавляет новую запись, полученную в формате JSON в базу данных.
     *
     * @param record Объект записи.
     */
    void addRecordFromJson(String record) throws JsonProcessingException;

    /**
     * Обновляет запись, полученную в формате JSON в базе данных.
     *
     * @param record Объект записи.
     */
    void updateRecordFromJson(Long id, String record) throws JsonProcessingException;
}
package com.raft.server.database.database.new_db;


import com.raft.server.database.database.new_db.utils.UnaryOperator;

public interface Field {

    /**
     * Возвращает имя поля.
     *
     * @return Имя поля.
     */
    String getName();

    /**
     * Получает значение поля.
     *
     * @return Значение поля.
     */
    Object getValue();

    /**
     * Устанавливает значение поля.
     *
     * @param value Новое значение поля.
     */
    void setValue(Object value);

    /**
     * Возвращает тип данных поля.
     *
     * @return Класс типа данных.
     */
    Class<?> getType();

    /**
     * Преобразует значение поля в число, если это возможно.
     *
     * @return Числовое значение или NULL, если преобразование невозможно.
     */
    Number asNumber();

    /**
     * Проверяет валидность значения поля.
     *
     * @return true, если значение валидно, иначе false.
     */
    boolean isValid();

    /**
     * Выполняет арифметическую операцию над числовым значением поля.
     *
     * @param operator Операция для применения.
     * @return Результат операции или NULL, если операция невозможна.
     */
    Number applyOperation(UnaryOperator<Number> operator);

    /**
     * Форматирует значение поля в строку по заданному шаблону.
     *
     * @param pattern Шаблон форматирования.
     * @return Отформатированная строка или NULL, если форматирование невозможно.
     */
    String format(String pattern);

    /**
     * Сравнивает значение текущего поля с другим.
     *
     * @param other Другое поле для сравнения.
     * @return Результат сравнения.
     */
    int compareTo(Field other);
}

package com.raft.server.database.database.new_db.utils;


import com.raft.server.database.database.new_db.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.raft.server.database.database.new_db.Record;
public class InMemoryCriteria implements Criteria {
    private final List<Condition> conditions;
    private final List<LogicalOperator> logicalOperators;

    public InMemoryCriteria() {
        conditions = new ArrayList<>();
        logicalOperators = new ArrayList<>();
    }

    @Override
    public Criteria equals(String fieldName, Object value) {
        conditions.add(new Condition(fieldName, Operator.EQUALS, value));
        return this;
    }

    @Override
    public Criteria notEquals(String fieldName, Object value) {
        conditions.add(new Condition(fieldName, Operator.NOT_EQUALS, value));
        return this;
    }

    @Override
    public Criteria greaterThan(String fieldName, Comparable<?> value) {
        conditions.add(new Condition(fieldName, Operator.GREATER_THAN, value));
        return this;
    }

    @Override
    public Criteria lessThan(String fieldName, Comparable<?> value) {
        conditions.add(new Condition(fieldName, Operator.LESS_THAN, value));
        return this;
    }

    @Override
    public Criteria like(String fieldName, String pattern) {
        conditions.add(new Condition(fieldName, Operator.LIKE, pattern));
        return this;
    }

    @Override
    public Criteria and(Criteria other) {
        if (other instanceof InMemoryCriteria) {
            this.conditions.addAll(((InMemoryCriteria) other).conditions);
            this.logicalOperators.add(LogicalOperator.AND);
        }
        return this;
    }

    @Override
    public Criteria or(Criteria other) {
        if (other instanceof InMemoryCriteria) {
            this.conditions.addAll(((InMemoryCriteria) other).conditions);
            this.logicalOperators.add(LogicalOperator.OR);
        }
        return this;
    }

    @Override
    public Criteria in(String fieldName, Collection<?> values) {
        conditions.add(new Condition(fieldName, Operator.IN, values));
        return this;
    }

    @Override
    public Criteria between(String fieldName, Comparable<?> lower, Comparable<?> upper) {
        conditions.add(new Condition(fieldName, Operator.BETWEEN, lower, upper));
        return this;
    }

    @Override
    public boolean matches(Record record) {
        boolean result = true;
        for (int i = 0; i < conditions.size(); i++) {
            Condition condition = conditions.get(i);
            boolean conditionResult = condition.evaluate(record);
            if (i > 0 && logicalOperators.size() > i - 1) {
                LogicalOperator operator = logicalOperators.get(i - 1);
                if (operator == LogicalOperator.AND) {
                    result = result && conditionResult;
                } else if (operator == LogicalOperator.OR) {
                    result = result || conditionResult;
                }
            } else {
                result = conditionResult;
            }
        }
        return result;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public enum Operator {
        EQUALS,
        NOT_EQUALS,
        GREATER_THAN,
        LESS_THAN,
        LIKE,
        IN,
        BETWEEN
    }

    private enum LogicalOperator {
        AND,
        OR
    }

    public class Condition {
        private String fieldName;
        private Operator operator;
        private Object value;
        private Object secondValue; // Для BETWEEN

        public Condition(String fieldName, Operator operator, Object value) {
            this.fieldName = fieldName;
            this.operator = operator;
            this.value = value;
        }

        public Condition(String fieldName, Operator operator, Object value, Object secondValue) {
            this.fieldName = fieldName;
            this.operator = operator;
            this.value = value;
            this.secondValue = secondValue;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Operator getOperator() {
            return operator;
        }

        public Object getValue() {
            return value;
        }

        public boolean evaluate(Record record) {
            Field field = record.getField(fieldName);
            if (field == null) {
                return false;
            }
            Object fieldValue = field.getValue();
            switch (operator) {
                case EQUALS:
                    return value.equals(fieldValue);
                case NOT_EQUALS:
                    return !value.equals(fieldValue);
                case GREATER_THAN:
                    if (fieldValue instanceof Integer && value instanceof Integer) {
                        return (Integer) fieldValue > (Integer) value;
                    } else if (fieldValue instanceof String && value instanceof String) {
                        return ((String) fieldValue).compareTo((String) value) > 0;
                    } else if (fieldValue instanceof Comparable && value instanceof Comparable && fieldValue.getClass().equals(value.getClass())) {
                        return ((Comparable) fieldValue).compareTo(value) > 0;
                    }
                    break;

                case LESS_THAN:
                    if (fieldValue instanceof Integer && value instanceof Integer) {
                        return (Integer) fieldValue < (Integer) value;
                    } else if (fieldValue instanceof String && value instanceof String) {
                        return ((String) fieldValue).compareTo((String) value) < 0;
                    } else if (fieldValue instanceof Comparable && value instanceof Comparable && fieldValue.getClass().equals(value.getClass())) {
                        return ((Comparable) fieldValue).compareTo(value) < 0;
                    }
                    break;
                case LIKE:
                    if (fieldValue instanceof String && value instanceof String) {
                        String pattern = ((String) value).replace("%", ".*").replace("_", ".");
                        return ((String) fieldValue).matches(pattern);
                    }
                    break;
                case IN:
                    if (value instanceof Collection) {
                        return ((Collection<?>) value).contains(fieldValue);
                    }
                    break;
                case BETWEEN:
                    if (fieldValue instanceof Comparable && value instanceof Comparable && secondValue instanceof Comparable) {
                        Comparable fieldComp = (Comparable) fieldValue;
                        Comparable lower = (Comparable) value;
                        Comparable upper = (Comparable) secondValue;
                        return fieldComp.compareTo(lower) >= 0 && fieldComp.compareTo(upper) <= 0;
                    }
                    break;
            }
            return false;
        }
    }
}

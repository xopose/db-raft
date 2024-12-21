package com.raft.server.database.database.new_db;

import com.raft.server.database.database.new_db.utils.UnaryOperator;
public class InMemoryField implements Field {
    private final String name;
    private Object value;

    public InMemoryField(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public Class<?> getType() {
        return value != null ? value.getClass() : Object.class;
    }

    @Override
    public Number asNumber() {
        if (value instanceof Number) {
            return (Number) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e1) {
                try {
                    return Double.parseDouble((String) value);
                } catch (NumberFormatException e2) {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Number applyOperation(UnaryOperator<Number> operator) {
        Number number = asNumber();
        if (number != null) {
            return operator.apply(number);
        }
        return null;
    }

    @Override
    public String format(String pattern) {
        if (value instanceof Number) {
            return String.format(pattern, value);
        } else if (value instanceof String) {
            return (String) value;
        }
        return null;
    }

    @Override
    public int compareTo(Field other) {
        if (this.value instanceof Comparable && other.getValue() instanceof Comparable) {
            Comparable thisComparable = (Comparable) this.value;
            Comparable otherComparable = (Comparable) other.getValue();
            return thisComparable.compareTo(otherComparable);
        }
        throw new UnsupportedOperationException("Fields are not comparable");
    }
}

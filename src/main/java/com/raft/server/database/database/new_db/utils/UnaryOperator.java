package com.raft.server.database.database.new_db.utils;

@FunctionalInterface
public interface UnaryOperator<T> {
    T apply(T t);
}

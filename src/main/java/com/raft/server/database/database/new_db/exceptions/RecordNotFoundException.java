package com.raft.server.database.database.new_db.exceptions;

public class RecordNotFoundException extends Exception {
    public RecordNotFoundException(String message) {
        super(message);
    }
}

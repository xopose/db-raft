package com.raft.server.database.database.new_db.exceptions;

public class IncorrectCommandException extends Exception{
    public IncorrectCommandException(String message){super(message);}
}
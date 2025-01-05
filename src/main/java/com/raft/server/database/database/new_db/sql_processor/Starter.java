package com.raft.server.database.database.new_db.sql_processor;

import com.network.http.Http;
import com.raft.server.context.Context;
import com.raft.server.database.database.new_db.Database;
import com.raft.server.database.database.new_db.exceptions.CantCreateDatabaseException;
import com.raft.server.database.database.new_db.exceptions.IncorrectCommandException;
import com.raft.server.database.database.new_db.exceptions.TableNotFoundException;

import java.util.Map;

public class Starter {
    private final Database database;
    private final Context context;
    private final Http http;
    public Starter(Database database, Context context, Http http) {
        this.database = database;
        this.context = context;
        this.http = http;
    }

    public Map<String, String> execute(String request) throws IncorrectCommandException, CantCreateDatabaseException, TableNotFoundException {
        String[] tokens = request.trim().split("(?=([^\"]*\"[^\"]*\")*[^\"]*$)\\s+");
        if (tokens.length == 0) {
            throw new IncorrectCommandException("Empty command");
        }
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].replace("’", "'").replace("‘", "'");
        }
        String command = tokens[0].toUpperCase();

        return switch (command) {
            case "SELECT" -> Select.select(tokens, database, context);
            case "INSERT" -> Insert.insert(tokens, database, context, http);
            case "UPDATE" -> Update.update(tokens, database, context, http);
            case "DELETE" -> Delete.delete(tokens, database, context, http);
            case "CREATE" -> Create.create(tokens, database, context, http);
            default -> throw new IncorrectCommandException("Unknown command: " + command);
        };
    }
}

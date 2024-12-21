package com.raft.server.database.controllers;

import com.raft.server.database.database.new_db.Database;
import com.raft.server.database.database.new_db.InMemoryDatabase;
import com.raft.server.database.database.new_db.exceptions.CantCreateDatabaseException;
import com.raft.server.database.database.new_db.exceptions.IncorrectCommandException;
import com.raft.server.database.database.new_db.exceptions.TableNotFoundException;
import com.raft.server.database.database.new_db.sql_processor.Starter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@ComponentScan(basePackages = "com.raft.server.database")
public class DbResourceController {
    private Database database;
    private Starter starter;
    @PostConstruct
    public void init() {
        this.database = new InMemoryDatabase();
        this.starter = new Starter(this.database);
    }
    @PostMapping("/execute")
    //TODO переделать на тело
    public String index(String command) throws CantCreateDatabaseException, TableNotFoundException, IncorrectCommandException {
        return starter.execute(command).toString();
    }
}

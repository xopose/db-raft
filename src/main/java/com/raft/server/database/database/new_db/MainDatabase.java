package com.raft.server.database.database.new_db;

import java.util.HashMap;
import java.util.Map;

public class MainDatabase {// implements Database {

//    private Map<String, Table> tables = new HashMap<>();
//    private ReplicationManager replicationManager;
//
//    public MainDatabase(String configFilePath) {
//        ConfigurationLoader configLoader = new ConfigurationLoader(configFilePath);
//        replicationManager = new ReplicationManager(this, configLoader.getReplicaAddresses());
//    }
//
//    @Override
//    public void createTable(String tableName) {
//        tables.put(tableName, new Table());
//        replicationManager.replicateOperation("CREATE TABLE " + tableName);
//    }
//
//    @Override
//    public void deleteTable(String tableName) {
//        tables.remove(tableName);
//        replicationManager.replicateOperation("DELETE TABLE " + tableName);
//    }
//
//    public void checkAndElectNewMaster() {
//        replicationManager.checkAndElectNewMaster();
//    }
//
//    @Override
//    public boolean isAlive() {
//        // Custom logic to check if this database instance is alive
//        return true;
//    }
//
//    @Override
//    public void executeRawOperation(String operation) {
//        // Custom logic to execute raw operations
//        System.out.println("Executing operation: " + operation);
//    }
}
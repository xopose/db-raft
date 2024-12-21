package com.raft.server.database.database.new_db;

import java.util.HashMap;
import java.util.Map;

public class ShardingManager {

    private Map<Integer, Database> shards = new HashMap<>();

    public void addShard(int shardId, Database database) {
        shards.put(shardId, database);
    }

    public Database getShard(int key) {
        int shardId = key % shards.size();
        return shards.get(shardId);
    }
}
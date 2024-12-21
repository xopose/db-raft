package com.raft.server.database.database.new_db.utils;

public class ComparisonResult {
    private final boolean isEqual;
    private final boolean isGreater;
    private final boolean isLess;

    public ComparisonResult(boolean isEqual, boolean isGreater, boolean isLess) {
        this.isEqual = isEqual;
        this.isGreater = isGreater;
        this.isLess = isLess;
    }

    public boolean isEqual() {
        return isEqual;
    }

    public boolean isGreater() {
        return isGreater;
    }

    public boolean isLess() {
        return isLess;
    }
}

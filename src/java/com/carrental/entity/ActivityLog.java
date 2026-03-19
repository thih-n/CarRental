package com.carrental.entity;

import java.sql.Timestamp;

public class ActivityLog {
    private int logID;
    private Integer userID;
    private String action;
    private String targetTable;
    private Integer targetID;
    private String description;
    private String ipAddress;
    private Timestamp createdAt;

    public ActivityLog() {
    }

    public ActivityLog(int logID, Integer userID, String action, String targetTable, Integer targetID,
            String description, String ipAddress, Timestamp createdAt) {
        this.logID = logID;
        this.userID = userID;
        this.action = action;
        this.targetTable = targetTable;
        this.targetID = targetID;
        this.description = description;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
    }

    public int getLogID() {
        return logID;
    }

    public void setLogID(int logID) {
        this.logID = logID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public Integer getTargetID() {
        return targetID;
    }

    public void setTargetID(Integer targetID) {
        this.targetID = targetID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

package com.carrental.entity;

public class CarStatus {
    private int statusID;
    private String statusName;
    private String description;

    public CarStatus() {
    }

    public CarStatus(int statusID, String statusName, String description) {
        this.statusID = statusID;
        this.statusName = statusName;
        this.description = description;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

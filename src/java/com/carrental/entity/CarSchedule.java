package com.carrental.entity;

import java.sql.Timestamp;

public class CarSchedule {
    private int scheduleID;
    private int carID;
    private Integer contractID;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private String scheduleStatus;
    private Timestamp createdAt;

    public CarSchedule() {
    }

    public CarSchedule(int scheduleID, int carID, Integer contractID, Timestamp startDateTime, Timestamp endDateTime,
            String scheduleStatus, Timestamp createdAt) {
        this.scheduleID = scheduleID;
        this.carID = carID;
        this.contractID = contractID;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.scheduleStatus = scheduleStatus;
        this.createdAt = createdAt;
    }

    public int getScheduleID() { return scheduleID; }
    public void setScheduleID(int scheduleID) { this.scheduleID = scheduleID; }

    public int getCarID() { return carID; }
    public void setCarID(int carID) { this.carID = carID; }

    public Integer getContractID() { return contractID; }
    public void setContractID(Integer contractID) { this.contractID = contractID; }

    public Timestamp getStartDateTime() { return startDateTime; }
    public void setStartDateTime(Timestamp startDateTime) { this.startDateTime = startDateTime; }

    public Timestamp getEndDateTime() { return endDateTime; }
    public void setEndDateTime(Timestamp endDateTime) { this.endDateTime = endDateTime; }

    public String getScheduleStatus() { return scheduleStatus; }
    public void setScheduleStatus(String scheduleStatus) { this.scheduleStatus = scheduleStatus; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

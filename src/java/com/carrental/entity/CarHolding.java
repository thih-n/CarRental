package com.carrental.entity;

import java.sql.Timestamp;

public class CarHolding {
    private int holdingID;
    private int carID;
    private Integer driverID;
    private int userID;
    private Timestamp rentStartDateTime;
    private Timestamp rentEndDateTime;
    private Timestamp expiryTime;
    private Timestamp createdAt;

    public CarHolding() {
    }

    public CarHolding(int carID, Integer driverID, int userID, Timestamp rentStartDateTime, Timestamp rentEndDateTime, Timestamp expiryTime) {
        this.carID = carID;
        this.driverID = driverID;
        this.userID = userID;
        this.rentStartDateTime = rentStartDateTime;
        this.rentEndDateTime = rentEndDateTime;
        this.expiryTime = expiryTime;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public int getHoldingID() { return holdingID; }
    public void setHoldingID(int holdingID) { this.holdingID = holdingID; }

    public int getCarID() { return carID; }
    public void setCarID(int carID) { this.carID = carID; }

    public Integer getDriverID() { return driverID; }
    public void setDriverID(Integer driverID) { this.driverID = driverID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public Timestamp getRentStartDateTime() { return rentStartDateTime; }
    public void setRentStartDateTime(Timestamp rentStartDateTime) { this.rentStartDateTime = rentStartDateTime; }

    public Timestamp getRentEndDateTime() { return rentEndDateTime; }
    public void setRentEndDateTime(Timestamp rentEndDateTime) { this.rentEndDateTime = rentEndDateTime; }

    public Timestamp getExpiryTime() { return expiryTime; }
    public void setExpiryTime(Timestamp expiryTime) { this.expiryTime = expiryTime; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isExpired() {
        return new Timestamp(System.currentTimeMillis()).after(expiryTime);
    }
}

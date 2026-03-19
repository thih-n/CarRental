package com.carrental.entity;

public class CarType {
    private int typeID;
    private String typeName;
    private Integer bodyID;
    private int seatCount;
    private String description;

    public CarType() {
    }

    public CarType(int typeID, String typeName, Integer bodyID, int seatCount, String description) {
        this.typeID = typeID;
        this.typeName = typeName;
        this.bodyID = bodyID;
        this.seatCount = seatCount;
        this.description = description;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getBodyID() {
        return bodyID;
    }

    public void setBodyID(Integer bodyID) {
        this.bodyID = bodyID;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getCarTypeID() {
        return typeID;
    }
}

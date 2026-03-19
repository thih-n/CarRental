package com.carrental.entity;

public class Location {
    private int locationID;
    private String locationName;
    private String address;
    private Boolean isActive;

    public Location() {
    }

    public Location(int locationID, String locationName, String address, Boolean isActive) {
        this.locationID = locationID;
        this.locationName = locationName;
        this.address = address;
        this.isActive = isActive;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}

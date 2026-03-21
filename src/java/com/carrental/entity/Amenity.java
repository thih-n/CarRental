package com.carrental.entity;

public class Amenity {
    private int amenityID;
    private String amenityName;
    private String iconClass;

    public Amenity() {
    }

    public Amenity(int amenityID, String amenityName, String iconClass) {
        this.amenityID = amenityID;
        this.amenityName = amenityName;
        this.iconClass = iconClass;
    }

    public int getAmenityID() {
        return amenityID;
    }

    public void setAmenityID(int amenityID) {
        this.amenityID = amenityID;
    }

    public String getAmenityName() {
        return amenityName;
    }

    public void setAmenityName(String amenityName) {
        this.amenityName = amenityName;
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }
}

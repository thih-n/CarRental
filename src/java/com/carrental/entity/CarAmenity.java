package com.carrental.entity;

public class CarAmenity {
    private int carID;
    private int amenityID;

    public CarAmenity() {
    }

    public CarAmenity(int carID, int amenityID) {
        this.carID = carID;
        this.amenityID = amenityID;
    }

    public int getCarID() {
        return carID;
    }

    public void setCarID(int carID) {
        this.carID = carID;
    }

    public int getAmenityID() {
        return amenityID;
    }

    public void setAmenityID(int amenityID) {
        this.amenityID = amenityID;
    }
}

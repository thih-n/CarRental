package com.carrental.entity;

public class BodyStyle {
    private int bodyID;
    private String bodyName;

    public BodyStyle() {
    }

    public BodyStyle(int bodyID, String bodyName) {
        this.bodyID = bodyID;
        this.bodyName = bodyName;
    }

    public int getBodyID() {
        return bodyID;
    }

    public void setBodyID(int bodyID) {
        this.bodyID = bodyID;
    }

    public String getBodyName() {
        return bodyName;
    }

    public void setBodyName(String bodyName) {
        this.bodyName = bodyName;
    }
}

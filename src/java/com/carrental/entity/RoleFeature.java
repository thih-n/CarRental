package com.carrental.entity;

public class RoleFeature {
    private int roleID;
    private int featureID;

    public RoleFeature() {
    }

    public RoleFeature(int roleID, int featureID) {
        this.roleID = roleID;
        this.featureID = featureID;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public int getFeatureID() {
        return featureID;
    }

    public void setFeatureID(int featureID) {
        this.featureID = featureID;
    }
}

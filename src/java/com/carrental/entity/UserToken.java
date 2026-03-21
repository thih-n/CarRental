package com.carrental.entity;

import java.sql.Timestamp;

public class UserToken {
    private int tokenID;
    private int userID;
    private String selector;
    private String validatorHash;
    private Timestamp expiryDate;

    public UserToken() {
    }

    public UserToken(int tokenID, int userID, String selector, String validatorHash, Timestamp expiryDate) {
        this.tokenID = tokenID;
        this.userID = userID;
        this.selector = selector;
        this.validatorHash = validatorHash;
        this.expiryDate = expiryDate;
    }

    public int getTokenID() {
        return tokenID;
    }

    public void setTokenID(int tokenID) {
        this.tokenID = tokenID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getValidatorHash() {
        return validatorHash;
    }

    public void setValidatorHash(String validatorHash) {
        this.validatorHash = validatorHash;
    }

    public Timestamp getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.carrental.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author Nguyen Duc Thinh
 */
public class User {
    private int userID;
    private int roleID; // Moved up to match DB order
    
    // Basic Info
    private String fullName;
    private String email;
    private String passwordHash;
    private String phoneNumber;
    private String address;
    
    // Single Session Check
    private String lastLoginToken;

    // Authentication & KYC
    private boolean isEmailVerified;
    private boolean isPhoneVerified;
    private String identityCardNumber;
    private String licenseImageFront;
    private String licenseImageBack;
    private String licenseStatus;
    
    // Status & Meta
    private boolean isActive;
    private Timestamp createdAt;
    
    // Driver Salary Info
    private BigDecimal baseSalary;
    private BigDecimal commissionRate;

    // Empty Constructor
    public User() {
    }

    // Full Constructor
    public User(int userID, int roleID, String fullName, String email, String passwordHash, 
                String phoneNumber, String address, String lastLoginToken, boolean isEmailVerified, 
                boolean isPhoneVerified, String identityCardNumber, String licenseImageFront, 
                String licenseImageBack, String licenseStatus, boolean isActive, Timestamp createdAt) {
        this.userID = userID;
        this.roleID = roleID;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.lastLoginToken = lastLoginToken;
        this.isEmailVerified = isEmailVerified;
        this.isPhoneVerified = isPhoneVerified;
        this.identityCardNumber = identityCardNumber;
        this.licenseImageFront = licenseImageFront;
        this.licenseImageBack = licenseImageBack;
        this.licenseStatus = licenseStatus;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // --- Getters and Setters ---

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public int getRoleID() { return roleID; }
    public void setRoleID(int roleID) { this.roleID = roleID; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLastLoginToken() { return lastLoginToken; }
    public void setLastLoginToken(String lastLoginToken) { this.lastLoginToken = lastLoginToken; }

    public boolean isEmailVerified() { return isEmailVerified; }
    public void setEmailVerified(boolean emailVerified) { isEmailVerified = emailVerified; }

    public boolean isPhoneVerified() { return isPhoneVerified; }
    public void setPhoneVerified(boolean phoneVerified) { isPhoneVerified = phoneVerified; }

    public String getIdentityCardNumber() { return identityCardNumber; }
    public void setIdentityCardNumber(String identityCardNumber) { this.identityCardNumber = identityCardNumber; }

    public String getLicenseImageFront() { return licenseImageFront; }
    public void setLicenseImageFront(String licenseImageFront) { this.licenseImageFront = licenseImageFront; }

    public String getLicenseImageBack() { return licenseImageBack; }
    public void setLicenseImageBack(String licenseImageBack) { this.licenseImageBack = licenseImageBack; }

    public String getLicenseStatus() { return licenseStatus; }
    public void setLicenseStatus(String licenseStatus) { this.licenseStatus = licenseStatus; }

    public boolean isActive() { return isActive; }
    public boolean getIsActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    // Driver Salary Info
    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }
    
    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }
}

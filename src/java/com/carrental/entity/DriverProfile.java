package com.carrental.entity;

import java.math.BigDecimal;
import java.sql.Date;

public class DriverProfile {
    private int driverID;
    private String fullName;
    private String licenseNumber;
    private Date licenseExpiry;
    private Integer experienceYears;
    private BigDecimal rating;
    private Integer totalTrips;
    private String driverStatus;
    private Integer baseLocationID;
    private BigDecimal baseSalary;
    private BigDecimal commissionRate;
    private BigDecimal hourlyRate;
    private BigDecimal dailyRate;
    private Boolean isActive;

    public DriverProfile() {
    }

    public DriverProfile(int driverID, String licenseNumber, Date licenseExpiry, Integer experienceYears,
            BigDecimal rating, Integer totalTrips, String driverStatus, Integer baseLocationID,
            BigDecimal baseSalary, BigDecimal commissionRate, BigDecimal hourlyRate, BigDecimal dailyRate) {
        this.driverID = driverID;
        this.licenseNumber = licenseNumber;
        this.licenseExpiry = licenseExpiry;
        this.experienceYears = experienceYears;
        this.rating = rating;
        this.totalTrips = totalTrips;
        this.driverStatus = driverStatus;
        this.baseLocationID = baseLocationID;
        this.baseSalary = baseSalary;
        this.commissionRate = commissionRate;
        this.hourlyRate = hourlyRate;
        this.dailyRate = dailyRate;
    }

    public int getDriverID() { return driverID; }
    public void setDriverID(int driverID) { this.driverID = driverID; }
    public int getUserID() { return driverID; }
    public void setUserID(int userID) { this.driverID = userID; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public Date getLicenseExpiry() { return licenseExpiry; }
    public void setLicenseExpiry(Date licenseExpiry) { this.licenseExpiry = licenseExpiry; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public Integer getTotalTrips() { return totalTrips; }
    public void setTotalTrips(Integer totalTrips) { this.totalTrips = totalTrips; }

    public String getDriverStatus() { return driverStatus; }
    public void setDriverStatus(String driverStatus) { this.driverStatus = driverStatus; }

    public Integer getBaseLocationID() { return baseLocationID; }
    public void setBaseLocationID(Integer baseLocationID) { this.baseLocationID = baseLocationID; }

    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }

    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public BigDecimal getDailyRate() { return dailyRate; }
    public void setDailyRate(BigDecimal dailyRate) { this.dailyRate = dailyRate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}

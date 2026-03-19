package com.carrental.entity;

import java.sql.Timestamp;

public class StaffBookingItem {
    private int contractID;
    private String contractCode;
    private Integer customerID;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String carName;
    private String licensePlate;
    private String driverName;
    private Integer driverID;
    private String driverPhone;
    private String driverEmail;
    private Integer carID;
    private Integer dropOffLocationID;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private String detailStatus;
    private String scheduleStatus;
    private Boolean hasDriver;
    private String statusName;
    private String pickupLocation;
    private String dropoffLocation;

    public int getContractID() { return contractID; }
    public void setContractID(int contractID) { this.contractID = contractID; }

    public String getContractCode() { return contractCode; }
    public void setContractCode(String contractCode) { this.contractCode = contractCode; }

    public Integer getCustomerID() { return customerID; }
    public void setCustomerID(Integer customerID) { this.customerID = customerID; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCarName() { return carName; }
    public void setCarName(String carName) { this.carName = carName; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public String getDriverEmail() { return driverEmail; }
    public void setDriverEmail(String driverEmail) { this.driverEmail = driverEmail; }

    public Integer getDriverID() { return driverID; }
    public void setDriverID(Integer driverID) { this.driverID = driverID; }

    public Integer getCarID() { return carID; }
    public void setCarID(Integer carID) { this.carID = carID; }

    public Integer getDropOffLocationID() { return dropOffLocationID; }
    public void setDropOffLocationID(Integer dropOffLocationID) { this.dropOffLocationID = dropOffLocationID; }

    public Timestamp getStartDateTime() { return startDateTime; }
    public void setStartDateTime(Timestamp startDateTime) { this.startDateTime = startDateTime; }

    public Timestamp getEndDateTime() { return endDateTime; }
    public void setEndDateTime(Timestamp endDateTime) { this.endDateTime = endDateTime; }

    public String getDetailStatus() { return detailStatus; }
    public void setDetailStatus(String detailStatus) { this.detailStatus = detailStatus; }

    public String getScheduleStatus() { return scheduleStatus; }
    public void setScheduleStatus(String scheduleStatus) { this.scheduleStatus = scheduleStatus; }

    public Boolean getHasDriver() { return hasDriver; }
    public void setHasDriver(Boolean hasDriver) { this.hasDriver = hasDriver; }
    
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getDropoffLocation() { return dropoffLocation; }
    public void setDropoffLocation(String dropoffLocation) { this.dropoffLocation = dropoffLocation; }
    
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    
    // Helper methods for the modal
    public int getBookingID() { return contractID; }
}

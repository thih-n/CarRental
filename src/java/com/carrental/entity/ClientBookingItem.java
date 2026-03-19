package com.carrental.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ClientBookingItem {
    private int contractID;
    private String contractCode;
    private int carID;
    private String carName;
    private Integer driverID;
    private String driverName;
    private int pickUpLocationID;
    private String pickUpLocationName;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private BigDecimal totalAmount;
    private BigDecimal depositAmount;
    private String contractStatus;
    private String detailStatus;

    public int getContractID() { return contractID; }
    public void setContractID(int contractID) { this.contractID = contractID; }

    public String getContractCode() { return contractCode; }
    public void setContractCode(String contractCode) { this.contractCode = contractCode; }

    public int getCarID() { return carID; }
    public void setCarID(int carID) { this.carID = carID; }

    public String getCarName() { return carName; }
    public void setCarName(String carName) { this.carName = carName; }

    public Integer getDriverID() { return driverID; }
    public void setDriverID(Integer driverID) { this.driverID = driverID; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public int getPickUpLocationID() { return pickUpLocationID; }
    public void setPickUpLocationID(int pickUpLocationID) { this.pickUpLocationID = pickUpLocationID; }

    public String getPickUpLocationName() { return pickUpLocationName; }
    public void setPickUpLocationName(String pickUpLocationName) { this.pickUpLocationName = pickUpLocationName; }

    public Timestamp getStartDateTime() { return startDateTime; }
    public void setStartDateTime(Timestamp startDateTime) { this.startDateTime = startDateTime; }

    public Timestamp getEndDateTime() { return endDateTime; }
    public void setEndDateTime(Timestamp endDateTime) { this.endDateTime = endDateTime; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getDepositAmount() { return depositAmount; }
    public void setDepositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; }

    public String getContractStatus() { return contractStatus; }
    public void setContractStatus(String contractStatus) { this.contractStatus = contractStatus; }

    public String getDetailStatus() { return detailStatus; }
    public void setDetailStatus(String detailStatus) { this.detailStatus = detailStatus; }

    public boolean canCancel() {
        return "Booked".equals(detailStatus) && startDateTime != null 
            && startDateTime.after(new Timestamp(System.currentTimeMillis()));
    }
}

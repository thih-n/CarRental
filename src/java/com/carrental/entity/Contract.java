package com.carrental.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Contract {
    private int contractID;
    private String contractCode;
    private Integer customerID;
    private Integer staffID;
    private Integer pickUpLocationID;
    private Integer dropOffLocationID;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private BigDecimal totalAmount;
    private BigDecimal depositAmount;
    private String cancelReason;
    private Integer cancelledBy;
    private Timestamp createdDate;
    private String notes;
    private Integer statusID;

    public Contract() {
    }

    public Contract(int contractID, String contractCode, Integer customerID, Integer staffID, Integer pickUpLocationID,
            Integer dropOffLocationID, Timestamp startDateTime, Timestamp endDateTime, BigDecimal totalAmount,
            BigDecimal depositAmount, String cancelReason, Integer cancelledBy, Timestamp createdDate,
            String notes, Integer statusID) {
        this.contractID = contractID;
        this.contractCode = contractCode;
        this.customerID = customerID;
        this.staffID = staffID;
        this.pickUpLocationID = pickUpLocationID;
        this.dropOffLocationID = dropOffLocationID;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.totalAmount = totalAmount;
        this.depositAmount = depositAmount;
        this.cancelReason = cancelReason;
        this.cancelledBy = cancelledBy;
        this.createdDate = createdDate;
        this.notes = notes;
        this.statusID = statusID;
    }

    public int getContractID() { return contractID; }
    public void setContractID(int contractID) { this.contractID = contractID; }

    public String getContractCode() { return contractCode; }
    public void setContractCode(String contractCode) { this.contractCode = contractCode; }

    public Integer getCustomerID() { return customerID; }
    public void setCustomerID(Integer customerID) { this.customerID = customerID; }

    public Integer getStaffID() { return staffID; }
    public void setStaffID(Integer staffID) { this.staffID = staffID; }

    public Integer getPickUpLocationID() { return pickUpLocationID; }
    public void setPickUpLocationID(Integer pickUpLocationID) { this.pickUpLocationID = pickUpLocationID; }

    public Integer getDropOffLocationID() { return dropOffLocationID; }
    public void setDropOffLocationID(Integer dropOffLocationID) { this.dropOffLocationID = dropOffLocationID; }

    public Timestamp getStartDateTime() { return startDateTime; }
    public void setStartDateTime(Timestamp startDateTime) { this.startDateTime = startDateTime; }

    public Timestamp getEndDateTime() { return endDateTime; }
    public void setEndDateTime(Timestamp endDateTime) { this.endDateTime = endDateTime; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getDepositAmount() { return depositAmount; }
    public void setDepositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public Integer getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(Integer cancelledBy) { this.cancelledBy = cancelledBy; }

    public Timestamp getCreatedDate() { return createdDate; }
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getStatusID() { return statusID; }
    public void setStatusID(Integer statusID) { this.statusID = statusID; }
}

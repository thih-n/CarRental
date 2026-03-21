package com.carrental.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Payment {
    private int paymentID;
    private Integer contractID;
    private BigDecimal amount;
    private Timestamp paymentDate;
    private String paymentMethod;
    private String paymentType;
    private String paymentStatus;
    private String transactionCode;
    private Integer refPaymentID;
    private String note;

    public Payment() {
    }

    public Payment(int paymentID, Integer contractID, BigDecimal amount, Timestamp paymentDate, String paymentMethod,
            String paymentType, String paymentStatus, String transactionCode, Integer refPaymentID, String note) {
        this.paymentID = paymentID;
        this.contractID = contractID;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.paymentType = paymentType;
        this.paymentStatus = paymentStatus;
        this.transactionCode = transactionCode;
        this.refPaymentID = refPaymentID;
        this.note = note;
    }

    public int getPaymentID() { return paymentID; }
    public void setPaymentID(int paymentID) { this.paymentID = paymentID; }

    public Integer getContractID() { return contractID; }
    public void setContractID(Integer contractID) { this.contractID = contractID; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Timestamp getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Timestamp paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getTransactionCode() { return transactionCode; }
    public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }

    public Integer getRefPaymentID() { return refPaymentID; }
    public void setRefPaymentID(Integer refPaymentID) { this.refPaymentID = refPaymentID; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

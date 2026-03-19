package com.carrental.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BookingPriceResult {
    private BigDecimal rentPrice;
    private BigDecimal driverFee;
    private BigDecimal totalAmount;
    private BigDecimal depositAmount;
    private Timestamp startDateTime;
    private Timestamp endDateTime;

    public BigDecimal getRentPrice() { return rentPrice; }
    public void setRentPrice(BigDecimal rentPrice) { this.rentPrice = rentPrice; }

    public BigDecimal getDriverFee() { return driverFee; }
    public void setDriverFee(BigDecimal driverFee) { this.driverFee = driverFee; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getDepositAmount() { return depositAmount; }
    public void setDepositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; }

    public Timestamp getStartDateTime() { return startDateTime; }
    public void setStartDateTime(Timestamp startDateTime) { this.startDateTime = startDateTime; }

    public Timestamp getEndDateTime() { return endDateTime; }
    public void setEndDateTime(Timestamp endDateTime) { this.endDateTime = endDateTime; }
}

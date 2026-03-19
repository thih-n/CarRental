package com.carrental.entity;

import java.math.BigDecimal;

public class AdminRevenuePoint {
    private String label;
    private BigDecimal totalAmount;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}

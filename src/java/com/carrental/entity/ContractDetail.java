package com.carrental.entity;

import java.math.BigDecimal;

public class ContractDetail {
    private int detailID;
    private Integer contractID;
    private Integer carID;
    private Boolean hasDriver;
    private Integer driverID;
    private BigDecimal rentPrice;
    private BigDecimal driverFee;
    private String detailStatus;
    private String driverStatus;

    public ContractDetail() {
    }

    public ContractDetail(int detailID, Integer contractID, Integer carID, Boolean hasDriver, Integer driverID,
            BigDecimal rentPrice, BigDecimal driverFee, String detailStatus, String driverStatus) {
        this.detailID = detailID;
        this.contractID = contractID;
        this.carID = carID;
        this.hasDriver = hasDriver;
        this.driverID = driverID;
        this.rentPrice = rentPrice;
        this.driverFee = driverFee;
        this.detailStatus = detailStatus;
        this.driverStatus = driverStatus;
    }

    public int getDetailID() { return detailID; }
    public void setDetailID(int detailID) { this.detailID = detailID; }

    public Integer getContractID() { return contractID; }
    public void setContractID(Integer contractID) { this.contractID = contractID; }

    public Integer getCarID() { return carID; }
    public void setCarID(Integer carID) { this.carID = carID; }

    public Boolean getHasDriver() { return hasDriver; }
    public void setHasDriver(Boolean hasDriver) { this.hasDriver = hasDriver; }

    public Integer getDriverID() { return driverID; }
    public void setDriverID(Integer driverID) { this.driverID = driverID; }

    public BigDecimal getRentPrice() { return rentPrice; }
    public void setRentPrice(BigDecimal rentPrice) { this.rentPrice = rentPrice; }

    public BigDecimal getDriverFee() { return driverFee; }
    public void setDriverFee(BigDecimal driverFee) { this.driverFee = driverFee; }

    public String getDetailStatus() { return detailStatus; }
    public void setDetailStatus(String detailStatus) { this.detailStatus = detailStatus; }

    public String getDriverStatus() { return driverStatus; }
    public void setDriverStatus(String driverStatus) { this.driverStatus = driverStatus; }
}

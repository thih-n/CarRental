package com.carrental.entity;

import java.sql.Timestamp;

public class DriverLeave {
    private int leaveID;
    private int driverID;
    private Timestamp leaveStart;
    private Timestamp leaveEnd;
    private String reason;
    private String status; // Approved, Pending, Rejected
    private Timestamp createdAt;
    private Integer approvedBy;
    private Timestamp approvedAt;

    public DriverLeave() {
    }

    public int getLeaveID() { return leaveID; }
    public void setLeaveID(int leaveID) { this.leaveID = leaveID; }

    public int getDriverID() { return driverID; }
    public void setDriverID(int driverID) { this.driverID = driverID; }

    public Timestamp getLeaveStart() { return leaveStart; }
    public void setLeaveStart(Timestamp leaveStart) { this.leaveStart = leaveStart; }

    public Timestamp getLeaveEnd() { return leaveEnd; }
    public void setLeaveEnd(Timestamp leaveEnd) { this.leaveEnd = leaveEnd; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Integer getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Integer approvedBy) { this.approvedBy = approvedBy; }

    public Timestamp getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Timestamp approvedAt) { this.approvedAt = approvedAt; }
}

package com.carrental.entity;

import java.util.List;

public class DriverScheduleDay {
    private java.sql.Date date;
    private List<DriverDayTrip> trips;
    private boolean hasLeave;
    private String leaveStatus;
    private boolean isToday;

    public DriverScheduleDay() {
    }

    public DriverScheduleDay(java.sql.Date date) {
        this.date = date;
    }

    public java.sql.Date getDate() { return date; }
    public void setDate(java.sql.Date date) { this.date = date; }

    public List<DriverDayTrip> getTrips() { return trips; }
    public void setTrips(List<DriverDayTrip> trips) { this.trips = trips; }

    public boolean isHasLeave() { return hasLeave; }
    public void setHasLeave(boolean hasLeave) { this.hasLeave = hasLeave; }

    public String getLeaveStatus() { return leaveStatus; }
    public void setLeaveStatus(String leaveStatus) { this.leaveStatus = leaveStatus; }

    public boolean isToday() { return isToday; }
    public void setToday(boolean isToday) { this.isToday = isToday; }

    public static class DriverDayTrip {
        private int contractID;
        private String contractCode;
        private String customerName;
        private String carName;
        private java.sql.Timestamp startDateTime;
        private java.sql.Timestamp endDateTime;
        private String detailStatus;
        private String scheduleStatus;

        public int getContractID() { return contractID; }
        public void setContractID(int contractID) { this.contractID = contractID; }

        public String getContractCode() { return contractCode; }
        public void setContractCode(String contractCode) { this.contractCode = contractCode; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public String getCarName() { return carName; }
        public void setCarName(String carName) { this.carName = carName; }

        public java.sql.Timestamp getStartDateTime() { return startDateTime; }
        public void setStartDateTime(java.sql.Timestamp startDateTime) { this.startDateTime = startDateTime; }

        public java.sql.Timestamp getEndDateTime() { return endDateTime; }
        public void setEndDateTime(java.sql.Timestamp endDateTime) { this.endDateTime = endDateTime; }

        public String getDetailStatus() { return detailStatus; }
        public void setDetailStatus(String detailStatus) { this.detailStatus = detailStatus; }

        public String getScheduleStatus() { return scheduleStatus; }
        public void setScheduleStatus(String scheduleStatus) { this.scheduleStatus = scheduleStatus; }
    }
}

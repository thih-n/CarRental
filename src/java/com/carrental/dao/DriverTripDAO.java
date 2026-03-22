package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.StaffBookingItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DriverTripDAO {

    public List<StaffBookingItem> getTripsByDriver(int driverID) {
        List<StaffBookingItem> items = new ArrayList<>();
        String sql = "SELECT c.ContractID, c.ContractCode, cu.FullName AS CustomerName, car.CarName, "
                + "du.FullName AS DriverName, c.StartDateTime, c.EndDateTime, cd.DetailStatus, cs.ScheduleStatus "
                + "FROM Contracts c "
                + "JOIN ContractDetails cd ON cd.ContractID = c.ContractID "
                + "JOIN Cars car ON car.CarID = cd.CarID "
                + "LEFT JOIN Users cu ON cu.UserID = c.CustomerID "
                + "LEFT JOIN Users du ON du.UserID = cd.DriverID "
                + "LEFT JOIN CarSchedules cs ON cs.ContractID = c.ContractID "
                + "WHERE cd.DriverID = ? AND cd.DetailStatus IN ('Scheduled','InUse') "
                + "ORDER BY c.StartDateTime ASC";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StaffBookingItem item = new StaffBookingItem();
                    item.setContractID(rs.getInt("ContractID"));
                    item.setContractCode(rs.getString("ContractCode"));
                    item.setCustomerName(rs.getString("CustomerName"));
                    item.setCarName(rs.getString("CarName"));
                    item.setDriverName(rs.getString("DriverName"));
                    item.setStartDateTime(rs.getTimestamp("StartDateTime"));
                    item.setEndDateTime(rs.getTimestamp("EndDateTime"));
                    item.setDetailStatus(rs.getString("DetailStatus"));
                    item.setScheduleStatus(rs.getString("ScheduleStatus"));
                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public boolean canStartTrip(int contractID, int driverID) {
        String sql = "SELECT COUNT(*) FROM ContractDetails "
                + "WHERE ContractID = ? AND DriverID = ? AND DetailStatus = 'Scheduled'";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractID);
            ps.setInt(2, driverID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean canCompleteTrip(int contractID, int driverID) {
        String sql = "SELECT COUNT(*) FROM ContractDetails "
                + "WHERE ContractID = ? AND DriverID = ? AND DetailStatus = 'InUse'";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractID);
            ps.setInt(2, driverID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<StaffBookingItem> getTripsByDriverAndDate(int driverID, java.sql.Date startDate, java.sql.Date endDate) {
        List<StaffBookingItem> items = new ArrayList<>();
        String sql = "SELECT c.ContractID, c.ContractCode, cu.FullName AS CustomerName, car.CarName, "
                + "du.FullName AS DriverName, c.StartDateTime, c.EndDateTime, cd.DetailStatus, cs.ScheduleStatus "
                + "FROM Contracts c "
                + "JOIN ContractDetails cd ON cd.ContractID = c.ContractID "
                + "JOIN Cars car ON car.CarID = cd.CarID "
                + "LEFT JOIN Users cu ON cu.UserID = c.CustomerID "
                + "LEFT JOIN Users du ON du.UserID = cd.DriverID "
                + "LEFT JOIN CarSchedules cs ON cs.ContractID = c.ContractID "
                + "WHERE cd.DriverID = ? "
                + "AND c.StartDateTime >= ? "
                + "AND c.StartDateTime < ? "
                + "ORDER BY c.StartDateTime ASC";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StaffBookingItem item = new StaffBookingItem();
                    item.setContractID(rs.getInt("ContractID"));
                    item.setContractCode(rs.getString("ContractCode"));
                    item.setCustomerName(rs.getString("CustomerName"));
                    item.setCarName(rs.getString("CarName"));
                    item.setDriverName(rs.getString("DriverName"));
                    item.setStartDateTime(rs.getTimestamp("StartDateTime"));
                    item.setEndDateTime(rs.getTimestamp("EndDateTime"));
                    item.setDetailStatus(rs.getString("DetailStatus"));
                    item.setScheduleStatus(rs.getString("ScheduleStatus"));
                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<StaffBookingItem> getTripsByDriverForMonth(int driverID, int year, int month) {
        List<StaffBookingItem> items = new ArrayList<>();
        String sql = "SELECT c.ContractID, c.ContractCode, cu.FullName AS CustomerName, car.CarName, "
                + "du.FullName AS DriverName, c.StartDateTime, c.EndDateTime, cd.DetailStatus, cs.ScheduleStatus, "
                + "CAST(c.StartDateTime AS DATE) AS TripDate "
                + "FROM Contracts c "
                + "JOIN ContractDetails cd ON cd.ContractID = c.ContractID "
                + "JOIN Cars car ON car.CarID = cd.CarID "
                + "LEFT JOIN Users cu ON cu.UserID = c.CustomerID "
                + "LEFT JOIN Users du ON du.UserID = cd.DriverID "
                + "LEFT JOIN CarSchedules cs ON cs.ContractID = c.ContractID "
                + "WHERE cd.DriverID = ? "
                + "AND YEAR(c.StartDateTime) = ? "
                + "AND MONTH(c.StartDateTime) = ? "
                + "ORDER BY c.StartDateTime ASC";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            ps.setInt(2, year);
            ps.setInt(3, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StaffBookingItem item = new StaffBookingItem();
                    item.setContractID(rs.getInt("ContractID"));
                    item.setContractCode(rs.getString("ContractCode"));
                    item.setCustomerName(rs.getString("CustomerName"));
                    item.setCarName(rs.getString("CarName"));
                    item.setDriverName(rs.getString("DriverName"));
                    item.setStartDateTime(rs.getTimestamp("StartDateTime"));
                    item.setEndDateTime(rs.getTimestamp("EndDateTime"));
                    item.setDetailStatus(rs.getString("DetailStatus"));
                    item.setScheduleStatus(rs.getString("ScheduleStatus"));
                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }
}

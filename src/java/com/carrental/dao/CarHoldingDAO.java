package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.CarHolding;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CarHoldingDAO {

    public boolean createHolding(CarHolding holding) {
        String sql = "INSERT INTO CarHoldings (CarID, DriverID, UserID, RentStartDateTime, RentEndDateTime, ExpiryTime, CreatedAt) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, holding.getCarID());
            if (holding.getDriverID() != null) {
                ps.setInt(2, holding.getDriverID());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setInt(3, holding.getUserID());
            ps.setTimestamp(4, holding.getRentStartDateTime());
            ps.setTimestamp(5, holding.getRentEndDateTime());
            ps.setTimestamp(6, holding.getExpiryTime());
            ps.setTimestamp(7, holding.getCreatedAt());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public CarHolding getHoldingByCar(int carID) {
        String sql = "SELECT * FROM CarHoldings WHERE CarID = ? AND ExpiryTime > GETDATE()";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapHolding(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CarHolding getHoldingByCarAndUser(int carID, int userID) {
        String sql = "SELECT * FROM CarHoldings WHERE CarID = ? AND UserID = ? AND ExpiryTime > GETDATE()";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carID);
            ps.setInt(2, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapHolding(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteHolding(int holdingID) {
        String sql = "DELETE FROM CarHoldings WHERE HoldingID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, holdingID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteHoldingByCar(int carID) {
        String sql = "DELETE FROM CarHoldings WHERE CarID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteExpiredHoldings() {
        String sql = "DELETE FROM CarHoldings WHERE ExpiryTime <= GETDATE()";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isCarOnHold(int carID) {
        return getHoldingByCar(carID) != null;
    }

    public boolean isCarOnHoldByOther(int carID, int userID) {
        String sql = "SELECT 1 FROM CarHoldings WHERE CarID = ? AND UserID != ? AND ExpiryTime > GETDATE()";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carID);
            ps.setInt(2, userID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if car is available (not in CarSchedules AND not in CarHoldings)
     */
    public boolean isCarAvailableForBooking(int carID, Timestamp startDateTime, Timestamp endDateTime) {
        // Check CarSchedules
        String checkSchedule = "SELECT 1 FROM CarSchedules WHERE CarID = ? AND ScheduleStatus IN ('Booked','InUse','Maintenance') AND (? < EndDateTime AND ? > StartDateTime)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSchedule)) {
            ps.setInt(1, carID);
            ps.setTimestamp(2, startDateTime);
            ps.setTimestamp(3, endDateTime);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return false; // Car is booked in CarSchedules
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // Check CarHoldings
        String checkHolding = "SELECT 1 FROM CarHoldings WHERE CarID = (? ) AND ExpiryTime > GETDATE() AND (? < RentEndDateTime AND ? > RentStartDateTime)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(checkHolding)) {
            ps.setInt(1, carID);
            ps.setTimestamp(2, startDateTime);
            ps.setTimestamp(3, endDateTime);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return false; // Car is on hold
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private CarHolding mapHolding(ResultSet rs) throws Exception {
        CarHolding h = new CarHolding();
        h.setHoldingID(rs.getInt("HoldingID"));
        h.setCarID(rs.getInt("CarID"));
        h.setDriverID((Integer) rs.getObject("DriverID"));
        h.setUserID(rs.getInt("UserID"));
        h.setRentStartDateTime(rs.getTimestamp("RentStartDateTime"));
        h.setRentEndDateTime(rs.getTimestamp("RentEndDateTime"));
        h.setExpiryTime(rs.getTimestamp("ExpiryTime"));
        h.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return h;
    }
}

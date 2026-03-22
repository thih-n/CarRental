package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.DriverProfile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for DriverLeaves table and driver availability checks
 */
public class DriverLeaveDAO {

    /**
     * Check if a driver is available for a specific time range.
     * Uses the "Gold Formula": TimeA < @CheckEnd AND TimeB > @CheckStart
     * to detect overlapping schedules.
     *
     * @param driverID The driver ID to check
     * @param checkStart Start of the desired booking period
     * @param checkEnd End of the desired booking period
     * @return true if driver is available (no conflicts), false if busy
     */
    public boolean isDriverAvailable(int driverID, Timestamp checkStart, Timestamp checkEnd) {
        // Filter 1: Check Contracts
        if (isDriverBusyInContracts(driverID, checkStart, checkEnd)) {
            return false;
        }

        // Filter 2: Check CarHoldings (reserved bookings)
        if (isDriverHoldingInCarHoldings(driverID, checkStart, checkEnd)) {
            return false;
        }

        // Filter 3: Check DriverLeaves
        if (isDriverOnLeave(driverID, checkStart, checkEnd)) {
            return false;
        }

        return true;
    }

    /**
     * Filter 1: Check if driver has any active contracts in the given time range
     * Only checks contracts with DetailStatus = 'Booked' or 'InUse'
     */
    private boolean isDriverBusyInContracts(int driverID, Timestamp checkStart, Timestamp checkEnd) {
        String sql = "SELECT COUNT(*) FROM ContractDetails cd "
                + "JOIN Contracts c ON c.ContractID = cd.ContractID "
                + "WHERE cd.DriverID = ? "
                + "  AND cd.DetailStatus IN ('Booked', 'InUse') "
                + "  AND c.StartDateTime < ? "  // TimeA < @CheckEnd
                + "  AND c.EndDateTime > ?";    // TimeB > @CheckStart

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            ps.setTimestamp(2, checkEnd);
            ps.setTimestamp(3, checkStart);
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

    /**
     * Filter 2: Check if driver has any active car holdings in the given time range
     * Only checks holdings that haven't expired
     */
    private boolean isDriverHoldingInCarHoldings(int driverID, Timestamp checkStart, Timestamp checkEnd) {
        String sql = "SELECT COUNT(*) FROM CarHoldings ch "
                + "WHERE ch.DriverID = ? "
                + "  AND ch.ExpiryTime > GETDATE() "  // Not expired
                + "  AND ch.rentStartDateTime < ? "   // TimeA < @CheckEnd
                + "  AND ch.rentEndDateTime > ?";     // TimeB > @CheckStart

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            ps.setTimestamp(2, checkEnd);
            ps.setTimestamp(3, checkStart);
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

    /**
     * Filter 3: Check if driver has any leave (Approved or Pending) in the given time range
     * Drivers with pending leave requests should not be available for booking
     */
    private boolean isDriverOnLeave(int driverID, Timestamp checkStart, Timestamp checkEnd) {
        String sql = "SELECT COUNT(*) FROM DriverLeaves dl "
                + "WHERE dl.DriverID = ? "
                + "  AND dl.Status IN ('Approved', 'Pending') "
                + "  AND dl.LeaveStart < ? "   // TimeA < @CheckEnd
                + "  AND dl.LeaveEnd > ?";     // TimeB > @CheckStart

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            ps.setTimestamp(2, checkEnd);
            ps.setTimestamp(3, checkStart);
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

    /**
     * Get all available drivers for a specific location and time range.
     * Applies all 3 filters.
     *
     * @param pickUpLocationID The pickup location ID
     * @param checkStart Start of the booking period
     * @param checkEnd End of the booking period
     * @return List of available drivers
     */
    public List<DriverProfile> getAvailableDriversForBooking(int pickUpLocationID, Timestamp checkStart, Timestamp checkEnd) {
        List<DriverProfile> availableDrivers = new ArrayList<>();

        // First get all drivers at this location who are "Available" in their status
        String baseSql = "SELECT dp.*, u.FullName FROM DriverProfiles dp "
                + "JOIN Users u ON u.UserID = dp.DriverID "
                + "WHERE dp.BaseLocationID = ? "
                + "AND dp.DriverStatus = 'Available' "
                + "AND u.IsActive = 1";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(baseSql)) {
            ps.setInt(1, pickUpLocationID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int driverID = rs.getInt("DriverID");

                    // Apply the 3 filters
                    if (isDriverAvailable(driverID, checkStart, checkEnd)) {
                        DriverProfile d = new DriverProfile();
                        d.setDriverID(rs.getInt("DriverID"));
                        d.setLicenseNumber(rs.getString("LicenseNumber"));
                        d.setLicenseExpiry(rs.getDate("LicenseExpiry"));
                        d.setExperienceYears((Integer) rs.getObject("ExperienceYears"));
                        d.setRating(rs.getBigDecimal("Rating"));
                        d.setTotalTrips((Integer) rs.getObject("TotalTrips"));
                        d.setDriverStatus(rs.getString("DriverStatus"));
                        d.setBaseLocationID((Integer) rs.getObject("BaseLocationID"));
                        d.setBaseSalary(rs.getBigDecimal("BaseSalary"));
                        d.setCommissionRate(rs.getBigDecimal("CommissionRate"));
                        d.setHourlyRate(rs.getBigDecimal("HourlyRate"));
                        d.setDailyRate(rs.getBigDecimal("DailyRate"));
                        d.setFullName(rs.getString("FullName"));
                        availableDrivers.add(d);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return availableDrivers;
    }

    /**
     * Check if driver is currently on a trip (for real-time status display)
     * This is a simplified check for the DriverStatus column
     *
     * @param driverID The driver ID
     * @return true if driver is on trip, false otherwise
     */
    public boolean isDriverOnTrip(int driverID) {
        String sql = "SELECT DriverStatus FROM DriverProfiles WHERE DriverID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("DriverStatus");
                    return "OnTrip".equals(status);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

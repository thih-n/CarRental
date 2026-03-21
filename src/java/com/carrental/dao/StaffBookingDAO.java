package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.StaffBookingItem;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class StaffBookingDAO {

    /**
     * Get bookings with optional filtering by status and search by contract code
     * @param statusFilter Specific status to filter (Booked, InUse, Completed, Cancelled) or null for all
     * @param searchCode Contract code to search (partial match) or null for all
     */
    public List<StaffBookingItem> getBookingsWithFilter(String statusFilter, String searchCode) {
        List<StaffBookingItem> items = new ArrayList<>();
        String sql = "SELECT c.ContractID, c.ContractCode, c.CustomerID, c.DropOffLocationID, cu.FullName AS CustomerName, car.CarName, "
                + "du.FullName AS DriverName, c.StartDateTime, c.EndDateTime, cd.DetailStatus, cs.ScheduleStatus, cd.DriverID, cd.CarID, cd.HasDriver "
                + "FROM Contracts c "
                + "JOIN ContractDetails cd ON cd.ContractID = c.ContractID "
                + "JOIN Cars car ON car.CarID = cd.CarID "
                + "LEFT JOIN Users cu ON cu.UserID = c.CustomerID "
                + "LEFT JOIN Users du ON du.UserID = cd.DriverID "
                + "LEFT JOIN CarSchedules cs ON cs.ContractID = c.ContractID "
                + "WHERE 1=1 ";

        // Add status filter if provided
        if (statusFilter != null && !statusFilter.isEmpty() && !"all".equalsIgnoreCase(statusFilter)) {
            sql += "AND cd.DetailStatus = ? ";
        } else if (statusFilter == null || statusFilter.isEmpty()) {
            // Default filter - only show Booked when no filter provided
            sql += "AND cd.DetailStatus = 'Booked' ";
        }
        // If "all" is selected, don't add any status filter

        // Add search by contract code if provided
        if (searchCode != null && !searchCode.trim().isEmpty()) {
            sql += "AND c.ContractCode LIKE ? ";
        }

        sql += "ORDER BY c.StartDateTime ASC";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramIndex = 1;

            // Set status filter parameter if provided
            if (statusFilter != null && !statusFilter.isEmpty() && !"all".equalsIgnoreCase(statusFilter)) {
                ps.setString(paramIndex++, statusFilter);
            }

            // Set search code parameter if provided
            if (searchCode != null && !searchCode.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchCode.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StaffBookingItem item = new StaffBookingItem();
                    item.setContractID(rs.getInt("ContractID"));
                    item.setContractCode(rs.getString("ContractCode"));
                    item.setCustomerID((Integer) rs.getObject("CustomerID"));
                    item.setCustomerName(rs.getString("CustomerName"));
                    item.setCarName(rs.getString("CarName"));
                    item.setDriverName(rs.getString("DriverName"));
                    item.setDriverID((Integer) rs.getObject("DriverID"));
                    item.setCarID((Integer) rs.getObject("CarID"));
                    item.setDropOffLocationID((Integer) rs.getObject("DropOffLocationID"));
                    item.setStartDateTime(rs.getTimestamp("StartDateTime"));
                    item.setEndDateTime(rs.getTimestamp("EndDateTime"));
                    item.setDetailStatus(rs.getString("DetailStatus"));
                    item.setScheduleStatus(rs.getString("ScheduleStatus"));
                    item.setHasDriver(rs.getBoolean("HasDriver"));
                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Get all upcoming bookings (legacy method for backward compatibility)
     */
    public List<StaffBookingItem> getUpcomingBookings() {
        return getBookingsWithFilter(null, null);
    }

    public boolean canStartTrip(int contractID) {
        String sql = "SELECT COUNT(*) "
                + "FROM ContractDetails cd "
                + "JOIN Contracts c ON c.ContractID = cd.ContractID "
                + "WHERE cd.ContractID = ? "
                + "  AND cd.DetailStatus = 'Booked' "
                + "  AND c.EndDateTime > c.StartDateTime";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public StaffBookingItem getBookingById(int contractId) {
        String sql = "SELECT c.ContractID, c.ContractCode, c.CustomerID, c.DropOffLocationID, cu.FullName AS CustomerName, cu.PhoneNumber AS CustomerPhone, cu.Email AS CustomerEmail, "
                + "car.CarID, car.CarName, car.PlateNumber, "
                + "du.FullName AS DriverName, du.PhoneNumber AS DriverPhone, du.Email AS DriverEmail, "
                + "c.StartDateTime, c.EndDateTime, cd.DetailStatus, cs.ScheduleStatus, cd.DriverID, cd.HasDriver, "
                + "l1.LocationName AS PickupLocation, l2.LocationName AS DropoffLocation "
                + "FROM Contracts c "
                + "JOIN ContractDetails cd ON cd.ContractID = c.ContractID "
                + "JOIN Cars car ON car.CarID = cd.CarID "
                + "LEFT JOIN Users cu ON cu.UserID = c.CustomerID "
                + "LEFT JOIN Users du ON du.UserID = cd.DriverID "
                + "LEFT JOIN CarSchedules cs ON cs.ContractID = c.ContractID "
                + "LEFT JOIN Locations l1 ON l1.LocationID = c.PickUpLocationID "
                + "LEFT JOIN Locations l2 ON l2.LocationID = c.DropOffLocationID "
                + "WHERE c.ContractID = ?";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StaffBookingItem item = new StaffBookingItem();
                    item.setContractID(rs.getInt("ContractID"));
                    item.setContractCode(rs.getString("ContractCode"));
                    item.setCustomerID((Integer) rs.getObject("CustomerID"));
                    item.setCustomerName(rs.getString("CustomerName"));
                    item.setCarID((Integer) rs.getObject("CarID"));
                    item.setCarName(rs.getString("CarName"));
                    item.setLicensePlate(rs.getString("PlateNumber"));
                    item.setDriverName(rs.getString("DriverName"));
                    item.setDriverID((Integer) rs.getObject("DriverID"));
                    item.setDriverPhone(rs.getString("DriverPhone"));
                    item.setDriverEmail(rs.getString("DriverEmail"));
                    item.setCustomerPhone(rs.getString("CustomerPhone"));
                    item.setCustomerEmail(rs.getString("CustomerEmail"));
                    item.setStartDateTime(rs.getTimestamp("StartDateTime"));
                    item.setEndDateTime(rs.getTimestamp("EndDateTime"));
                    item.setDetailStatus(rs.getString("DetailStatus"));
                    item.setScheduleStatus(rs.getString("ScheduleStatus"));
                    item.setHasDriver(rs.getBoolean("HasDriver"));
                    item.setPickupLocation(rs.getString("PickupLocation"));
                    item.setDropoffLocation(rs.getString("DropoffLocation"));
                    return item;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean needsKycForTrip(int contractID) {
        String sql = "SELECT cd.HasDriver "
                + "FROM ContractDetails cd "
                + "WHERE cd.ContractID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Boolean hasDriver = rs.getBoolean("HasDriver");
                    return hasDriver == null || !hasDriver;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean canCompleteTrip(int contractID) {
        String sql = "SELECT COUNT(*) "
                + "FROM ContractDetails cd "
                + "WHERE cd.ContractID = ? "
                + "  AND cd.DetailStatus = 'InUse'";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean completeTrip(int contractID, String staffNote) {
        String updateDetail = "UPDATE ContractDetails SET DetailStatus = 'Completed', DriverStatus = 'Completed' WHERE ContractID = ?";
        String updateSchedule = "UPDATE CarSchedules SET ScheduleStatus = 'Completed' WHERE ContractID = ?";
        String updateContract = "UPDATE Contracts SET StatusID = 3 WHERE ContractID = ?"; // 3 = Completed status
        String updateContractNote = "UPDATE Contracts SET Notes = CASE WHEN Notes IS NULL OR LTRIM(RTRIM(Notes)) = '' THEN ? ELSE Notes + CHAR(10) + ? END WHERE ContractID = ?";

        try (Connection conn = new DBContext().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(updateDetail);
                 PreparedStatement ps2 = conn.prepareStatement(updateSchedule);
                 PreparedStatement ps3 = conn.prepareStatement(updateContract)) {

                ps1.setInt(1, contractID);
                ps1.executeUpdate();

                ps2.setInt(1, contractID);
                ps2.executeUpdate();

                ps3.setInt(1, contractID);
                ps3.executeUpdate();

                if (staffNote != null && !staffNote.trim().isEmpty()) {
                    try (PreparedStatement ps4 = conn.prepareStatement(updateContractNote)) {
                        String note = staffNote.trim();
                        ps4.setString(1, note);
                        ps4.setString(2, note);
                        ps4.setInt(3, contractID);
                        ps4.executeUpdate();
                    } catch (Exception ex) {
                        // Skip note update if column is missing
                    }
                }

                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean cancelBooking(int contractID, String cancelReason, int cancelledByUserID) {
        // Get contract details first for refund calculation
        BigDecimal refundAmount = BigDecimal.ZERO;
        Integer depositPaymentID = null;
        
        String getDepositSql = "SELECT p.PaymentID, p.Amount FROM Payments p "
                + "JOIN Contracts c ON p.ContractID = c.ContractID "
                + "WHERE c.ContractID = ? AND p.PaymentType = 'Deposit' AND p.PaymentStatus = 'Completed'";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(getDepositSql)) {
            ps.setInt(1, contractID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    depositPaymentID = rs.getInt("PaymentID");
                    refundAmount = rs.getBigDecimal("Amount");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Cancel the contract and update CarSchedules to Cancelled
        String updateContract = "UPDATE Contracts SET StatusID = 4, CancelReason = ?, CancelledBy = ? WHERE ContractID = ?";
        String updateDetail = "UPDATE ContractDetails SET DetailStatus = 'Cancelled' WHERE ContractID = ?";
        String updateSchedule = "UPDATE CarSchedules SET ScheduleStatus = 'Cancelled' WHERE ContractID = ?";
        
        // Create refund record
        String insertRefund = "INSERT INTO Payments (ContractID, Amount, PaymentDate, PaymentMethod, PaymentStatus, PaymentType, RefPaymentID, Note) "
                + "VALUES (?, ?, GETDATE(), 'Bank Transfer', 'Pending', 'Refund', ?, ?)";

        try (Connection conn = new DBContext().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(updateContract);
                 PreparedStatement ps2 = conn.prepareStatement(updateDetail);
                 PreparedStatement ps3 = conn.prepareStatement(updateSchedule);
                 PreparedStatement psRefund = conn.prepareStatement(insertRefund)) {

                ps1.setString(1, cancelReason);
                ps1.setInt(2, cancelledByUserID);
                ps1.setInt(3, contractID);
                ps1.executeUpdate();

                ps2.setInt(1, contractID);
                ps2.executeUpdate();

                ps3.setInt(1, contractID);
                ps3.executeUpdate();
                
                // Insert refund record if there's a deposit to refund
                if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
                    psRefund.setInt(1, contractID);
                    psRefund.setBigDecimal(2, refundAmount);
                    if (depositPaymentID != null) {
                        psRefund.setInt(3, depositPaymentID);
                    } else {
                        psRefund.setNull(3, Types.INTEGER);
                    }
                    psRefund.setString(4, "Hủy đơn - Hoàn tiền đặt cọc");
                    psRefund.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

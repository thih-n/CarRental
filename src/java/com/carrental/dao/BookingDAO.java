package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.CarSchedule;
import com.carrental.entity.Contract;
import com.carrental.entity.ContractDetail;
import com.carrental.entity.StaffBookingItem;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public int createBookingTransaction(Contract contract, ContractDetail detail, CarSchedule schedule, Integer driverID, com.carrental.entity.Payment payment) {
        String insertContractSql = "INSERT INTO Contracts (ContractCode, CustomerID, StaffID, PickUpLocationID, DropOffLocationID, "
                + "StartDateTime, EndDateTime, TotalAmount, DepositAmount, CancelReason, CancelledBy, CreatedDate, Notes, StatusID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String insertDetailSql = "INSERT INTO ContractDetails (ContractID, CarID, HasDriver, DriverID, RentPrice, DriverFee, DetailStatus, DriverStatus) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        String insertScheduleSql = "INSERT INTO CarSchedules (CarID, ContractID, StartDateTime, EndDateTime, ScheduleStatus, CreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        String insertPaymentSql = "INSERT INTO Payments (ContractID, Amount, PaymentDate, PaymentMethod, PaymentType, PaymentStatus, Note) "
                + "VALUES (?, ?, GETDATE(), ?, ?, ?, ?)";

        try (Connection conn = new DBContext().getConnection()) {
            conn.setAutoCommit(false);

            int contractID;
            try (PreparedStatement ps = conn.prepareStatement(insertContractSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, contract.getContractCode());
                ps.setObject(2, contract.getCustomerID());
                ps.setObject(3, contract.getStaffID());
                ps.setObject(4, contract.getPickUpLocationID());
                ps.setObject(5, contract.getDropOffLocationID());
                ps.setTimestamp(6, contract.getStartDateTime());
                ps.setTimestamp(7, contract.getEndDateTime());
                ps.setBigDecimal(8, contract.getTotalAmount());
                ps.setBigDecimal(9, contract.getDepositAmount());
                ps.setString(10, contract.getCancelReason());
                ps.setObject(11, contract.getCancelledBy());
                ps.setTimestamp(12, contract.getCreatedDate());
                ps.setString(13, contract.getNotes());
                ps.setObject(14, contract.getStatusID());
                int inserted = ps.executeUpdate();
                if (inserted == 0) {
                    conn.rollback();
                    return -1;
                }

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return -1;
                    }
                    contractID = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(insertDetailSql)) {
                ps.setInt(1, contractID);
                ps.setObject(2, detail.getCarID());
                ps.setObject(3, detail.getHasDriver());
                ps.setObject(4, detail.getDriverID());
                ps.setBigDecimal(5, detail.getRentPrice());
                ps.setBigDecimal(6, detail.getDriverFee());
                ps.setString(7, detail.getDetailStatus());
                ps.setString(8, detail.getDriverStatus());
                int inserted = ps.executeUpdate();
                if (inserted == 0) {
                    conn.rollback();
                    return -1;
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(insertScheduleSql)) {
                ps.setInt(1, schedule.getCarID());
                ps.setInt(2, contractID);
                ps.setTimestamp(3, schedule.getStartDateTime());
                ps.setTimestamp(4, schedule.getEndDateTime());
                ps.setString(5, schedule.getScheduleStatus());
                ps.setTimestamp(6, schedule.getCreatedAt());
                int inserted = ps.executeUpdate();
                if (inserted == 0) {
                    conn.rollback();
                    return -1;
                }
            }

            if (payment != null) {
                try (PreparedStatement ps = conn.prepareStatement(insertPaymentSql)) {
                    ps.setInt(1, contractID);
                    ps.setBigDecimal(2, payment.getAmount());
                    ps.setString(3, payment.getPaymentMethod());
                    ps.setString(4, payment.getPaymentType());
                    ps.setString(5, payment.getPaymentStatus());
                    ps.setString(6, payment.getNote());
                    int inserted = ps.executeUpdate();
                    if (inserted == 0) {
                        conn.rollback();
                        return -1;
                    }
                }
            }

            conn.commit();
            return contractID;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public List<StaffBookingItem> getBookingsByDriverId(int driverID) {
        List<StaffBookingItem> bookings = new ArrayList<>();
        String sql = "SELECT c.ContractID, c.ContractCode, c.CustomerID, u.FullName as CustomerName, u.Email as CustomerEmail, "
                + "c.StartDateTime, c.EndDateTime, cd.DetailStatus, cd.DriverID, cd.HasDriver "
                + "FROM Contracts c "
                + "JOIN ContractDetails cd ON c.ContractID = cd.ContractID "
                + "LEFT JOIN Users u ON u.UserID = c.CustomerID "
                + "WHERE cd.DriverID = ? "
                + "ORDER BY c.StartDateTime DESC";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StaffBookingItem item = new StaffBookingItem();
                    item.setContractID(rs.getInt("ContractID"));
                    item.setContractCode(rs.getString("ContractCode"));
                    item.setCustomerID(rs.getInt("CustomerID"));
                    item.setCustomerName(rs.getString("CustomerName"));
                    item.setCustomerEmail(rs.getString("CustomerEmail"));
                    item.setStartDateTime(rs.getTimestamp("StartDateTime"));
                    item.setEndDateTime(rs.getTimestamp("EndDateTime"));
                    item.setDetailStatus(rs.getString("DetailStatus"));
                    item.setDriverID(rs.getInt("DriverID"));
                    item.setHasDriver(rs.getBoolean("HasDriver"));
                    
                    // Map DetailStatus to display name
                    String detailStatus = rs.getString("DetailStatus");
                    if (detailStatus == null) {
                        detailStatus = "Unknown";
                    }
                    item.setStatusName(detailStatus);
                    
                    // Keep scheduleStatus for filtering
                    if ("Booked".equals(detailStatus)) {
                        item.setScheduleStatus("Pending");
                    } else if ("InUse".equals(detailStatus) || "In Progress".equals(detailStatus)) {
                        item.setScheduleStatus("In Progress");
                    } else if ("Completed".equals(detailStatus)) {
                        item.setScheduleStatus("Completed");
                    } else if ("Cancelled".equals(detailStatus)) {
                        item.setScheduleStatus("Cancelled");
                    } else {
                        item.setScheduleStatus(detailStatus);
                    }
                    
                    bookings.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public List<StaffBookingItem> getBookingsByDriverAndRange(int driverID, java.sql.Timestamp start, java.sql.Timestamp end) {
        List<StaffBookingItem> bookings = new ArrayList<>();
        String sql = "SELECT c.ContractID, c.ContractCode, c.CustomerID, u.FullName as CustomerName, u.Email as CustomerEmail, "
                + "c.StartDateTime, c.EndDateTime, cd.DetailStatus, cd.DriverID "
                + "FROM Contracts c "
                + "JOIN ContractDetails cd ON c.ContractID = cd.ContractID "
                + "LEFT JOIN Users u ON u.UserID = c.CustomerID "
                + "WHERE cd.DriverID = ? "
                + "AND c.StartDateTime < ? "
                + "AND c.EndDateTime > ? "
                + "ORDER BY c.StartDateTime DESC";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            ps.setTimestamp(2, end);
            ps.setTimestamp(3, start);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StaffBookingItem item = new StaffBookingItem();
                    item.setContractID(rs.getInt("ContractID"));
                    item.setContractCode(rs.getString("ContractCode"));
                    item.setCustomerID(rs.getInt("CustomerID"));
                    item.setCustomerName(rs.getString("CustomerName"));
                    item.setCustomerEmail(rs.getString("CustomerEmail"));
                    item.setStartDateTime(rs.getTimestamp("StartDateTime"));
                    item.setEndDateTime(rs.getTimestamp("EndDateTime"));
                    item.setDetailStatus(rs.getString("DetailStatus"));
                    item.setDriverID(rs.getInt("DriverID"));
                    bookings.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public List<StaffBookingItem> getBookingsByCustomerId(int customerID) {
        List<StaffBookingItem> bookings = new ArrayList<>();
        String sql = "SELECT c.ContractID, c.ContractCode, c.CustomerID, u.FullName as CustomerName, u.Email as CustomerEmail, "
                + "c.StartDateTime, c.EndDateTime, cd.DetailStatus, cd.DriverID "
                + "FROM Contracts c "
                + "JOIN ContractDetails cd ON c.ContractID = cd.ContractID "
                + "LEFT JOIN Users u ON u.UserID = c.CustomerID "
                + "WHERE c.CustomerID = ? "
                + "ORDER BY c.StartDateTime DESC";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StaffBookingItem item = new StaffBookingItem();
                    item.setContractID(rs.getInt("ContractID"));
                    item.setContractCode(rs.getString("ContractCode"));
                    item.setCustomerID(rs.getInt("CustomerID"));
                    item.setCustomerName(rs.getString("CustomerName"));
                    item.setCustomerEmail(rs.getString("CustomerEmail"));
                    item.setDriverID(rs.getInt("DriverID"));
                    item.setStartDateTime(rs.getTimestamp("StartDateTime"));
                    item.setEndDateTime(rs.getTimestamp("EndDateTime"));
                    item.setDetailStatus(rs.getString("DetailStatus"));
                    item.setStatusName(rs.getString("DetailStatus"));
                    bookings.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public boolean cancelBooking(int contractID, String cancelReason, Integer cancelledBy) {
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
        
        // Try to get cancelled status from ContractStatuses
        int cancelledStatusID = getStatusID("Cancelled");
        if (cancelledStatusID <= 0) {
            cancelledStatusID = getStatusID("Cancelled");
            if (cancelledStatusID <= 0) {
                cancelledStatusID = 6;
            }
        }
        
        // Update Contracts table
        String sql = "UPDATE Contracts SET StatusID = ?, CancelReason = ?, CancelledBy = ? WHERE ContractID = ?";
        
        // Also update ContractDetails
        String detailSql = "UPDATE ContractDetails SET DetailStatus = 'Cancelled' WHERE ContractID = ?";
        
        // Update CarSchedules
        String scheduleSql = "UPDATE CarSchedules SET ScheduleStatus = 'Cancelled' WHERE ContractID = ?";
        
        // Create refund record
        String insertRefund = "INSERT INTO Payments (ContractID, Amount, PaymentDate, PaymentMethod, PaymentStatus, PaymentType, RefPaymentID, Note) "
                + "VALUES (?, ?, GETDATE(), 'Bank Transfer', 'Pending', 'Refund', ?, ?)";

        try (Connection conn = new DBContext().getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, cancelledStatusID);
                ps.setString(2, cancelReason);
                if (cancelledBy != null) {
                    ps.setInt(3, cancelledBy);
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                ps.setInt(4, contractID);
                ps.executeUpdate();
            }
            
            try (PreparedStatement ps = conn.prepareStatement(detailSql)) {
                ps.setInt(1, contractID);
                ps.executeUpdate();
            }
            
            try (PreparedStatement ps = conn.prepareStatement(scheduleSql)) {
                ps.setInt(1, contractID);
                ps.executeUpdate();
            }
            
            // Insert refund record if there's a deposit to refund
            if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
                try (PreparedStatement psRefund = conn.prepareStatement(insertRefund)) {
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
            }
            
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private int getStatusID(String statusName) {
        // Try ContractStatuses first
        String sql = "SELECT StatusID FROM ContractStatuses WHERE StatusName = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("StatusID");
                }
            }
        } catch (Exception e) {
            // Try next approach
        }
        
        // Try BookingStatus
        sql = "SELECT StatusID FROM BookingStatus WHERE StatusName = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("StatusID");
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        
        return -1;
    }
}

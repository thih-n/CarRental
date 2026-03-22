package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.ClientBookingItem;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ClientBookingDAO {

    private ClientBookingItem mapItem(ResultSet rs) throws Exception {
        ClientBookingItem item = new ClientBookingItem();
        item.setContractID(rs.getInt("ContractID"));
        item.setContractCode(rs.getString("ContractCode"));
        
        Object carIdObj = rs.getObject("CarID");
        if (carIdObj != null) {
            item.setCarID(((Number) carIdObj).intValue());
        }
        
        item.setCarName(rs.getString("CarName"));
        item.setDriverID((Integer) rs.getObject("DriverID"));
        item.setDriverName(rs.getString("DriverName"));
        
        Object pickUpIdObj = rs.getObject("PickUpLocationID");
        if (pickUpIdObj != null) {
            item.setPickUpLocationID(((Number) pickUpIdObj).intValue());
        }
        
        item.setPickUpLocationName(rs.getString("PickUpLocationName"));
        item.setStartDateTime(rs.getTimestamp("StartDateTime"));
        item.setEndDateTime(rs.getTimestamp("EndDateTime"));
        item.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        item.setDepositAmount(rs.getBigDecimal("DepositAmount"));
        item.setContractStatus(rs.getString("ContractStatus"));
        item.setDetailStatus(rs.getString("DetailStatus"));
        return item;
    }

    public List<ClientBookingItem> getByCustomer(int customerID) {
        return getByCustomerWithStatus(customerID, null, null, null);
    }

    public List<ClientBookingItem> getCurrentOrders(int customerID) {
        return getByCustomerWithStatus(customerID, "CURRENT", null, null);
    }

    public List<ClientBookingItem> getHistory(int customerID) {
        return getByCustomerWithStatus(customerID, "HISTORY", null, null);
    }

    /**
     * Get orders with optional filtering by status and search by contract code
     * @param customerID Customer ID
     * @param statusFilter Specific status to filter (Booked, InUse, Completed, Cancelled) or null for all
     * @param searchCode Contract code to search (partial match) or null for all
     * @param mode Mode: "CURRENT" for active orders, "HISTORY" for completed/cancelled, null for all
     */
    public List<ClientBookingItem> getOrdersWithFilter(Integer customerID, String statusFilter, String searchCode) {
        return getByCustomerWithStatus(customerID, null, statusFilter, searchCode);
    }

    private List<ClientBookingItem> getByCustomerWithStatus(Integer customerID, String mode, String statusFilter, String searchCode) {
        List<ClientBookingItem> items = new ArrayList<>();
        String sql = "SELECT c.ContractID, c.ContractCode, c.StartDateTime, c.EndDateTime, c.TotalAmount, c.DepositAmount, "
                + "c.PickUpLocationID, pl.LocationName AS PickUpLocationName, "
                + "cs.StatusName AS ContractStatus, cd.DetailStatus, car.CarID, car.CarName, cd.DriverID, du.FullName AS DriverName "
                + "FROM Contracts c "
                + "LEFT JOIN ContractStatuses cs ON cs.StatusID = c.StatusID "
                + "LEFT JOIN ContractDetails cd ON cd.ContractID = c.ContractID "
                + "LEFT JOIN Cars car ON car.CarID = cd.CarID "
                + "LEFT JOIN Locations pl ON pl.LocationID = c.PickUpLocationID "
                + "LEFT JOIN Users du ON du.UserID = cd.DriverID "
                + "WHERE c.CustomerID = ? ";

        if ("CURRENT".equals(mode)) {
            sql += "AND cd.DetailStatus IN ('Booked','InUse') ";
        } else if ("HISTORY".equals(mode)) {
            sql += "AND cd.DetailStatus IN ('Completed','Cancelled') ";
        }

        // Add status filter if provided
        if (statusFilter != null && !statusFilter.isEmpty()) {
            if ("active".equalsIgnoreCase(statusFilter)) {
                sql += "AND cd.DetailStatus IN ('Booked','InUse') ";
            } else if ("completed".equalsIgnoreCase(statusFilter)) {
                sql += "AND cd.DetailStatus = 'Completed' ";
            } else if ("cancelled".equalsIgnoreCase(statusFilter)) {
                sql += "AND cd.DetailStatus = 'Cancelled' ";
            } else {
                // Specific status
                sql += "AND cd.DetailStatus = ? ";
            }
        }

        // Add search by contract code if provided
        if (searchCode != null && !searchCode.trim().isEmpty()) {
            sql += "AND c.ContractCode LIKE ? ";
        }

        sql += "ORDER BY c.CreatedDate DESC";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, customerID);

            // Set status filter parameter if provided
            if (statusFilter != null && !statusFilter.isEmpty()
                    && !"active".equalsIgnoreCase(statusFilter)
                    && !"completed".equalsIgnoreCase(statusFilter)
                    && !"cancelled".equalsIgnoreCase(statusFilter)) {
                ps.setString(paramIndex++, statusFilter);
            }

            // Set search code parameter if provided
            if (searchCode != null && !searchCode.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchCode.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapItem(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public ClientBookingItem getDetail(int customerID, int contractID) {
        String sql = "SELECT c.ContractID, c.ContractCode, c.StartDateTime, c.EndDateTime, c.TotalAmount, c.DepositAmount, "
                + "c.PickUpLocationID, pl.LocationName AS PickUpLocationName, "
                + "cs.StatusName AS ContractStatus, cd.DetailStatus, car.CarID, car.CarName, cd.DriverID, du.FullName AS DriverName "
                + "FROM Contracts c "
                + "LEFT JOIN ContractStatuses cs ON cs.StatusID = c.StatusID "
                + "LEFT JOIN ContractDetails cd ON cd.ContractID = c.ContractID "
                + "LEFT JOIN Cars car ON car.CarID = cd.CarID "
                + "LEFT JOIN Locations pl ON pl.LocationID = c.PickUpLocationID "
                + "LEFT JOIN Users du ON du.UserID = cd.DriverID "
                + "WHERE c.CustomerID = ? AND c.ContractID = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerID);
            ps.setInt(2, contractID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapItem(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean cancelBooking(int contractID, int cancelledByUserID, String cancelReason) {
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

    public boolean updateDriver(int contractID, int driverID) {
        String sql = "UPDATE ContractDetails SET DriverID = ?, HasDriver = 1, DriverStatus = 'Assigned' WHERE ContractID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            ps.setInt(2, contractID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

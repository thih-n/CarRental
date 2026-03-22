package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.AdminRevenuePoint;
import com.carrental.entity.AdminStatusCount;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardDAO {

    public int countUsers() {
        String sql = "SELECT COUNT(*) FROM Users WHERE IsActive = 1";
        return fetchCount(sql);
    }

    public int countActiveCars() {
        String sql = "SELECT COUNT(*) FROM Cars WHERE StatusID = 1";
        return fetchCount(sql);
    }

    public int countTodayBookings() {
        String sql = "SELECT COUNT(*) FROM Contracts WHERE CONVERT(date, CreatedDate) = CONVERT(date, GETDATE())";
        return fetchCount(sql);
    }

    public List<AdminRevenuePoint> getRevenueLastSixMonths(int month, int year) {
        List<AdminRevenuePoint> items = new ArrayList<>();
        String sql = "WITH MonthSeries AS ( "
                + "  SELECT DATEFROMPARTS(?, ?, 1) AS MonthStart, 1 AS lvl "
                + "  UNION ALL "
                + "  SELECT DATEADD(MONTH, -1, MonthStart), lvl + 1 "
                + "  FROM MonthSeries WHERE lvl < 6 "
                + ") "
                + "SELECT FORMAT(ms.MonthStart, 'MM/yyyy') AS Label, "
                + "       ISNULL(SUM(CASE "
                + "         WHEN cd.DetailStatus = 'Completed' THEN c.TotalAmount "
                + "         WHEN cd.DetailStatus IN ('Booked', 'Draft') THEN c.DepositAmount "
                + "         WHEN cd.DetailStatus = 'Cancelled' AND cancelledBy.RoleID = 5 THEN c.DepositAmount "
                + "         ELSE 0 END), 0) AS TotalAmount "
                + "FROM MonthSeries ms "
                + "LEFT JOIN Contracts c "
                + "  ON YEAR(c.CreatedDate) = YEAR(ms.MonthStart) "
                + " AND MONTH(c.CreatedDate) = MONTH(ms.MonthStart) "
                + "LEFT JOIN ContractDetails cd ON cd.ContractID = c.ContractID "
                + "LEFT JOIN Users cancelledBy ON cancelledBy.UserID = c.CancelledBy "
                + "GROUP BY ms.MonthStart "
                + "ORDER BY ms.MonthStart ASC OPTION (MAXRECURSION 6)";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AdminRevenuePoint point = new AdminRevenuePoint();
                    point.setLabel(rs.getString("Label"));
                    point.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                    items.add(point);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public BigDecimal getRevenueForMonth(int month, int year) {
        String sql = "SELECT ISNULL(SUM(CASE "
                + "WHEN cd.DetailStatus = 'Completed' THEN c.TotalAmount "
                + "WHEN cd.DetailStatus IN ('Booked', 'Draft') THEN c.DepositAmount "
                + "WHEN cd.DetailStatus = 'Cancelled' AND cancelledBy.RoleID = 5 THEN c.DepositAmount "
                + "ELSE 0 END), 0) "
                + "FROM Contracts c "
                + "LEFT JOIN ContractDetails cd ON cd.ContractID = c.ContractID "
                + "LEFT JOIN Users cancelledBy ON cancelledBy.UserID = c.CancelledBy "
                + "WHERE MONTH(c.CreatedDate) = ? AND YEAR(c.CreatedDate) = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal value = rs.getBigDecimal(1);
                    return value == null ? BigDecimal.ZERO : value;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public List<AdminStatusCount> getCarStatusCounts() {
        List<AdminStatusCount> items = new ArrayList<>();
        String sql = "SELECT cs.StatusName, COUNT(*) AS TotalCount "
                + "FROM Cars c "
                + "JOIN CarStatuses cs ON cs.StatusID = c.StatusID "
                + "GROUP BY cs.StatusName";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AdminStatusCount item = new AdminStatusCount();
                item.setStatusName(rs.getString("StatusName"));
                item.setTotalCount(rs.getInt("TotalCount"));
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    private int fetchCount(String sql) {
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Map<String, Object>> getRevenueByMonth(int months) {
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = "SELECT TOP " + months + " FORMAT(c.CreatedDate, 'MM/yyyy') AS month, SUM(c.TotalAmount) AS amount "
                + "FROM Contracts c "
                + "GROUP BY FORMAT(c.CreatedDate, 'MM/yyyy') "
                + "ORDER BY MAX(c.CreatedDate)";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> point = new HashMap<>();
                point.put("month", rs.getString("month"));
                point.put("amount", rs.getBigDecimal("amount"));
                items.add(point);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public int getTotalBookings() {
        String sql = "SELECT COUNT(*) FROM Contracts";
        return fetchCount(sql);
    }

    public List<Map<String, Object>> getStaffSalaries() {
        List<Map<String, Object>> salaries = new ArrayList<>();
        String sql = "SELECT u.UserID, u.FullName, u.RoleID, COUNT(ct.ContractID) as TripCount, "
                + "SUM(ct.TotalAmount * 0.05) as TotalSalary "
                + "FROM Users u "
                + "LEFT JOIN Contracts ct ON u.UserID = ct.DriverID AND ct.StatusID = 5 "
                + "WHERE u.RoleID IN (3, 4) AND u.IsActive = 1 "
                + "GROUP BY u.UserID, u.FullName, u.RoleID "
                + "ORDER BY TotalSalary DESC";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> salary = new HashMap<>();
                salary.put("userID", rs.getInt("UserID"));
                salary.put("fullName", rs.getString("FullName"));
                salary.put("roleID", rs.getInt("RoleID"));
                salary.put("tripCount", rs.getInt("TripCount"));
                salary.put("totalSalary", rs.getBigDecimal("TotalSalary"));
                salaries.add(salary);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return salaries;
    }

    // Get completed bookings count (DetailStatus = 'Completed')
    public int getCompletedBookingsCount() {
        String sql = "SELECT COUNT(*) FROM ContractDetails WHERE DetailStatus = 'Completed'";
        return fetchCount(sql);
    }

    // Get deposited bookings count (DetailStatus = 'Booked')
    public int getDepositedBookingsCount() {
        String sql = "SELECT COUNT(*) FROM ContractDetails WHERE DetailStatus = 'Booked'";
        return fetchCount(sql);
    }

    // Get cancelled bookings count (DetailStatus = 'Cancelled')
    public int getCancelledBookingsCount() {
        String sql = "SELECT COUNT(*) FROM ContractDetails WHERE DetailStatus = 'Cancelled'";
        return fetchCount(sql);
    }

    // Get revenue from Booked + Completed bookings
    public long getTotalRevenueFromBookings() {
        String sql = "SELECT ISNULL(SUM(c.TotalAmount), 0) FROM Contracts c "
                + "JOIN ContractDetails cd ON c.ContractID = cd.ContractID "
                + "WHERE cd.DetailStatus IN ('Booked', 'Completed')";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get driver salaries (only RoleID = 4) with search and month/year filter
    public List<Map<String, Object>> getDriverSalaries(String search, Integer month, Integer year) {
        List<Map<String, Object>> salaries = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT u.UserID, u.FullName, dp.BaseSalary, dp.CommissionRate, ");
        sql.append("COALESCE(completedTrips.TripCount, 0) as TripCount, ");
        sql.append("COALESCE(completedTrips.TotalCommission, 0) as TotalSalary ");
        sql.append("FROM Users u ");
        sql.append("JOIN DriverProfiles dp ON dp.DriverID = u.UserID ");
        sql.append("LEFT JOIN ( ");
        sql.append("    SELECT cd.DriverID, ");
        sql.append("           COUNT(cd.ContractID) as TripCount, ");
        sql.append("           SUM(ct.TotalAmount * dp2.CommissionRate / 100.0) as TotalCommission ");
        sql.append("    FROM ContractDetails cd ");
        sql.append("    JOIN Contracts ct ON cd.ContractID = ct.ContractID AND ct.StatusID = 3 ");
        sql.append("    JOIN DriverProfiles dp2 ON cd.DriverID = dp2.DriverID ");
        
        // Add month/year filter in subquery
        if (month != null && month > 0) {
            sql.append("    AND MONTH(ct.EndDateTime) = ").append(month).append(" ");
        }
        if (year != null && year > 0) {
            sql.append("    AND YEAR(ct.EndDateTime) = ").append(year).append(" ");
        }
        
        sql.append("    GROUP BY cd.DriverID ");
        sql.append(") completedTrips ON completedTrips.DriverID = u.UserID ");
        
        // Build WHERE clause
        List<String> conditions = new ArrayList<>();
        conditions.add("u.RoleID = 4");
        conditions.add("u.IsActive = 1");
        
        // Add search condition
        if (search != null && !search.trim().isEmpty()) {
            conditions.add("u.FullName LIKE ?");
        }
        
        sql.append("WHERE ");
        sql.append(String.join(" AND ", conditions));
        
        sql.append(" ORDER BY TotalSalary DESC");

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            // Set search parameter
            if (search != null && !search.trim().isEmpty()) {
                ps.setString(1, "%" + search.trim() + "%");
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> salary = new HashMap<>();
                salary.put("userID", rs.getInt("UserID"));
                salary.put("fullName", rs.getString("FullName"));
                salary.put("baseSalary", rs.getBigDecimal("BaseSalary") != null ? rs.getBigDecimal("BaseSalary").setScale(0) : BigDecimal.ZERO);
                salary.put("commissionRate", rs.getBigDecimal("CommissionRate") != null ? rs.getBigDecimal("CommissionRate").setScale(0) : new BigDecimal("5"));
                salary.put("tripCount", rs.getInt("TripCount"));
                salary.put("totalSalary", rs.getBigDecimal("TotalSalary") != null ? rs.getBigDecimal("TotalSalary").setScale(0) : BigDecimal.ZERO);
                salaries.add(salary);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return salaries;
    }

    // Update driver salary info
    public boolean updateDriverSalary(int userID, BigDecimal baseSalary, BigDecimal commissionRate) {
        String sql = "UPDATE DriverProfiles SET BaseSalary = ?, CommissionRate = ? WHERE DriverID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, baseSalary);
            ps.setBigDecimal(2, commissionRate);
            ps.setInt(3, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get refund list with contract and booking details
    public List<Map<String, Object>> getRefunds(String status, String search) {
        List<Map<String, Object>> refunds = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.PaymentID, p.ContractID, p.Amount, p.PaymentDate, p.PaymentStatus, ");
        sql.append("p.RefPaymentID, p.Note, ");
        sql.append("c.ContractCode, c.StartDateTime, c.EndDateTime, ");
        sql.append("u.FullName as CustomerName, ");
        sql.append("'-' as DriverName ");
        sql.append("FROM Payments p ");
        sql.append("LEFT JOIN Contracts c ON p.ContractID = c.ContractID ");
        sql.append("LEFT JOIN Users u ON c.CustomerID = u.UserID ");
        sql.append("WHERE p.PaymentType = 'Refund' ");
        
        List<String> conditions = new ArrayList<>();
        
        if (status != null && !status.isEmpty() && !status.equals("all")) {
            conditions.add("p.PaymentStatus = ?");
        }
        
        if (search != null && !search.trim().isEmpty()) {
            conditions.add("(u.FullName LIKE ? OR c.ContractCode LIKE ? OR p.Note LIKE ?)");
        }
        
        if (!conditions.isEmpty()) {
            sql.append(" AND ");
            sql.append(String.join(" AND ", conditions));
        }
        
        sql.append(" ORDER BY p.PaymentDate DESC");
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (status != null && !status.isEmpty() && !status.equals("all")) {
                ps.setString(paramIndex++, status);
            }
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim() + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> refund = new HashMap<>();
                refund.put("paymentID", rs.getInt("PaymentID"));
                refund.put("contractID", rs.getObject("ContractID"));
                refund.put("amount", rs.getBigDecimal("Amount") != null ? rs.getBigDecimal("Amount").setScale(0) : BigDecimal.ZERO);
                refund.put("paymentDate", rs.getTimestamp("PaymentDate"));
                refund.put("paymentStatus", rs.getString("PaymentStatus"));
                refund.put("refPaymentID", rs.getObject("RefPaymentID"));
                refund.put("note", rs.getString("Note"));
                refund.put("bookingID", rs.getObject("ContractCode"));
                refund.put("pickupDate", rs.getTimestamp("StartDateTime"));
                refund.put("returnDate", rs.getTimestamp("EndDateTime"));
                refund.put("customerName", rs.getString("CustomerName"));
                refund.put("driverName", rs.getString("DriverName"));
                refunds.add(refund);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return refunds;
    }
    public List<Map<String, Object>> getRefundsByTime(String status, String search, int month, int year) {
        List<Map<String, Object>> refunds = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.PaymentID, p.ContractID, p.Amount, p.PaymentDate, p.PaymentStatus, ");
        sql.append("p.RefPaymentID, p.Note, ");
        sql.append("c.ContractCode, c.StartDateTime, c.EndDateTime, ");
        sql.append("u.FullName as CustomerName, ");
        sql.append("'-' as DriverName ");
        sql.append("FROM Payments p ");
        sql.append("LEFT JOIN Contracts c ON p.ContractID = c.ContractID ");
        sql.append("LEFT JOIN Users u ON c.CustomerID = u.UserID ");
        sql.append("WHERE p.PaymentType = 'Refund' ");
        sql.append("AND MONTH(p.PaymentDate) = ? AND YEAR(p.PaymentDate) = ? ");
        
        List<String> conditions = new ArrayList<>();
        
        if (status != null && !status.isEmpty() && !status.equals("all")) {
            conditions.add("p.PaymentStatus = ?");
        }
        
        if (search != null && !search.trim().isEmpty()) {
            conditions.add("(u.FullName LIKE ? OR c.ContractCode LIKE ? OR p.Note LIKE ?)");
        }
        
        if (!conditions.isEmpty()) {
            sql.append(" AND ");
            sql.append(String.join(" AND ", conditions));
        }
        
        sql.append(" ORDER BY p.PaymentDate DESC");
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            ps.setInt(paramIndex++, month);
            ps.setInt(paramIndex++, year);
            if (status != null && !status.isEmpty() && !status.equals("all")) {
                ps.setString(paramIndex++, status);
            }
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim() + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> refund = new HashMap<>();
                refund.put("paymentID", rs.getInt("PaymentID"));
                refund.put("contractID", rs.getObject("ContractID"));
                refund.put("amount", rs.getBigDecimal("Amount") != null ? rs.getBigDecimal("Amount").setScale(0) : BigDecimal.ZERO);
                refund.put("paymentDate", rs.getTimestamp("PaymentDate"));
                refund.put("paymentStatus", rs.getString("PaymentStatus"));
                refund.put("refPaymentID", rs.getObject("RefPaymentID"));
                refund.put("note", rs.getString("Note"));
                refund.put("bookingID", rs.getObject("ContractCode"));
                refund.put("pickupDate", rs.getTimestamp("StartDateTime"));
                refund.put("returnDate", rs.getTimestamp("EndDateTime"));
                refund.put("customerName", rs.getString("CustomerName"));
                refund.put("driverName", rs.getString("DriverName"));
                refunds.add(refund);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return refunds;
    }
    // Update refund status
    public boolean updateRefundStatus(int paymentID, String status) {
        String sql = "UPDATE Payments SET PaymentStatus = ? WHERE PaymentID = ? AND PaymentType = 'Refund'";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, paymentID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get refund detail by paymentID
    public Map<String, Object> getRefundDetail(int paymentID) {
        String sql = "SELECT p.PaymentID, p.ContractID, p.Amount, p.PaymentDate, p.PaymentMethod, " +
                   "p.PaymentStatus, p.RefPaymentID, p.Note, p.TransactionCode, " +
                   "c.ContractCode, c.StartDateTime, c.EndDateTime, c.PickUpLocationID, c.DropOffLocationID, " +
                   "c.TotalAmount as ContractTotal, c.DepositAmount, " +
                   "u.FullName as CustomerName, u.Email as CustomerEmail, u.PhoneNumber as CustomerPhone, " +
                   "d.FullName as DriverName, d.PhoneNumber as DriverPhone, " +
                   "cd.CarID, ca.plateNumber, ca.carName, " +
                   "pl1.LocationName as PickupLocation, pl2.LocationName as ReturnLocation, " +
                   "dep.PaymentID as DepositPaymentID, dep.Amount as DepositAmount, dep.PaymentStatus as DepositStatus " +
                   "FROM Payments p " +
                   "LEFT JOIN Contracts c ON p.ContractID = c.ContractID " +
                   "LEFT JOIN Users u ON c.CustomerID = u.UserID " +
                   "LEFT JOIN ContractDetails cd ON c.ContractID = cd.ContractID " +
                   "LEFT JOIN Users d ON cd.DriverID = d.UserID " +
                   "LEFT JOIN Cars ca ON cd.CarID = ca.CarID " +
                   "LEFT JOIN Locations pl1 ON c.PickUpLocationID = pl1.LocationID " +
                   "LEFT JOIN Locations pl2 ON c.DropOffLocationID = pl2.LocationID " +
                   "LEFT JOIN (SELECT ContractID, PaymentID, Amount, PaymentStatus FROM Payments WHERE PaymentType = 'Deposit' AND RefPaymentID IS NULL) dep ON c.ContractID = dep.ContractID " +
                   "WHERE p.PaymentID = ? AND p.PaymentType = 'Refund'";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> detail = new HashMap<>();
                    detail.put("paymentID", rs.getInt("PaymentID"));
                    detail.put("contractID", rs.getObject("ContractID"));
                    detail.put("amount", rs.getBigDecimal("Amount") != null ? rs.getBigDecimal("Amount").setScale(0) : BigDecimal.ZERO);
                    detail.put("paymentDate", rs.getTimestamp("PaymentDate"));
                    detail.put("paymentMethod", rs.getString("PaymentMethod"));
                    detail.put("paymentStatus", rs.getString("PaymentStatus"));
                    detail.put("refPaymentID", rs.getObject("RefPaymentID"));
                    detail.put("note", rs.getString("Note"));
                    detail.put("transactionCode", rs.getString("TransactionCode"));
                    detail.put("bookingID", rs.getObject("ContractCode"));
                    detail.put("pickupDate", rs.getTimestamp("StartDateTime"));
                    detail.put("returnDate", rs.getTimestamp("EndDateTime"));
                    detail.put("customerName", rs.getString("CustomerName"));
                    detail.put("customerEmail", rs.getString("CustomerEmail"));
                    detail.put("customerPhone", rs.getString("CustomerPhone"));
                    detail.put("driverName", rs.getString("DriverName"));
                    detail.put("driverPhone", rs.getString("DriverPhone"));
                    detail.put("carID", rs.getObject("CarID"));
                    detail.put("licensePlate", rs.getString("plateNumber"));
                    detail.put("modelName", rs.getString("carName"));
                    detail.put("pickupLocation", rs.getString("PickupLocation"));
                    detail.put("returnLocation", rs.getString("ReturnLocation"));
                    detail.put("contractTotal", rs.getBigDecimal("ContractTotal"));
                    detail.put("depositPaymentID", rs.getObject("DepositPaymentID"));
                    detail.put("depositAmount", rs.getBigDecimal("DepositAmount"));
                    detail.put("depositStatus", rs.getString("DepositStatus"));
                    return detail;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

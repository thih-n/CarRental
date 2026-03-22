package com.carrental.dao;

import com.carrental.config.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DriverLeaveRequestDAO {

    public static class DriverLeaveRequest {
        private int leaveID;
        private int driverID;
        private String driverName;
        private Timestamp leaveStart;
        private Timestamp leaveEnd;
        private String reason;
        private String status;
        private Timestamp createdAt;
        private Timestamp approvedAt;

        public int getLeaveID() { return leaveID; }
        public void setLeaveID(int leaveID) { this.leaveID = leaveID; }
        public int getDriverID() { return driverID; }
        public void setDriverID(int driverID) { this.driverID = driverID; }
        public String getDriverName() { return driverName; }
        public void setDriverName(String driverName) { this.driverName = driverName; }
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
        public Timestamp getApprovedAt() { return approvedAt; }
        public void setApprovedAt(Timestamp approvedAt) { this.approvedAt = approvedAt; }
    }

    public List<DriverLeaveRequest> getAllRequests(String status, String search) {
        List<DriverLeaveRequest> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT dl.LeaveID, dl.DriverID, u.FullName as DriverName, ")
           .append("dl.LeaveStart, dl.LeaveEnd, dl.Reason, dl.Status, dl.CreatedAt, dl.ApprovedAt ")
           .append("FROM DriverLeaves dl ")
           .append("JOIN Users u ON u.UserID = dl.DriverID ")
           .append("WHERE 1=1 ");

        if (status != null && !status.equals("all")) {
            sql.append("AND dl.Status = ? ");
            params.add(status);
        }

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND u.FullName LIKE ? ");
            params.add("%" + search.trim() + "%");
        }

        sql.append("ORDER BY dl.CreatedAt DESC");

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DriverLeaveRequest r = new DriverLeaveRequest();
                    r.setLeaveID(rs.getInt("LeaveID"));
                    r.setDriverID(rs.getInt("DriverID"));
                    r.setDriverName(rs.getString("DriverName"));
                    r.setLeaveStart(rs.getTimestamp("LeaveStart"));
                    r.setLeaveEnd(rs.getTimestamp("LeaveEnd"));
                    r.setReason(rs.getString("Reason"));
                    r.setStatus(rs.getString("Status"));
                    r.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    r.setApprovedAt(rs.getTimestamp("ApprovedAt"));
                    requests.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requests;
    }

    public boolean approveLeave(int leaveID, int approvedBy) {
        String sql = "UPDATE DriverLeaves SET Status = 'Approved', ApprovedBy = ?, ApprovedAt = GETDATE() WHERE LeaveID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, approvedBy);
            ps.setInt(2, leaveID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean rejectLeave(int leaveID, int approvedBy, String reason) {
        String sql = "UPDATE DriverLeaves SET Status = 'Rejected', ApprovedBy = ?, ApprovedAt = GETDATE(), Reason = ? WHERE LeaveID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, approvedBy);
            ps.setString(2, reason);
            ps.setInt(3, leaveID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DriverLeaveRequest> getRequestsByDriver(int driverID) {
        List<DriverLeaveRequest> requests = new ArrayList<>();
        String sql = "SELECT dl.LeaveID, dl.DriverID, u.FullName as DriverName, "
                + "dl.LeaveStart, dl.LeaveEnd, dl.Reason, dl.Status, dl.CreatedAt, dl.ApprovedAt "
                + "FROM DriverLeaves dl "
                + "JOIN Users u ON u.UserID = dl.DriverID "
                + "WHERE dl.DriverID = ? "
                + "ORDER BY dl.LeaveStart DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DriverLeaveRequest r = new DriverLeaveRequest();
                    r.setLeaveID(rs.getInt("LeaveID"));
                    r.setDriverID(rs.getInt("DriverID"));
                    r.setDriverName(rs.getString("DriverName"));
                    r.setLeaveStart(rs.getTimestamp("LeaveStart"));
                    r.setLeaveEnd(rs.getTimestamp("LeaveEnd"));
                    r.setReason(rs.getString("Reason"));
                    r.setStatus(rs.getString("Status"));
                    r.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    r.setApprovedAt(rs.getTimestamp("ApprovedAt"));
                    requests.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requests;
    }

    public DriverLeaveRequest getLeaveById(int leaveID) {
        String sql = "SELECT dl.LeaveID, dl.DriverID, u.FullName as DriverName, "
                + "dl.LeaveStart, dl.LeaveEnd, dl.Reason, dl.Status, dl.CreatedAt, dl.ApprovedAt "
                + "FROM DriverLeaves dl "
                + "JOIN Users u ON u.UserID = dl.DriverID "
                + "WHERE dl.LeaveID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, leaveID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DriverLeaveRequest r = new DriverLeaveRequest();
                    r.setLeaveID(rs.getInt("LeaveID"));
                    r.setDriverID(rs.getInt("DriverID"));
                    r.setDriverName(rs.getString("DriverName"));
                    r.setLeaveStart(rs.getTimestamp("LeaveStart"));
                    r.setLeaveEnd(rs.getTimestamp("LeaveEnd"));
                    r.setReason(rs.getString("Reason"));
                    r.setStatus(rs.getString("Status"));
                    r.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    r.setApprovedAt(rs.getTimestamp("ApprovedAt"));
                    return r;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createLeaveRequest(int driverID, Timestamp leaveStart, Timestamp leaveEnd, String reason) {
        String sql = "INSERT INTO DriverLeaves (DriverID, LeaveStart, LeaveEnd, Reason, Status, CreatedAt) "
                + "VALUES (?, ?, ?, ?, 'Pending', GETDATE())";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            ps.setTimestamp(2, leaveStart);
            ps.setTimestamp(3, leaveEnd);
            ps.setString(4, reason);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DriverLeaveRequest> getConflictingLeaves(int driverID, Timestamp leaveStart, Timestamp leaveEnd) {
        List<DriverLeaveRequest> conflicts = new ArrayList<>();
        String sql = "SELECT dl.LeaveID, dl.DriverID, u.FullName as DriverName, "
                + "dl.LeaveStart, dl.LeaveEnd, dl.Reason, dl.Status, dl.CreatedAt, dl.ApprovedAt "
                + "FROM DriverLeaves dl "
                + "JOIN Users u ON u.UserID = dl.DriverID "
                + "WHERE dl.DriverID = ? "
                + "AND dl.Status != 'Rejected' "
                + "AND dl.LeaveStart < ? "
                + "AND dl.LeaveEnd > ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            ps.setTimestamp(2, leaveEnd);
            ps.setTimestamp(3, leaveStart);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DriverLeaveRequest r = new DriverLeaveRequest();
                    r.setLeaveID(rs.getInt("LeaveID"));
                    r.setDriverID(rs.getInt("DriverID"));
                    r.setDriverName(rs.getString("DriverName"));
                    r.setLeaveStart(rs.getTimestamp("LeaveStart"));
                    r.setLeaveEnd(rs.getTimestamp("LeaveEnd"));
                    r.setReason(rs.getString("Reason"));
                    r.setStatus(rs.getString("Status"));
                    r.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    r.setApprovedAt(rs.getTimestamp("ApprovedAt"));
                    conflicts.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conflicts;
    }

    public boolean cancelLeave(int leaveID, int driverID) {
        String sql = "DELETE FROM DriverLeaves WHERE LeaveID = ? AND DriverID = ? AND Status = 'Pending'";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, leaveID);
            ps.setInt(2, driverID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

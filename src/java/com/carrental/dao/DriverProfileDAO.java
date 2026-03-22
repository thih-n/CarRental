package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.DriverCard;
import com.carrental.entity.DriverProfile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DriverProfileDAO {

    private DriverProfile mapDriverProfile(ResultSet rs) throws Exception {
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
        d.setIsActive((Boolean) rs.getObject("IsActive"));
        return d;
    }

    public DriverProfile getById(int driverID) {
        String sql = "SELECT dp.*, u.FullName, u.IsActive FROM DriverProfiles dp "
                + "JOIN Users u ON u.UserID = dp.DriverID "
                + "WHERE dp.DriverID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DriverProfile d = mapDriverProfile(rs);
                    d.setFullName(rs.getString("FullName"));
                    return d;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DriverProfile> getAvailableDriversByLocation(int pickUpLocationID) {
        return searchDrivers(pickUpLocationID, null, null);
    }

    public List<DriverProfile> searchDrivers(Integer pickUpLocationID, String keyword, String minRating) {
        List<DriverProfile> drivers = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT dp.*, u.FullName FROM DriverProfiles dp ")
           .append("JOIN Users u ON u.UserID = dp.DriverID ")
           .append("WHERE dp.BaseLocationID = ? ")
           .append("AND dp.DriverStatus = 'Available' ")
           .append("AND u.IsActive = 1 ");

        params.add(pickUpLocationID);

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND u.FullName LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }

        if (minRating != null && !minRating.isEmpty()) {
            try {
                float rating = Float.parseFloat(minRating);
                sql.append("AND dp.Rating >= ? ");
                params.add(rating);
            } catch (NumberFormatException e) {
                // ignore invalid rating
            }
        }

        sql.append("ORDER BY dp.Rating DESC, dp.TotalTrips DESC");

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DriverProfile d = mapDriverProfile(rs);
                    d.setFullName(rs.getString("FullName"));
                    drivers.add(d);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return drivers;
    }

    public boolean updateDriverStatus(int driverID, String newStatus) {
        String sql = "UPDATE DriverProfiles SET DriverStatus = ? WHERE DriverID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, driverID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateDriverStatusByUserID(int userID, String newStatus) {
        String sql = "UPDATE DriverProfiles SET DriverStatus = ? WHERE DriverID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DriverCard> getTopDrivers(int limit) {
        List<DriverCard> items = new ArrayList<>();
        String sql = "SELECT TOP (?) dp.DriverID, u.FullName, dp.Rating, dp.TotalTrips, dp.DailyRate, dp.ExperienceYears "
                + "FROM DriverProfiles dp "
                + "JOIN Users u ON u.UserID = dp.DriverID "
                + "WHERE u.IsActive = 1 "
                + "ORDER BY dp.Rating DESC, dp.TotalTrips DESC";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Math.max(1, limit));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DriverCard item = new DriverCard();
                    item.setDriverID(rs.getInt("DriverID"));
                    item.setFullName(rs.getString("FullName"));
                    item.setRating(rs.getBigDecimal("Rating"));
                    item.setTotalTrips((Integer) rs.getObject("TotalTrips"));
                    item.setDailyRate(rs.getBigDecimal("DailyRate"));
                    item.setExperienceYears((Integer) rs.getObject("ExperienceYears"));
                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<DriverProfile> getAllDrivers(String status, String search) {
        List<DriverProfile> drivers = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT dp.*, u.FullName, u.IsActive FROM DriverProfiles dp ")
           .append("JOIN Users u ON u.UserID = dp.DriverID ")
           .append("WHERE 1=1 ");

        if (status != null && !status.equals("all")) {
            sql.append("AND dp.DriverStatus = ? ");
            params.add(status);
        }

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND u.FullName LIKE ? ");
            params.add("%" + search.trim() + "%");
        }

        sql.append("ORDER BY u.FullName ASC");

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DriverProfile d = mapDriverProfile(rs);
                    d.setFullName(rs.getString("FullName"));
                    drivers.add(d);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return drivers;
    }

    public boolean updateDriver(DriverProfile driver) {
        String sql = "UPDATE DriverProfiles SET LicenseNumber = ?, LicenseExpiry = ?, ExperienceYears = ?, "
                + "DriverStatus = ?, BaseSalary = ?, CommissionRate = ?, HourlyRate = ?, DailyRate = ? "
                + "WHERE DriverID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, driver.getLicenseNumber());
            ps.setDate(2, driver.getLicenseExpiry());
            ps.setObject(3, driver.getExperienceYears());
            ps.setString(4, driver.getDriverStatus());
            ps.setBigDecimal(5, driver.getBaseSalary());
            ps.setBigDecimal(6, driver.getCommissionRate());
            ps.setBigDecimal(7, driver.getHourlyRate());
            ps.setBigDecimal(8, driver.getDailyRate());
            ps.setInt(9, driver.getDriverID());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean toggleUserActiveStatus(int userID, boolean isActive) {
        String sql = "UPDATE Users SET IsActive = ? WHERE UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            ps.setInt(2, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public DriverProfile getByUserId(int userID) {
        String sql = "SELECT dp.*, u.FullName, u.IsActive FROM DriverProfiles dp "
                + "JOIN Users u ON u.UserID = dp.DriverID "
                + "WHERE dp.DriverID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DriverProfile d = mapDriverProfile(rs);
                    d.setFullName(rs.getString("FullName"));
                    d.setUserID(rs.getInt("DriverID"));
                    return d;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean createDriverProfile(DriverProfile driver) {
        String sql = "INSERT INTO DriverProfiles (DriverID, LicenseNumber, LicenseExpiry, ExperienceYears, DriverStatus, BaseLocationID) "
                + "VALUES (?, ?, ?, ?, 'Available', ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driver.getUserID());
            ps.setString(2, driver.getLicenseNumber());
            ps.setDate(3, driver.getLicenseExpiry());
            ps.setObject(4, driver.getExperienceYears());
            ps.setObject(5, driver.getBaseLocationID());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

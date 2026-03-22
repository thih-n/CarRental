package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.CarType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CarTypeDAO {

    public List<CarType> getAllCarTypes() {
        return searchCarTypes(null);
    }
    
    public List<CarType> searchCarTypes(String keyword) {
        List<CarType> types = new ArrayList<>();
        String sql = "SELECT TypeID, TypeName, BodyID, SeatCount, Description FROM CarTypes";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " WHERE TypeName LIKE ?";
        }
        sql += " ORDER BY TypeName";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CarType type = new CarType();
                    type.setTypeID(rs.getInt("TypeID"));
                    type.setTypeName(rs.getString("TypeName"));
                    type.setBodyID((Integer) rs.getObject("BodyID"));
                    type.setSeatCount(rs.getInt("SeatCount"));
                    type.setDescription(rs.getString("Description"));
                    types.add(type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return types;
    }
    
    public List<CarType> getCarTypesPaging(int page, int pageSize, String keyword) {
        List<CarType> types = new ArrayList<>();
        String sql = "SELECT TypeID, TypeName, BodyID, SeatCount, Description FROM CarTypes";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " WHERE TypeName LIKE ?";
        }
        sql += " ORDER BY TypeName OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            ps.setInt(paramIndex++, (page - 1) * pageSize);
            ps.setInt(paramIndex++, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CarType type = new CarType();
                    type.setTypeID(rs.getInt("TypeID"));
                    type.setTypeName(rs.getString("TypeName"));
                    type.setBodyID((Integer) rs.getObject("BodyID"));
                    type.setSeatCount(rs.getInt("SeatCount"));
                    type.setDescription(rs.getString("Description"));
                    types.add(type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return types;
    }
    
    public int getTotalCarTypes(String keyword) {
        String sql = "SELECT COUNT(*) FROM CarTypes";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " WHERE TypeName LIKE ?";
        }
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public CarType getCarTypeById(int typeID) {
        String sql = "SELECT TypeID, TypeName, BodyID, SeatCount, Description FROM CarTypes WHERE TypeID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, typeID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CarType type = new CarType();
                    type.setTypeID(rs.getInt("TypeID"));
                    type.setTypeName(rs.getString("TypeName"));
                    type.setBodyID((Integer) rs.getObject("BodyID"));
                    type.setSeatCount(rs.getInt("SeatCount"));
                    type.setDescription(rs.getString("Description"));
                    return type;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addCarType(String typeName, Integer bodyID, int seatCount, String description) {
        String sql = "INSERT INTO CarTypes (TypeName, BodyID, SeatCount, Description) VALUES (?, ?, ?, ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, typeName);
            if (bodyID != null) {
                ps.setInt(2, bodyID);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setInt(3, seatCount);
            ps.setString(4, description);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCarType(int typeID, String typeName, Integer bodyID, int seatCount, String description) {
        String sql = "UPDATE CarTypes SET TypeName = ?, BodyID = ?, SeatCount = ?, Description = ? WHERE TypeID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, typeName);
            if (bodyID != null) {
                ps.setInt(2, bodyID);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setInt(3, seatCount);
            ps.setString(4, description);
            ps.setInt(5, typeID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCarType(int typeID) {
        String sql = "DELETE FROM CarTypes WHERE TypeID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, typeID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.Brand;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO {

    public List<Brand> getAllBrands() {
        return searchBrands(null);
    }
    
    public List<Brand> searchBrands(String keyword) {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT BrandID, BrandName FROM Brands";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " WHERE BrandName LIKE ?";
        }
        sql += " ORDER BY BrandName";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Brand brand = new Brand();
                    brand.setBrandID(rs.getInt("BrandID"));
                    brand.setBrandName(rs.getString("BrandName"));
                    brands.add(brand);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return brands;
    }
    
    public List<Brand> getBrandsPaging(int page, int pageSize, String keyword) {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT BrandID, BrandName FROM Brands";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " WHERE BrandName LIKE ?";
        }
        sql += " ORDER BY BrandName OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
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
                    Brand brand = new Brand();
                    brand.setBrandID(rs.getInt("BrandID"));
                    brand.setBrandName(rs.getString("BrandName"));
                    brands.add(brand);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return brands;
    }
    
    public int getTotalBrands(String keyword) {
        String sql = "SELECT COUNT(*) FROM Brands";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " WHERE BrandName LIKE ?";
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

    public Brand getBrandById(int brandID) {
        String sql = "SELECT BrandID, BrandName FROM Brands WHERE BrandID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, brandID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Brand brand = new Brand();
                    brand.setBrandID(rs.getInt("BrandID"));
                    brand.setBrandName(rs.getString("BrandName"));
                    return brand;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addBrand(String brandName) {
        String sql = "INSERT INTO Brands (BrandName) VALUES (?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, brandName);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBrand(int brandID, String brandName) {
        String sql = "UPDATE Brands SET BrandName = ? WHERE BrandID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, brandName);
            ps.setInt(2, brandID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBrand(int brandID) {
        String sql = "DELETE FROM Brands WHERE BrandID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, brandID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

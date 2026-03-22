package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.CarImage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CarImageDAO {
    
    public List<CarImage> getImagesByCarId(int carID) {
        List<CarImage> images = new ArrayList<>();
        String sql = "SELECT ImageID, CarID, ImageUrl, ImageType, SortOrder FROM CarImages WHERE CarID = ? ORDER BY SortOrder";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CarImage img = new CarImage();
                    img.setImageID(rs.getInt("ImageID"));
                    img.setCarID(rs.getInt("CarID"));
                    img.setImageUrl(rs.getString("ImageUrl"));
                    img.setImageType(rs.getString("ImageType"));
                    img.setSortOrder((Integer) rs.getObject("SortOrder"));
                    images.add(img);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return images;
    }
    
    public boolean addImage(CarImage image) {
        String sql = "INSERT INTO CarImages (CarID, ImageUrl, ImageType, SortOrder) VALUES (?, ?, ?, ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, image.getCarID());
            ps.setString(2, image.getImageUrl());
            ps.setString(3, image.getImageType());
            ps.setObject(4, image.getSortOrder());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateImage(CarImage image) {
        String sql = "UPDATE CarImages SET ImageUrl = ?, ImageType = ?, SortOrder = ? WHERE ImageID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, image.getImageUrl());
            ps.setString(2, image.getImageType());
            ps.setObject(3, image.getSortOrder());
            ps.setInt(4, image.getImageID());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteImage(int imageID) {
        String sql = "DELETE FROM CarImages WHERE ImageID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, imageID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteImagesByCarId(int carID) {
        String sql = "DELETE FROM CarImages WHERE CarID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

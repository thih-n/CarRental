package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.Review;
import com.carrental.entity.ReviewCard;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReviewDAO {

    public List<ReviewCard> getNewestHighRatingReviews(int limit) {
        List<ReviewCard> items = new ArrayList<>();
        String sql = "SELECT TOP (?) r.ReviewID, r.Rating, r.Comment, u.FullName AS ReviewerName, r.CreatedAt "
                + "FROM Reviews r "
                + "JOIN Users u ON u.UserID = r.UserID "
                + "WHERE r.Rating IN (4,5) "
                + "ORDER BY r.CreatedAt DESC";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Math.max(1, limit));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReviewCard item = new ReviewCard();
                    item.setReviewID(rs.getInt("ReviewID"));
                    item.setRating(rs.getInt("Rating"));
                    item.setComment(rs.getString("Comment"));
                    item.setReviewerName(rs.getString("ReviewerName"));
                    item.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }
    public List<ReviewCard> getNewestReviews(int limit, int month, int year) {
        List<ReviewCard> items = new ArrayList<>();
        String sql = "SELECT TOP (?) r.ReviewID, r.Rating, r.Comment, u.FullName AS ReviewerName, r.CreatedAt "
                + "FROM Reviews r "
                + "JOIN Users u ON u.UserID = r.UserID "
                + "WHERE MONTH(r.CreatedAt) = ? AND YEAR(r.CreatedAt) = ? "
                + "ORDER BY r.CreatedAt DESC";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Math.max(1, limit));
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReviewCard item = new ReviewCard();
                    item.setReviewID(rs.getInt("ReviewID"));
                    item.setRating(rs.getInt("Rating"));
                    item.setComment(rs.getString("Comment"));
                    item.setReviewerName(rs.getString("ReviewerName"));
                    item.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }
    public boolean hasReviewed(int contractID, int driverID) {
        String sql = "SELECT 1 FROM Reviews WHERE ContractID = ? AND TargetType = 'driver' AND TargetID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractID);
            ps.setInt(2, driverID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean insertReview(int contractID, int userID, String targetType, int targetID, int rating, String comment) {
        String sql = "INSERT INTO Reviews (ContractID, UserID, TargetType, TargetID, Rating, Comment, CreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractID);
            ps.setInt(2, userID);
            ps.setString(3, targetType);
            ps.setInt(4, targetID);
            ps.setInt(5, rating);
            ps.setString(6, comment);
            boolean inserted = ps.executeUpdate() > 0;
            
            // Update driver rating in DriverProfiles table
            if (inserted && "driver".equals(targetType)) {
                updateDriverRating(conn, targetID);
            }
            
            return inserted;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private void updateDriverRating(Connection conn, int driverID) {
        String sql = "UPDATE DriverProfiles SET Rating = "
                + "(SELECT AVG(CAST(Rating AS FLOAT)) FROM Reviews WHERE TargetType = 'driver' AND TargetID = ?) "
                + "WHERE DriverID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            ps.setInt(2, driverID);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Double getAverageRatingForDriver(int driverID) {
        String sql = "SELECT AVG(CAST(Rating AS FLOAT)) AS AvgRating FROM Reviews WHERE TargetType = 'driver' AND TargetID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("AvgRating");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getAllReviews() {
        List<Map<String, Object>> reviews = new ArrayList<>();
        String sql = "SELECT r.ReviewID, r.Rating, r.Comment, r.CreatedAt, "
                + "u.FullName as ReviewerName, c.CarName "
                + "FROM Reviews r "
                + "LEFT JOIN Users u ON r.UserID = u.UserID "
                + "LEFT JOIN Contracts ct ON r.ContractID = ct.ContractID "
                + "LEFT JOIN ContractDetails cd ON ct.ContractID = cd.ContractID "
                + "LEFT JOIN Cars c ON cd.CarID = c.CarID "
                + "ORDER BY r.CreatedAt DESC";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> review = new java.util.HashMap<>();
                review.put("reviewID", rs.getInt("ReviewID"));
                review.put("rating", rs.getInt("Rating"));
                review.put("comment", rs.getString("Comment"));
                review.put("createdAt", rs.getTimestamp("CreatedAt"));
                review.put("reviewerName", rs.getString("ReviewerName"));
                review.put("carName", rs.getString("CarName"));
                reviews.add(review);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reviews;
    }

    public boolean deleteReview(int reviewID) {
        String sql = "DELETE FROM Reviews WHERE ReviewID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

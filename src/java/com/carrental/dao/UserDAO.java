package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations.
 */
public class UserDAO {

    private User mapUser(ResultSet rs) throws Exception {
        User user = new User();
        user.setUserID(rs.getInt("UserID"));

        int roleId = rs.getInt("RoleID");
        user.setRoleID(rs.wasNull() ? 0 : roleId);

        user.setFullName(rs.getString("FullName"));
        user.setEmail(rs.getString("Email"));
        user.setPasswordHash(rs.getString("PasswordHash"));
        user.setPhoneNumber(rs.getString("PhoneNumber"));
        user.setAddress(rs.getString("Address"));
        user.setLastLoginToken(rs.getString("LastLoginToken"));
        user.setEmailVerified(rs.getBoolean("IsEmailVerified"));

        // Not present in current DB schema but still exists in entity.
        user.setPhoneVerified(false);
        user.setLicenseStatus(null);

        user.setIdentityCardNumber(rs.getString("IdentityCardNumber"));
        user.setLicenseImageFront(rs.getString("LicenseImageFront"));
        user.setLicenseImageBack(rs.getString("LicenseImageBack"));
        user.setActive(rs.getBoolean("IsActive"));
        user.setCreatedAt(rs.getTimestamp("CreatedAt"));

        return user;
    }

    public User checkLogin(String email, String passwordHash) {
        String sql = "SELECT * FROM Users WHERE Email = ? AND PasswordHash = ? AND IsActive = 1";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateSessionToken(int userID, String newToken) {
        String sql = "UPDATE Users SET LastLoginToken = ? WHERE UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newToken);
            ps.setInt(2, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getSessionToken(int userID) {
        String sql = "SELECT LastLoginToken FROM Users WHERE UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("LastLoginToken");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean validateSessionToken(int userID, String token) {
        if (token == null) {
            return false;
        }
        String dbToken = getSessionToken(userID);
        return token.equals(dbToken);
    }

    public boolean clearSessionToken(int userID) {
        String sql = "UPDATE Users SET LastLoginToken = NULL WHERE UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserById(int userID) {
        String sql = "SELECT * FROM Users WHERE UserID = ? AND IsActive = 1";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE Email = ? AND IsActive = 1";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Email = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerUser(User user) {
        return createUser(user, 5);
    }

    public boolean createUserByAdmin(User user, int roleID) {
        return createUser(user, roleID);
    }
    
    public int getUserIdByEmail(String email) {
        String sql = "SELECT UserID FROM Users WHERE Email = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("UserID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean registerUser(String fullName, String email, String password, String phoneNumber, String address, int roleID) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPasswordHash(password);
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);
        return createUser(user, roleID);
    }

    private boolean createUser(User user, int roleID) {
        String sql = "INSERT INTO Users (FullName, Email, PasswordHash, PhoneNumber, Address, RoleID, IsActive, IsEmailVerified, CreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, 1, 0, GETDATE())";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getAddress());
            ps.setInt(6, roleID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserProfile(int userID, String fullName, String phoneNumber, String address) {
        String sql = "UPDATE Users SET FullName = ?, PhoneNumber = ?, Address = ? WHERE UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, phoneNumber);
            ps.setString(3, address);
            ps.setInt(4, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProfileKyc(int userID, String identityCardNumber, String licenseImageFront, String licenseImageBack) {
        String sql = "UPDATE Users SET IdentityCardNumber = ?, LicenseImageFront = ?, LicenseImageBack = ? WHERE UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, identityCardNumber);
            ps.setString(2, licenseImageFront);
            ps.setString(3, licenseImageBack);
            ps.setInt(4, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateIdentityCardNumber(int userID, String identityCardNumber) {
        String sql = "UPDATE Users SET IdentityCardNumber = ? WHERE UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, identityCardNumber);
            ps.setInt(2, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changePassword(int userID, String currentPasswordHash, String newPasswordHash) {
        String sql = "UPDATE Users SET PasswordHash = ? WHERE UserID = ? AND PasswordHash = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userID);
            ps.setString(3, currentPasswordHash);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(int userID, String newPasswordHash) {
        String sql = "UPDATE Users SET PasswordHash = ? WHERE UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deactivateUser(int userID) {
        String sql = "UPDATE Users SET IsActive = 0, LastLoginToken = NULL WHERE UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean activateUser(int userID) {
        String sql = "UPDATE Users SET IsActive = 1 WHERE UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean toggleUserActive(int userID, boolean active) {
        if (active) {
            return activateUser(userID);
        } else {
            return deactivateUser(userID);
        }
    }

    public boolean savePasswordResetToken(int userID, String token) {
        String sql = "UPDATE Users SET ResetToken = ?, ResetTokenExpiry = DATEADD(MINUTE, 15, GETDATE()) WHERE UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setInt(2, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int verifyResetToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return -1;
        }
        String sql = "SELECT UserID FROM Users WHERE ResetToken = ? AND ResetTokenExpiry > GETDATE()";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("UserID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean resetPassword(int userID, String newPassword) {
        String hashedPassword = com.carrental.util.PasswordUtil.hashPassword(newPassword);
        String sql = "UPDATE Users SET PasswordHash = ?, ResetToken = NULL, ResetTokenExpiry = NULL WHERE UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearResetToken(int userID) {
        String sql = "UPDATE Users SET ResetToken = NULL, ResetTokenExpiry = NULL WHERE UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyEmail(int userID) {
        String sql = "UPDATE Users SET IsEmailVerified = 1 WHERE UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY CreatedAt DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
}

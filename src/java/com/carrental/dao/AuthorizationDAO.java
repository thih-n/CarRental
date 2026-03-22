package com.carrental.dao;

import com.carrental.config.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for role/feature permission checks.
 */
public class AuthorizationDAO {

    public boolean hasPermission(int roleID, String endpoint, String httpMethod) {
        if (hasExactPermission(roleID, endpoint, httpMethod)) {
            return true;
        }

        // Fallback theo prefix: /admin/users/edit -> /admin/*
        String prefix = buildPrefixPattern(endpoint);
        if (prefix != null) {
            return hasExactPermission(roleID, prefix, httpMethod);
        }

        return false;
    }

    private boolean hasExactPermission(int roleID, String endpoint, String httpMethod) {
        String sql = "SELECT COUNT(*) "
                + "FROM RoleFeatures rf "
                + "JOIN Features f ON f.FeatureID = rf.FeatureID "
                + "WHERE rf.RoleID = ? "
                + "AND f.UrlEndpoint = ? "
                + "AND (f.HttpMethod IS NULL OR UPPER(f.HttpMethod) = UPPER(?) OR UPPER(f.HttpMethod) = 'ALL')";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleID);
            ps.setString(2, endpoint);
            ps.setString(3, httpMethod);

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

    private String buildPrefixPattern(String endpoint) {
        if (endpoint == null || endpoint.isEmpty() || "/".equals(endpoint)) {
            return null;
        }

        int secondSlash = endpoint.indexOf('/', 1);
        if (secondSlash <= 0) {
            return null;
        }

        return endpoint.substring(0, secondSlash) + "/*";
    }

    public Map<String, Object> getRoleById(int roleID) {
        String sql = "SELECT r.RoleID, r.RoleName, r.Description, COUNT(u.UserID) as UserCount "
                + "FROM Roles r "
                + "LEFT JOIN Users u ON r.RoleID = u.RoleID AND u.IsActive = 1 "
                + "WHERE r.RoleID = ? "
                + "GROUP BY r.RoleID, r.RoleName, r.Description";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> role = new HashMap<>();
                    role.put("roleID", rs.getInt("RoleID"));
                    role.put("roleName", rs.getString("RoleName"));
                    role.put("description", rs.getString("Description"));
                    role.put("userCount", rs.getInt("UserCount"));
                    return role;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getAllRolesWithUserCount() {
        List<Map<String, Object>> roles = new ArrayList<>();
        String sql = "SELECT r.RoleID, r.RoleName, r.Description, COUNT(u.UserID) as UserCount "
                + "FROM Roles r "
                + "LEFT JOIN Users u ON r.RoleID = u.RoleID AND u.IsActive = 1 "
                + "GROUP BY r.RoleID, r.RoleName, r.Description "
                + "ORDER BY r.RoleID";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> role = new HashMap<>();
                role.put("roleID", rs.getInt("RoleID"));
                role.put("roleName", rs.getString("RoleName"));
                role.put("description", rs.getString("Description"));
                role.put("userCount", rs.getInt("UserCount"));
                roles.add(role);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roles;
    }

    public boolean addRole(String roleName, String description) {
        String sql = "INSERT INTO Roles (RoleName, Description) VALUES (?, ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roleName);
            ps.setString(2, description);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRole(int roleID, String roleName, String description) {
        String sql = "UPDATE Roles SET RoleName = ?, Description = ? WHERE RoleID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roleName);
            ps.setString(2, description);
            ps.setInt(3, roleID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRole(int roleID) {
        String sql = "DELETE FROM Roles WHERE RoleID = ? AND RoleID NOT IN (1, 3, 4, 5)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.Amenity;
import com.carrental.entity.BodyStyle;
import com.carrental.entity.Brand;
import com.carrental.entity.CarType;
import com.carrental.entity.Location;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookingLookupDAO {

    public List<Brand> getAllBrands() {
        List<Brand> items = new ArrayList<>();
        String sql = "SELECT BrandID, BrandName FROM Brands ORDER BY BrandName";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Brand b = new Brand();
                b.setBrandID(rs.getInt("BrandID"));
                b.setBrandName(rs.getString("BrandName"));
                items.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<BodyStyle> getAllBodyStyles() {
        List<BodyStyle> items = new ArrayList<>();
        String sql = "SELECT BodyID, BodyName FROM BodyStyles ORDER BY BodyName";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BodyStyle item = new BodyStyle();
                item.setBodyID(rs.getInt("BodyID"));
                item.setBodyName(rs.getString("BodyName"));
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<CarType> getAllCarTypes() {
        List<CarType> items = new ArrayList<>();
        String sql = "SELECT TypeID, TypeName, BodyID, SeatCount, Description FROM CarTypes ORDER BY TypeName";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CarType item = new CarType();
                item.setTypeID(rs.getInt("TypeID"));
                item.setTypeName(rs.getString("TypeName"));
                item.setBodyID((Integer) rs.getObject("BodyID"));
                item.setSeatCount(rs.getInt("SeatCount"));
                item.setDescription(rs.getString("Description"));
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<Location> getAllLocations() {
        List<Location> items = new ArrayList<>();
        String sql = "SELECT LocationID, LocationName, Address, IsActive FROM Locations WHERE IsActive = 1 ORDER BY LocationName";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Location item = new Location();
                item.setLocationID(rs.getInt("LocationID"));
                item.setLocationName(rs.getString("LocationName"));
                item.setAddress(rs.getString("Address"));
                item.setIsActive(rs.getBoolean("IsActive"));
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<Amenity> getAllAmenities() {
        List<Amenity> items = new ArrayList<>();
        String sql = "SELECT AmenityID, AmenityName, IconClass FROM Amenities ORDER BY AmenityName";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Amenity item = new Amenity();
                item.setAmenityID(rs.getInt("AmenityID"));
                item.setAmenityName(rs.getString("AmenityName"));
                item.setIconClass(rs.getString("IconClass"));
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<Map<String, Object>> getAllBookings() {
        List<Map<String, Object>> bookings = new ArrayList<>();
        String sql = "SELECT c.ContractID as BookingID, c.ContractCode, u.FullName as CustomerName, car.CarName, "
                + "c.StartDateTime as StartDate, c.EndDateTime as EndDate, c.TotalAmount, cs.StatusName "
                + "FROM Contracts c "
                + "LEFT JOIN Users u ON c.CustomerID = u.UserID "
                + "LEFT JOIN Cars car ON (SELECT CarID FROM ContractDetails WHERE ContractID = c.ContractID) = car.CarID "
                + "LEFT JOIN ContractStatuses cs ON c.StatusID = cs.StatusID "
                + "ORDER BY c.StartDateTime DESC";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> booking = new java.util.HashMap<>();
                booking.put("bookingID", rs.getInt("BookingID"));
                booking.put("contractCode", rs.getString("ContractCode"));
                booking.put("customerName", rs.getString("CustomerName"));
                booking.put("carName", rs.getString("CarName"));
                booking.put("startDate", rs.getString("StartDate"));
                booking.put("endDate", rs.getString("EndDate"));
                booking.put("totalAmount", rs.getBigDecimal("TotalAmount"));
                booking.put("statusName", rs.getString("StatusName"));
                bookings.add(booking);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookings;
    }

    public Map<String, Object> getBookingById(int bookingID) {
        String sql = "SELECT c.ContractID as BookingID, c.ContractCode, u.FullName as CustomerName, u.PhoneNumber, u.Email, "
                + "car.CarName, car.PlateNumber, c.StartDateTime as StartDate, c.EndDateTime as EndDate, c.TotalAmount, cs.StatusName, "
                + "pickupLoc.LocationName as PickupLocation, returnLoc.LocationName as ReturnLocation "
                + "FROM Contracts c "
                + "LEFT JOIN Users u ON c.CustomerID = u.UserID "
                + "LEFT JOIN ContractDetails cd ON c.ContractID = cd.ContractID "
                + "LEFT JOIN Cars car ON cd.CarID = car.CarID "
                + "LEFT JOIN ContractStatuses cs ON c.StatusID = cs.StatusID "
                + "LEFT JOIN Locations pickupLoc ON c.PickUpLocationID = pickupLoc.LocationID "
                + "LEFT JOIN Locations returnLoc ON c.DropOffLocationID = returnLoc.LocationID "
                + "WHERE c.ContractID = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> booking = new java.util.HashMap<>();
                    booking.put("bookingID", rs.getInt("BookingID"));
                    booking.put("contractCode", rs.getString("ContractCode"));
                    booking.put("customerName", rs.getString("CustomerName"));
                    booking.put("phoneNumber", rs.getString("PhoneNumber"));
                    booking.put("email", rs.getString("Email"));
                    booking.put("carName", rs.getString("CarName"));
                    booking.put("plateNumber", rs.getString("PlateNumber"));
                    booking.put("startDate", rs.getString("StartDate"));
                    booking.put("endDate", rs.getString("EndDate"));
                    booking.put("totalAmount", rs.getBigDecimal("TotalAmount"));
                    booking.put("statusName", rs.getString("StatusName"));
                    booking.put("pickupLocation", rs.getString("PickupLocation"));
                    booking.put("returnLocation", rs.getString("ReturnLocation"));
                    return booking;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateBookingStatus(int bookingID, String statusName) {
        // First get the status ID from ContractStatuses
        int statusID = -1;
        String getStatusSql = "SELECT StatusID FROM ContractStatuses WHERE StatusName = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(getStatusSql)) {
            ps.setString(1, statusName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    statusID = rs.getInt("StatusID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (statusID <= 0) {
            return false;
        }
        
        // Update ContractDetails table - DetailStatus field
        String updateDetailSql = "UPDATE ContractDetails SET DetailStatus = ? WHERE ContractID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(updateDetailSql)) {
            ps.setString(1, statusName);
            ps.setInt(2, bookingID);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Update Contracts table
        String sql = "UPDATE Contracts SET StatusID = ? WHERE ContractID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statusID);
            ps.setInt(2, bookingID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, Object>> getAllLocationsAdmin() {
        List<Map<String, Object>> locations = new ArrayList<>();
        String sql = "SELECT LocationID, LocationName, Address, IsActive FROM Locations ORDER BY LocationName";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> loc = new java.util.HashMap<>();
                loc.put("locationID", rs.getInt("LocationID"));
                loc.put("locationName", rs.getString("LocationName"));
                loc.put("address", rs.getString("Address"));
                loc.put("isActive", rs.getBoolean("IsActive"));
                locations.add(loc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locations;
    }

    public Map<String, Object> getLocationById(int locationID) {
        String sql = "SELECT LocationID, LocationName, Address, IsActive FROM Locations WHERE LocationID = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, locationID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> loc = new java.util.HashMap<>();
                    loc.put("locationID", rs.getInt("LocationID"));
                    loc.put("locationName", rs.getString("LocationName"));
                    loc.put("address", rs.getString("Address"));
                    loc.put("isActive", rs.getBoolean("IsActive"));
                    return loc;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addLocation(String locationName, String address) {
        String sql = "INSERT INTO Locations (LocationName, Address, IsActive) VALUES (?, ?, 1)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, locationName);
            ps.setString(2, address);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateLocation(int locationID, String locationName, String address) {
        String sql = "UPDATE Locations SET LocationName = ?, Address = ? WHERE LocationID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, locationName);
            ps.setString(2, address);
            ps.setInt(3, locationID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLocation(int locationID) {
        String sql = "DELETE FROM Locations WHERE LocationID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, locationID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, Object>> searchBookings(String contractCode, String customerName) {
        List<Map<String, Object>> bookings = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT c.ContractID as BookingID, c.ContractCode, u.FullName as CustomerName, car.CarName, "
            + "c.StartDateTime as StartDate, c.EndDateTime as EndDate, c.TotalAmount, cs.StatusName "
            + "FROM Contracts c "
            + "LEFT JOIN Users u ON c.CustomerID = u.UserID "
            + "LEFT JOIN ContractDetails cd ON c.ContractID = cd.ContractID "
            + "LEFT JOIN Cars car ON cd.CarID = car.CarID "
            + "LEFT JOIN ContractStatuses cs ON c.StatusID = cs.StatusID "
            + "WHERE 1=1 "
        );

        if (contractCode != null && !contractCode.trim().isEmpty()) {
            sql.append(" AND c.ContractCode LIKE ? ");
        }
        if (customerName != null && !customerName.trim().isEmpty()) {
            sql.append(" AND u.FullName LIKE ? ");
        }
        sql.append(" ORDER BY c.StartDateTime DESC");

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (contractCode != null && !contractCode.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + contractCode.trim() + "%");
            }
            if (customerName != null && !customerName.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + customerName.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> booking = new java.util.HashMap<>();
                    booking.put("bookingID", rs.getInt("BookingID"));
                    booking.put("contractCode", rs.getString("ContractCode"));
                    booking.put("customerName", rs.getString("CustomerName"));
                    booking.put("carName", rs.getString("CarName"));
                    booking.put("startDate", rs.getString("StartDate"));
                    booking.put("endDate", rs.getString("EndDate"));
                    booking.put("totalAmount", rs.getBigDecimal("TotalAmount"));
                    booking.put("statusName", rs.getString("StatusName"));
                    bookings.add(booking);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookings;
    }

    public List<String> getAllCustomerNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT DISTINCT u.FullName FROM Users u " +
                     "INNER JOIN Contracts c ON u.UserID = c.CustomerID " +
                     "ORDER BY u.FullName";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                names.add(rs.getString("FullName"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return names;
    }
}

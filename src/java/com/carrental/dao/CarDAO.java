package com.carrental.dao;

import com.carrental.config.DBContext;
import com.carrental.entity.Car;
import com.carrental.entity.CarSearchResult;
import com.carrental.entity.StaffBookingItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarDAO {

    private Car mapCar(ResultSet rs) throws Exception {
        Car car = new Car();
        car.setCarID(rs.getInt("CarID"));
        car.setTypeID((Integer) rs.getObject("TypeID"));
        car.setBrandID((Integer) rs.getObject("BrandID"));
        car.setPlateNumber(rs.getString("PlateNumber"));
        car.setCarName(rs.getString("CarName"));
        try { car.setColor(rs.getString("Color")); } catch (Exception e) { }
        try { car.setProductionYear((Integer) rs.getObject("ProductionYear")); } catch (Exception e) { }
        try { car.setTransmission(rs.getString("Transmission")); } catch (Exception e) { }
        try { car.setFuelType(rs.getString("FuelType")); } catch (Exception e) { }
        car.setDefaultPricePerDay(rs.getBigDecimal("DefaultPricePerDay"));
        car.setImageURL(rs.getString("ImageURL"));
        car.setDescription(rs.getString("Description"));
        car.setStatusID((Integer) rs.getObject("StatusID"));
        car.setLocationID((Integer) rs.getObject("LocationID"));
        return car;
    }

    private CarSearchResult mapCarSearchResult(ResultSet rs) throws Exception {
        CarSearchResult item = new CarSearchResult();
        item.setCarID(rs.getInt("CarID"));
        item.setCarName(rs.getString("CarName"));
        item.setPlateNumber(rs.getString("PlateNumber"));
        item.setImageURL(rs.getString("ImageURL"));
        item.setDefaultPricePerDay(rs.getBigDecimal("DefaultPricePerDay"));
        item.setBrandID((Integer) rs.getObject("BrandID"));
        item.setBrandName(rs.getString("BrandName"));
        item.setTypeID((Integer) rs.getObject("TypeID"));
        item.setTypeName(rs.getString("TypeName"));
        item.setBodyID((Integer) rs.getObject("BodyID"));
        item.setBodyName(rs.getString("BodyName"));
        item.setSeatCount((Integer) rs.getObject("SeatCount"));
        item.setTransmission(rs.getString("Transmission"));
        item.setFuelType(rs.getString("FuelType"));
        item.setProductionYear((Integer) rs.getObject("ProductionYear"));
        item.setLocationID((Integer) rs.getObject("LocationID"));
        return item;
    }

    public List<Car> findAvailableCars(String startDateTime, String endDateTime) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT c.* FROM Cars c "
                + "WHERE c.StatusID IS NOT NULL "
                + "AND NOT EXISTS ("
                + "    SELECT 1 FROM CarSchedules cs "
                + "    WHERE cs.CarID = c.CarID "
                + "      AND cs.ScheduleStatus IN ('Booked','InProgress','Maintenance') "
                + "      AND ("
                + "          (? < DATEADD(HOUR, 1, cs.EndDateTime) AND ? > cs.StartDateTime) "
                + "          OR "
                + "          (? < cs.EndDateTime AND ? > DATEADD(HOUR, -2, cs.StartDateTime)) "
                + "      )"
                + ")";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, startDateTime);
            ps.setString(2, endDateTime);
            ps.setString(3, startDateTime);
            ps.setString(4, endDateTime);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cars.add(mapCar(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cars;
    }

    public List<CarSearchResult> searchAvailableCars(
            String startDateTime,
            String endDateTime,
            Integer locationID,
            Integer carID,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            String keyword,
            List<Integer> brandIDs,
            List<Integer> typeIDs,
            List<Integer> amenityIDs,
            Integer seatCount,
            String transmission,
            String fuelType,
            String sortBy,
            String sortOrder,
            int page,
            int pageSize) {

        List<CarSearchResult> items = new ArrayList<>();
        String orderBy = buildOrderBy(sortBy, sortOrder);
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT c.CarID, c.CarName, c.PlateNumber, c.ImageURL, c.DefaultPricePerDay, c.BrandID, b.BrandName, ")
           .append("c.TypeID, ct.TypeName, ct.BodyID, bs.BodyName, ct.SeatCount, c.Transmission, c.FuelType, c.ProductionYear, c.LocationID, c.StatusID ")
           .append("FROM Cars c ")
           .append("LEFT JOIN Brands b ON b.BrandID = c.BrandID ")
           .append("LEFT JOIN CarTypes ct ON ct.TypeID = c.TypeID ")
           .append("LEFT JOIN BodyStyles bs ON bs.BodyID = ct.BodyID ")
           .append("WHERE c.StatusID = 1 ")  // Only available cars
           .append("AND NOT EXISTS (")
           .append("    SELECT 1 FROM CarSchedules cs ")
           .append("    WHERE cs.CarID = c.CarID ")
           .append("      AND cs.ScheduleStatus IN ('Booked','InUse','Maintenance') ")
           .append("      AND (")
           .append("          (? < DATEADD(HOUR, 1, cs.EndDateTime) AND ? > cs.StartDateTime) ")
           .append("          OR ")
           .append("          (? < cs.EndDateTime AND ? > DATEADD(HOUR, -2, cs.StartDateTime)) ")
           .append("      )")
           .append(") ")
           .append("AND NOT EXISTS (")
           .append("    SELECT 1 FROM CarHoldings ch ")
           .append("    WHERE ch.CarID = c.CarID ")
           .append("      AND ch.ExpiryTime > GETDATE() ")
           .append("      AND (? < ch.RentEndDateTime AND ? > ch.RentStartDateTime) ")
           .append(") ");

        // Params for CarSchedules: 4 params (start/end for both buffer checks)
        params.add(startDateTime);
        params.add(endDateTime);
        params.add(startDateTime);
        params.add(endDateTime);
        // Params for CarHoldings: 2 params
        params.add(startDateTime);
        params.add(endDateTime);

        if (locationID != null) {
            sql.append("AND c.LocationID = ? ");
            params.add(locationID);
        }
        if (carID != null) {
            sql.append("AND c.CarID = ? ");
            params.add(carID);
        }
        if (minPrice != null) {
            sql.append("AND c.DefaultPricePerDay >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append("AND c.DefaultPricePerDay <= ? ");
            params.add(maxPrice);
        }

        String keywordLike = (keyword == null || keyword.trim().isEmpty()) ? null : "%" + keyword.trim() + "%";
        if (keywordLike != null) {
            sql.append("AND (c.CarName LIKE ? OR c.PlateNumber LIKE ?) ");
            params.add(keywordLike);
            params.add(keywordLike);
        }

        if (brandIDs != null && !brandIDs.isEmpty()) {
            sql.append("AND c.BrandID IN (").append(buildInClause(brandIDs.size())).append(") ");
            params.addAll(brandIDs);
        }
        if (typeIDs != null && !typeIDs.isEmpty()) {
            sql.append("AND c.TypeID IN (").append(buildInClause(typeIDs.size())).append(") ");
            params.addAll(typeIDs);
        }
        if (amenityIDs != null && !amenityIDs.isEmpty()) {
            sql.append("AND EXISTS (SELECT 1 FROM CarAmenities ca WHERE ca.CarID = c.CarID AND ca.AmenityID IN (")
               .append(buildInClause(amenityIDs.size()))
               .append(")) ");
            params.addAll(amenityIDs);
        }

        if (seatCount != null) {
            sql.append("AND ct.SeatCount = ? ");
            params.add(seatCount);
        }
        if (transmission != null) {
            sql.append("AND c.Transmission = ? ");
            params.add(transmission);
        }
        if (fuelType != null) {
            sql.append("AND c.FuelType = ? ");
            params.add(fuelType);
        }

        sql.append(orderBy).append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapCarSearchResult(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public int countAvailableCars(
            String startDateTime,
            String endDateTime,
            Integer locationID,
            Integer carID,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            String keyword,
            List<Integer> brandIDs,
            List<Integer> typeIDs,
            List<Integer> amenityIDs,
            Integer seatCount,
            String transmission,
            String fuelType) {

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT COUNT(*) ")
           .append("FROM Cars c ")
           .append("LEFT JOIN CarTypes ct ON ct.TypeID = c.TypeID ")
           .append("WHERE c.StatusID = 1 ")  // Only available cars
           .append("AND NOT EXISTS (")
           .append("    SELECT 1 FROM CarSchedules cs ")
           .append("    WHERE cs.CarID = c.CarID ")
           .append("      AND cs.ScheduleStatus IN ('Booked','InUse','Maintenance') ")
           .append("      AND (")
           .append("          (? < DATEADD(HOUR, 1, cs.EndDateTime) AND ? > cs.StartDateTime) ")
           .append("          OR ")
           .append("          (? < cs.EndDateTime AND ? > DATEADD(HOUR, -2, cs.StartDateTime)) ")
           .append("      )")
           .append(") ")
           .append("AND NOT EXISTS (")
           .append("    SELECT 1 FROM CarHoldings ch ")
           .append("    WHERE ch.CarID = c.CarID ")
           .append("      AND ch.ExpiryTime > GETDATE() ")
           .append("      AND (? < ch.RentEndDateTime AND ? > ch.RentStartDateTime) ")
           .append(") ");

        params.add(startDateTime);
        params.add(endDateTime);
        params.add(startDateTime);
        params.add(endDateTime);
        // Params for CarHoldings: 2 params
        params.add(startDateTime);
        params.add(endDateTime);

        if (locationID != null) {
            sql.append("AND c.LocationID = ? ");
            params.add(locationID);
        }
        if (carID != null) {
            sql.append("AND c.CarID = ? ");
            params.add(carID);
        }
        if (minPrice != null) {
            sql.append("AND c.DefaultPricePerDay >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append("AND c.DefaultPricePerDay <= ? ");
            params.add(maxPrice);
        }

        String keywordLike = (keyword == null || keyword.trim().isEmpty()) ? null : "%" + keyword.trim() + "%";
        if (keywordLike != null) {
            sql.append("AND (c.CarName LIKE ? OR c.PlateNumber LIKE ?) ");
            params.add(keywordLike);
            params.add(keywordLike);
        }

        if (brandIDs != null && !brandIDs.isEmpty()) {
            sql.append("AND c.BrandID IN (").append(buildInClause(brandIDs.size())).append(") ");
            params.addAll(brandIDs);
        }
        if (typeIDs != null && !typeIDs.isEmpty()) {
            sql.append("AND c.TypeID IN (").append(buildInClause(typeIDs.size())).append(") ");
            params.addAll(typeIDs);
        }
        if (amenityIDs != null && !amenityIDs.isEmpty()) {
            sql.append("AND EXISTS (SELECT 1 FROM CarAmenities ca WHERE ca.CarID = c.CarID AND ca.AmenityID IN (")
               .append(buildInClause(amenityIDs.size()))
               .append(")) ");
            params.addAll(amenityIDs);
        }

        if (seatCount != null) {
            sql.append("AND ct.SeatCount = ? ");
            params.add(seatCount);
        }
        if (transmission != null) {
            sql.append("AND c.Transmission = ? ");
            params.add(transmission);
        }
        if (fuelType != null) {
            sql.append("AND c.FuelType = ? ");
            params.add(fuelType);
        }

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
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

    public Car getById(int carID) {
        String sql = "SELECT * FROM Cars WHERE CarID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCar(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public Car getCarById(int carID) {
        return getById(carID);
    }

    public List<CarSearchResult> getAllCars() {
        return searchCars(null, null, null);
    }
    
    public List<CarSearchResult> searchCars(String search, String status, String type) {
        return searchCarsPaging(1, Integer.MAX_VALUE, search, status, type, null);
    }
    
    public List<CarSearchResult> searchCarsPaging(int page, int pageSize, String search, String status, String type, String brand) {
        List<CarSearchResult> items = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT c.CarID, c.CarName, c.PlateNumber, c.ImageURL, c.DefaultPricePerDay, c.BrandID, b.BrandName, "
                + "c.TypeID, ct.TypeName, ct.BodyID, bs.BodyName, ct.SeatCount, c.Transmission, c.FuelType, c.ProductionYear, c.LocationID, "
                + "cs.StatusName "
                + "FROM Cars c "
                + "LEFT JOIN Brands b ON b.BrandID = c.BrandID "
                + "LEFT JOIN CarTypes ct ON ct.TypeID = c.TypeID "
                + "LEFT JOIN BodyStyles bs ON bs.BodyID = ct.BodyID "
                + "LEFT JOIN CarStatuses cs ON cs.StatusID = c.StatusID "
                + "WHERE 1=1 ");
        
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (c.CarName LIKE ? OR c.PlateNumber LIKE ?) ");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND cs.StatusName = ? ");
        }
        if (type != null && !type.trim().isEmpty()) {
            sql.append("AND ct.TypeName = ? ");
        }
        if (brand != null && !brand.trim().isEmpty()) {
            sql.append("AND b.BrandName LIKE ? ");
        }
        
        sql.append("ORDER BY c.CarID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (search != null && !search.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + search + "%");
                ps.setString(paramIndex++, "%" + search + "%");
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (type != null && !type.trim().isEmpty()) {
                ps.setString(paramIndex++, type);
            }
            if (brand != null && !brand.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + brand + "%");
            }
            ps.setInt(paramIndex++, (page - 1) * pageSize);
            ps.setInt(paramIndex++, pageSize);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CarSearchResult car = new CarSearchResult();
                    car.setCarID(rs.getInt("CarID"));
                    car.setCarName(rs.getString("CarName"));
                    car.setPlateNumber(rs.getString("PlateNumber"));
                    car.setImageURL(rs.getString("ImageURL"));
                    car.setDefaultPricePerDay(rs.getBigDecimal("DefaultPricePerDay"));
                    car.setBrandID((Integer) rs.getObject("BrandID"));
                    car.setBrandName(rs.getString("BrandName"));
                    car.setTypeID((Integer) rs.getObject("TypeID"));
                    car.setTypeName(rs.getString("TypeName"));
                    car.setBodyID((Integer) rs.getObject("BodyID"));
                    car.setBodyName(rs.getString("BodyName"));
                    car.setSeatCount((Integer) rs.getObject("SeatCount"));
                    car.setTransmission(rs.getString("Transmission"));
                    car.setFuelType(rs.getString("FuelType"));
                    car.setProductionYear((Integer) rs.getObject("ProductionYear"));
                    car.setLocationID((Integer) rs.getObject("LocationID"));
                    car.setStatusName(rs.getString("StatusName"));
                    items.add(car);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
    
    public List<String> getAllCarStatuses() {
        List<String> statuses = new ArrayList<>();
        String sql = "SELECT StatusName FROM CarStatuses ORDER BY StatusID";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                statuses.add(rs.getString("StatusName"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statuses;
    }
    
    public int getTotalCars(String search, String status, String type, String brand) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM Cars c "
            + "LEFT JOIN Brands b ON b.BrandID = c.BrandID "
            + "LEFT JOIN CarTypes ct ON ct.TypeID = c.TypeID "
            + "LEFT JOIN CarStatuses cs ON cs.StatusID = c.StatusID "
            + "WHERE 1=1 ");
        
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (c.CarName LIKE ? OR c.PlateNumber LIKE ?) ");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND cs.StatusName = ? ");
        }
        if (type != null && !type.trim().isEmpty()) {
            sql.append("AND ct.TypeName = ? ");
        }
        if (brand != null && !brand.trim().isEmpty()) {
            sql.append("AND b.BrandName LIKE ? ");
        }
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (search != null && !search.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + search + "%");
                ps.setString(paramIndex++, "%" + search + "%");
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (type != null && !type.trim().isEmpty()) {
                ps.setString(paramIndex++, type);
            }
            if (brand != null && !brand.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + brand + "%");
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
    
    public List<Map<String, Object>> getAllCarStatusesFull() {
        List<Map<String, Object>> statuses = new ArrayList<>();
        String sql = "SELECT StatusID, StatusName FROM CarStatuses ORDER BY StatusID";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> status = new HashMap<>();
                status.put("statusID", rs.getInt("StatusID"));
                status.put("statusName", rs.getString("StatusName"));
                statuses.add(status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statuses;
    }
    
    public List<StaffBookingItem> getBookingsByCarId(int carID) {
        List<StaffBookingItem> bookings = new ArrayList<>();
        String sql = "SELECT c.ContractID, c.ContractCode, c.CustomerID, u.FullName as CustomerName, u.Email as CustomerEmail, "
                + "c.StartDateTime, c.EndDateTime, cd.DetailStatus, cd.CarID, cd.DriverID "
                + "FROM Contracts c "
                + "JOIN ContractDetails cd ON c.ContractID = cd.ContractID "
                + "LEFT JOIN Users u ON u.UserID = c.CustomerID "
                + "WHERE cd.CarID = ? "
                + "ORDER BY c.StartDateTime DESC";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StaffBookingItem item = new StaffBookingItem();
                    item.setContractID(rs.getInt("ContractID"));
                    item.setContractCode(rs.getString("ContractCode"));
                    item.setCustomerID(rs.getInt("CustomerID"));
                    item.setCustomerName(rs.getString("CustomerName"));
                    item.setCustomerEmail(rs.getString("CustomerEmail"));
                    item.setDriverID(rs.getInt("DriverID"));
                    item.setStartDateTime(rs.getTimestamp("StartDateTime"));
                    item.setEndDateTime(rs.getTimestamp("EndDateTime"));
                    item.setDetailStatus(rs.getString("DetailStatus"));
                    item.setStatusName(rs.getString("DetailStatus"));
                    bookings.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public boolean updateCarStatus(int carID, String newStatus) {
        // First get status ID from CarStatuses
        int statusID = -1;
        String getStatusSql = "SELECT StatusID FROM CarStatuses WHERE StatusName = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(getStatusSql)) {
            ps.setString(1, newStatus);
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
        
        String sql = "UPDATE Cars SET StatusID = ? WHERE CarID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statusID);
            ps.setInt(2, carID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int addCar(String carName, String licensePlate, int carTypeID, int brandID, int locationID, int statusID,
            String color, Integer productionYear, String transmission, String fuelType, double pricePerDay, 
            String imageURL, String description) {
        String sql = "INSERT INTO Cars (CarName, PlateNumber, TypeID, BrandID, LocationID, StatusID, Color, ProductionYear, Transmission, FuelType, DefaultPricePerDay, ImageURL, Description) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, carName);
            ps.setString(2, licensePlate);
            ps.setInt(3, carTypeID);
            ps.setInt(4, brandID);
            ps.setInt(5, locationID);
            ps.setInt(6, statusID);
            ps.setString(7, color);
            ps.setObject(8, productionYear);
            ps.setString(9, transmission);
            ps.setString(10, fuelType);
            ps.setDouble(11, pricePerDay);
            ps.setString(12, imageURL);
            ps.setString(13, description);
            int result = ps.executeUpdate();
            System.out.println("[DEBUG] CarDAO.addCar - executeUpdate result: " + result);
            
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean updateCar(int carID, String carName, String licensePlate, int carTypeID, int brandID, 
            int locationID, int statusID, String color, Integer productionYear, String transmission, 
            String fuelType, double pricePerDay, String imageURL, String description) {
        String sql = "UPDATE Cars SET CarName = ?, PlateNumber = ?, TypeID = ?, BrandID = ?, LocationID = ?, "
                + "StatusID = ?, Color = ?, ProductionYear = ?, Transmission = ?, FuelType = ?, "
                + "DefaultPricePerDay = ?, ImageURL = ?, Description = ? WHERE CarID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, carName);
            ps.setString(2, licensePlate);
            ps.setInt(3, carTypeID);
            ps.setInt(4, brandID);
            ps.setInt(5, locationID);
            ps.setInt(6, statusID);
            ps.setString(7, color);
            ps.setObject(8, productionYear);
            ps.setString(9, transmission);
            ps.setString(10, fuelType);
            ps.setDouble(11, pricePerDay);
            ps.setString(12, imageURL);
            ps.setString(13, description);
            ps.setInt(14, carID);
            int result = ps.executeUpdate();
            System.out.println("[DEBUG] CarDAO.updateCar - executeUpdate result: " + result);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCar(int carID) {
        String sql = "DELETE FROM Cars WHERE CarID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCarLocationAndStatus(int carID, int locationID, int statusID) {
        String sql = "UPDATE Cars SET LocationID = ?, StatusID = ? WHERE CarID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, locationID);
            ps.setInt(2, statusID);
            ps.setInt(3, carID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CarSearchResult> getCarsPaging(int page, int pageSize, String sortBy, String sortOrder) {
        List<CarSearchResult> items = new ArrayList<>();
        String orderBy = buildOrderBy(sortBy, sortOrder);

        String sql = "SELECT c.CarID, c.CarName, c.PlateNumber, c.ImageURL, c.DefaultPricePerDay, c.BrandID, b.BrandName, "
                + "c.TypeID, ct.TypeName, ct.BodyID, bs.BodyName, ct.SeatCount, c.Transmission, c.FuelType, c.ProductionYear, c.LocationID "
                + "FROM Cars c "
                + "LEFT JOIN Brands b ON b.BrandID = c.BrandID "
                + "LEFT JOIN CarTypes ct ON ct.TypeID = c.TypeID "
                + "LEFT JOIN BodyStyles bs ON bs.BodyID = ct.BodyID "
                + orderBy
                + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapCarSearchResult(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public int countCars() {
        String sql = "SELECT COUNT(*) FROM Cars";
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

    public List<CarSearchResult> searchCars(
            Integer locationID,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            List<Integer> brandIDs,
            List<Integer> typeIDs,
            List<Integer> amenityIDs,
            String sortBy,
            String sortOrder,
            int page,
            int pageSize) {
        List<CarSearchResult> items = new ArrayList<>();
        String orderBy = buildOrderBy(sortBy, sortOrder);
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT c.CarID, c.CarName, c.PlateNumber, c.ImageURL, c.DefaultPricePerDay, c.BrandID, b.BrandName, ")
           .append("c.TypeID, ct.TypeName, ct.BodyID, bs.BodyName, ct.SeatCount, c.Transmission, c.FuelType, c.ProductionYear, c.LocationID ")
           .append("FROM Cars c ")
           .append("LEFT JOIN Brands b ON b.BrandID = c.BrandID ")
           .append("LEFT JOIN CarTypes ct ON ct.TypeID = c.TypeID ")
           .append("LEFT JOIN BodyStyles bs ON bs.BodyID = ct.BodyID ")
           .append("WHERE 1=1 ");

        if (locationID != null) {
            sql.append("AND c.LocationID = ? ");
            params.add(locationID);
        }
        if (minPrice != null) {
            sql.append("AND c.DefaultPricePerDay >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append("AND c.DefaultPricePerDay <= ? ");
            params.add(maxPrice);
        }
        if (brandIDs != null && !brandIDs.isEmpty()) {
            sql.append("AND c.BrandID IN (").append(buildInClause(brandIDs.size())).append(") ");
            params.addAll(brandIDs);
        }
        if (typeIDs != null && !typeIDs.isEmpty()) {
            sql.append("AND c.TypeID IN (").append(buildInClause(typeIDs.size())).append(") ");
            params.addAll(typeIDs);
        }
        if (amenityIDs != null && !amenityIDs.isEmpty()) {
            sql.append("AND EXISTS (SELECT 1 FROM CarAmenities ca WHERE ca.CarID = c.CarID AND ca.AmenityID IN (")
               .append(buildInClause(amenityIDs.size()))
               .append(")) ");
            params.addAll(amenityIDs);
        }

        sql.append(orderBy).append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapCarSearchResult(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public int countCars(
            Integer locationID,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            List<Integer> brandIDs,
            List<Integer> typeIDs,
            List<Integer> amenityIDs) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT COUNT(*) FROM Cars c WHERE 1=1 ");
        if (locationID != null) {
            sql.append("AND c.LocationID = ? ");
            params.add(locationID);
        }
        if (minPrice != null) {
            sql.append("AND c.DefaultPricePerDay >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append("AND c.DefaultPricePerDay <= ? ");
            params.add(maxPrice);
        }
        if (brandIDs != null && !brandIDs.isEmpty()) {
            sql.append("AND c.BrandID IN (").append(buildInClause(brandIDs.size())).append(") ");
            params.addAll(brandIDs);
        }
        if (typeIDs != null && !typeIDs.isEmpty()) {
            sql.append("AND c.TypeID IN (").append(buildInClause(typeIDs.size())).append(") ");
            params.addAll(typeIDs);
        }
        if (amenityIDs != null && !amenityIDs.isEmpty()) {
            sql.append("AND EXISTS (SELECT 1 FROM CarAmenities ca WHERE ca.CarID = c.CarID AND ca.AmenityID IN (")
               .append(buildInClause(amenityIDs.size()))
               .append(")) ");
            params.addAll(amenityIDs);
        }

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
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

    private String buildInClause(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("?");
        }
        return sb.toString();
    }

    private String buildOrderBy(String sortBy, String sortOrder) {
        String order = "desc".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";
        if ("name".equalsIgnoreCase(sortBy)) {
            return " ORDER BY c.CarName " + order;
        }
        if ("year".equalsIgnoreCase(sortBy)) {
            return " ORDER BY c.ProductionYear " + order;
        }
        return " ORDER BY c.DefaultPricePerDay " + order;
    }
}

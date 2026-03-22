package com.carrental.controller.admin;

import com.carrental.dao.CarDAO;
import com.carrental.dao.BookingDAO;
import com.carrental.dao.BookingLookupDAO;
import com.carrental.dao.BrandDAO;
import com.carrental.dao.CarTypeDAO;
import com.carrental.dao.CarImageDAO;
import com.carrental.dao.UserDAO;
import com.carrental.entity.Car;
import com.carrental.entity.CarImage;
import com.carrental.entity.StaffBookingItem;
import com.carrental.entity.Brand;
import com.carrental.entity.CarType;
import com.carrental.entity.Email;
import com.carrental.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.File;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet(name = "AdminCarManagementServlet", urlPatterns = {"/admin/cars"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 1024 * 1024 * 5,
    maxRequestSize = 1024 * 1024 * 10
)
public class AdminCarManagementServlet extends HttpServlet {

    private final CarDAO carDAO = new CarDAO();
    private final CarImageDAO carImageDAO = new CarImageDAO();
    private final BookingLookupDAO lookupDAO = new BookingLookupDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final BrandDAO brandDAO = new BrandDAO();
    private final CarTypeDAO carTypeDAO = new CarTypeDAO();
    private static final String UPLOAD_DIR = "uploads/cars";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // Handle AJAX request for brand CRUD
        if ("addBrand".equals(action)) {
            handleAddBrand(request, response);
            return;
        }
        if ("editBrand".equals(action)) {
            handleEditBrand(request, response);
            return;
        }
        if ("deleteBrand".equals(action)) {
            handleDeleteBrand(request, response);
            return;
        }
        
        // Handle AJAX request for carType CRUD
        if ("addCarType".equals(action)) {
            handleAddCarType(request, response);
            return;
        }
        if ("editCarType".equals(action)) {
            handleEditCarType(request, response);
            return;
        }
        if ("deleteCarType".equals(action)) {
            handleDeleteCarType(request, response);
            return;
        }
        
        // Handle AJAX request to get car bookings
        if ("getCarBookings".equals(action)) {
            handleGetCarBookings(request, response);
            return;
        }
        
        // Handle lock/unlock
        if ("lock".equals(action) || "unlock".equals(action)) {
            int carID = Integer.parseInt(request.getParameter("id"));
            
            // Get bookings that will be affected
            List<StaffBookingItem> carBookings = carDAO.getBookingsByCarId(carID);
            
            if ("lock".equals(action)) {
                // Get bookings that will be cancelled (Booked status)
                List<StaffBookingItem> affectedBookings = new java.util.ArrayList<>();
                for (StaffBookingItem booking : carBookings) {
                    String status = booking.getStatusName();
                    if (status != null && "Booked".equalsIgnoreCase(status)) {
                        affectedBookings.add(booking);
                    }
                }
                
                // If there's affected bookings, show confirmation modal with list
                if (!affectedBookings.isEmpty()) {
                    request.setAttribute("affectedBookings", affectedBookings);
                    request.setAttribute("carID", carID);
                    request.setAttribute("hasAffectedBookings", true);
                    Car car = carDAO.getCarById(carID);
                    request.setAttribute("carName", car != null ? car.getCarName() : "");
                    request.setAttribute("car", car);
                    request.setAttribute("cars", carDAO.getAllCars());
                    request.setAttribute("carTypes", lookupDAO.getAllCarTypes());
                    request.setAttribute("carStatuses", carDAO.getAllCarStatuses());
                    request.getRequestDispatcher("/views/admin/car-management.jsp").forward(request, response);
                    return;
                } else {
                    // No affected bookings, proceed to lock directly
                    carDAO.updateCarStatus(carID, "Inactive");
                    response.sendRedirect(request.getContextPath() + "/admin/cars?locked=1&cancelledBookings=0");
                    return;
                }
            } else {
                // For unlock: set car to Available
                carDAO.updateCarStatus(carID, "Available");
                response.sendRedirect(request.getContextPath() + "/admin/cars?unlocked=1");
            }
            return;
        }
        
        // Confirm lock action
        if ("confirmLock".equals(action)) {
            int carID = Integer.parseInt(request.getParameter("id"));
            Email emailService = new Email();
            UserDAO userDAO = new UserDAO();
            
            // Get current admin user
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("USER_SESSION");
            Integer cancelledBy = currentUser != null ? currentUser.getUserID() : null;
            String cancelReason = "Xe đang cần sửa chữa";
            
            // Get bookings to cancel
            List<StaffBookingItem> carBookings = carDAO.getBookingsByCarId(carID);
            int cancelledCount = 0;
            for (StaffBookingItem booking : carBookings) {
                String status = booking.getStatusName();
                if (status != null && "Booked".equalsIgnoreCase(status)) {
                    bookingDAO.cancelBooking(booking.getBookingID(), cancelReason, cancelledBy);
                    cancelledCount++;
                    
                    // Send email to customer
                    if (booking.getCustomerEmail() != null && !booking.getCustomerEmail().isEmpty()) {
                        emailService.sendTripCancelledEmail(
                            booking.getCustomerEmail(),
                            booking.getCustomerName(),
                            String.valueOf(booking.getContractID()),
                            cancelReason
                        );
                    }
                    
                    // Send email to driver if assigned
                    if (booking.getDriverID() != null) {
                        User driver = userDAO.getUserById(booking.getDriverID());
                        if (driver != null && driver.getEmail() != null) {
                            emailService.sendTripCancelledEmail(
                                driver.getEmail(),
                                driver.getFullName(),
                                String.valueOf(booking.getContractID()),
                                cancelReason
                            );
                        }
                    }
                }
            }
            
            carDAO.updateCarStatus(carID, "Inactive");
            response.sendRedirect(request.getContextPath() + "/admin/cars?locked=1&cancelledBookings=" + cancelledCount);
            return;
        }
        
        // Handle create or edit
        if ("create".equals(action) || "edit".equals(action)) {
            request.setAttribute("carTypes", lookupDAO.getAllCarTypes());
            request.setAttribute("brands", lookupDAO.getAllBrands());
            request.setAttribute("locations", lookupDAO.getAllLocations());
            
            if ("edit".equals(action)) {
                int carID = Integer.parseInt(request.getParameter("id"));
                Car car = carDAO.getCarById(carID);
                request.setAttribute("car", car);
                
                // Load car images
                List<CarImage> carImages = carImageDAO.getImagesByCarId(carID);
                request.setAttribute("carImages", carImages);
            }
            
            request.getRequestDispatcher("/views/admin/car-form.jsp").forward(request, response);
            return;
        }
        
        // Handle search and filter
        String search = request.getParameter("search");
        String status = request.getParameter("status");
        String type = request.getParameter("type");
        String brandSearch = request.getParameter("brandSearch");
        
        // Get current tab for preserving tab state
        String currentTab = request.getParameter("tab");
        if (currentTab == null) {
            currentTab = "cars";
        }
        request.setAttribute("currentTab", currentTab);
        
        // Pagination for cars
        int page = 1;
        int pageSize = 10;
        try {
            if (request.getParameter("page") != null) {
                page = Integer.parseInt(request.getParameter("page"));
            }
        } catch (NumberFormatException e) {
            page = 1;
        }
        
        // Get total cars count for pagination
        int totalCars = carDAO.getTotalCars(search, status, type, brandSearch);
        int totalCarPages = (int) Math.ceil((double) totalCars / pageSize);
        
        List<com.carrental.entity.CarSearchResult> cars = carDAO.searchCarsPaging(page, pageSize, search, status, type, brandSearch);
        
        request.setAttribute("cars", cars);
        request.setAttribute("carTypes", lookupDAO.getAllCarTypes());
        request.setAttribute("carStatuses", carDAO.getAllCarStatuses());
        
        // Pagination attributes for cars
        request.setAttribute("currentPage", page);
        request.setAttribute("totalCarPages", totalCarPages);
        request.setAttribute("totalCars", totalCars);
        
        // Handle pagination and search for brands
        String brandKeyword = request.getParameter("brandKeyword");
        int brandPage = 1;
        try {
            if (request.getParameter("brandPage") != null) {
                brandPage = Integer.parseInt(request.getParameter("brandPage"));
            }
        } catch (NumberFormatException e) {
            brandPage = 1;
        }
        int brandPageSize = 10;
        int totalBrands = brandDAO.getTotalBrands(brandKeyword);
        int totalBrandPages = (int) Math.ceil((double) totalBrands / brandPageSize);
        List<Brand> brandList = brandDAO.getBrandsPaging(brandPage, brandPageSize, brandKeyword);
        
        request.setAttribute("brands", brandList);
        request.setAttribute("brandKeyword", brandKeyword);
        request.setAttribute("brandPage", brandPage);
        request.setAttribute("totalBrandPages", totalBrandPages);
        
        // Handle pagination and search for car types
        String carTypeKeyword = request.getParameter("carTypeKeyword");
        int carTypePage = 1;
        try {
            if (request.getParameter("carTypePage") != null) {
                carTypePage = Integer.parseInt(request.getParameter("carTypePage"));
            }
        } catch (NumberFormatException e) {
            carTypePage = 1;
        }
        int carTypePageSize = 10;
        int totalCarTypes = carTypeDAO.getTotalCarTypes(carTypeKeyword);
        int totalCarTypePages = (int) Math.ceil((double) totalCarTypes / carTypePageSize);
        List<CarType> carTypeList = carTypeDAO.getCarTypesPaging(carTypePage, carTypePageSize, carTypeKeyword);
        
        request.setAttribute("carTypesList", carTypeList);
        request.setAttribute("carTypeKeyword", carTypeKeyword);
        request.setAttribute("carTypePage", carTypePage);
        request.setAttribute("totalCarTypePages", totalCarTypePages);
        
        request.getRequestDispatcher("/views/admin/car-management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        System.out.println("[DEBUG] POST action: " + action);
        
        if (action == null || action.isEmpty()) {
            System.out.println("[DEBUG] action is null, redirecting to list");
            response.sendRedirect(request.getContextPath() + "/admin/cars");
            return;
        }
        
        if ("add".equals(action) || "create".equals(action)) {
            try {
                String carName = request.getParameter("carName");
                String licensePlate = request.getParameter("plateNumber");
                
                String carTypeIDStr = request.getParameter("carTypeID");
                String brandIDStr = request.getParameter("brandID");
                String locationIDStr = request.getParameter("locationID");
                
                if (carTypeIDStr == null || carTypeIDStr.trim().isEmpty() ||
                    brandIDStr == null || brandIDStr.trim().isEmpty() ||
                    locationIDStr == null || locationIDStr.trim().isEmpty()) {
                    request.setAttribute("error", "Vui lòng chọn đầy đủ thông tin: Loại xe, Hãng xe, Địa điểm");
                    request.setAttribute("carTypes", lookupDAO.getAllCarTypes());
                    request.setAttribute("brands", lookupDAO.getAllBrands());
                    request.setAttribute("locations", lookupDAO.getAllLocations());
                    request.getRequestDispatcher("/views/admin/car-form.jsp").forward(request, response);
                    return;
                }
                
                int carTypeID = Integer.parseInt(carTypeIDStr);
                int brandID = Integer.parseInt(brandIDStr);
                int locationID = Integer.parseInt(locationIDStr);
                
                String color = request.getParameter("color");
                String productionYearStr = request.getParameter("productionYear");
                Integer productionYear = (productionYearStr == null || productionYearStr.trim().isEmpty()) ? null : Integer.parseInt(productionYearStr);
                String transmission = request.getParameter("transmission");
                String fuelType = request.getParameter("fuelType");
                double pricePerDay = Double.parseDouble(request.getParameter("pricePerDay"));
                String imageURL = request.getParameter("imageURL");
                String description = request.getParameter("description");
                
                // Default statusID = 1 (Available)
                int newCarId = carDAO.addCar(carName, licensePlate, carTypeID, brandID, locationID, 1,
                        color, productionYear, transmission, fuelType, pricePerDay, imageURL, description);
                
                if (newCarId > 0) {
                    // Handle car images for new car
                    try {
                        handleCarImageUpload(request, newCarId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    response.sendRedirect(request.getContextPath() + "/admin/cars?success=add");
                } else {
                    request.setAttribute("error", "Không thể thêm xe. Vui lòng kiểm tra lại thông tin.");
                    request.setAttribute("carTypes", lookupDAO.getAllCarTypes());
                    request.setAttribute("brands", lookupDAO.getAllBrands());
                    request.setAttribute("locations", lookupDAO.getAllLocations());
                    request.getRequestDispatcher("/views/admin/car-form.jsp").forward(request, response);
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Lỗi: " + e.getMessage());
                request.setAttribute("carTypes", lookupDAO.getAllCarTypes());
                request.setAttribute("brands", lookupDAO.getAllBrands());
                request.setAttribute("locations", lookupDAO.getAllLocations());
                try {
                    request.getRequestDispatcher("/views/admin/car-form.jsp").forward(request, response);
                } catch (Exception ex) {
                    response.sendRedirect(request.getContextPath() + "/admin/cars?error=1");
                }
                return;
            }
        }
        
        if ("update".equals(action) || "edit".equals(action)) {
            try {
                int carID = Integer.parseInt(request.getParameter("carID"));
                String carName = request.getParameter("carName");
                String licensePlate = request.getParameter("plateNumber");
                
                String carTypeIDStr = request.getParameter("carTypeID");
                String brandIDStr = request.getParameter("brandID");
                String locationIDStr = request.getParameter("locationID");
                
                if (carTypeIDStr == null || carTypeIDStr.trim().isEmpty() ||
                    brandIDStr == null || brandIDStr.trim().isEmpty() ||
                    locationIDStr == null || locationIDStr.trim().isEmpty()) {
                    request.setAttribute("error", "Vui lòng chọn đầy đủ thông tin: Loại xe, Hãng xe, Địa điểm");
                    request.setAttribute("carTypes", lookupDAO.getAllCarTypes());
                    request.setAttribute("brands", lookupDAO.getAllBrands());
                    request.setAttribute("locations", lookupDAO.getAllLocations());
                    request.getRequestDispatcher("/views/admin/car-form.jsp").forward(request, response);
                    return;
                }
                
                int carTypeID = Integer.parseInt(carTypeIDStr);
                int brandID = Integer.parseInt(brandIDStr);
                int locationID = Integer.parseInt(locationIDStr);
                
                String color = request.getParameter("color");
                String productionYearStr = request.getParameter("productionYear");
                Integer productionYear = (productionYearStr == null || productionYearStr.trim().isEmpty()) ? null : Integer.parseInt(productionYearStr);
                String transmission = request.getParameter("transmission");
                String fuelType = request.getParameter("fuelType");
                double pricePerDay = Double.parseDouble(request.getParameter("pricePerDay"));
                String imageURL = request.getParameter("imageURL");
                String description = request.getParameter("description");
                
                // Get current car to preserve statusID
                Car existingCar = carDAO.getCarById(carID);
                int statusID = existingCar != null && existingCar.getStatusID() != null ? existingCar.getStatusID() : 1;
                
                boolean updated = carDAO.updateCar(carID, carName, licensePlate, carTypeID, brandID, locationID, statusID,
                        color, productionYear, transmission, fuelType, pricePerDay, imageURL, description);
                
                // Handle car images
                handleCarImageUpload(request, carID);
                
                if (updated || true) {
                    response.sendRedirect(request.getContextPath() + "/admin/cars?success=update");
                } else {
                    request.setAttribute("error", "Không thể cập nhật xe. Vui lòng kiểm tra lại thông tin.");
                    request.setAttribute("carTypes", lookupDAO.getAllCarTypes());
                    request.setAttribute("brands", lookupDAO.getAllBrands());
                    request.setAttribute("locations", lookupDAO.getAllLocations());
                    request.getRequestDispatcher("/views/admin/car-form.jsp").forward(request, response);
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Lỗi: " + e.getMessage());
                request.setAttribute("carTypes", lookupDAO.getAllCarTypes());
                request.setAttribute("brands", lookupDAO.getAllBrands());
                request.setAttribute("locations", lookupDAO.getAllLocations());
                try {
                    request.getRequestDispatcher("/views/admin/car-form.jsp").forward(request, response);
                } catch (Exception ex) {
                    response.sendRedirect(request.getContextPath() + "/admin/cars?error=1");
                }
                return;
            }
        }
        
        doGet(request, response);
    }
    
    private void handleGetCarBookings(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            int carID = Integer.parseInt(request.getParameter("id"));
            
            // Get all bookings for this car
            List<StaffBookingItem> carBookings = carDAO.getBookingsByCarId(carID);
            
            // Filter: ongoing trip vs trips to cancel
            StaffBookingItem ongoingTrip = null;
            List<Map<String, Object>> cancelledTripsList = new java.util.ArrayList<>();
            
            for (StaffBookingItem booking : carBookings) {
                String status = booking.getStatusName();
                
                // Check if this is an ongoing trip (InUse, In Progress, Đang chạy)
                if (status != null && (status.equalsIgnoreCase("InUse") || 
                        status.equalsIgnoreCase("In Progress") || 
                        status.equalsIgnoreCase("Đang chạy"))) {
                    ongoingTrip = booking;
                } else if (status != null && (status.equalsIgnoreCase("Booked") || 
                        status.equalsIgnoreCase("Confirmed") || 
                        status.equalsIgnoreCase("Pending"))) {
                    // Add to cancelled list
                    Map<String, Object> trip = new java.util.HashMap<>();
                    trip.put("bookingID", booking.getBookingID());
                    trip.put("contractCode", booking.getContractCode());
                    trip.put("customerName", booking.getCustomerName());
                    trip.put("startDateTime", booking.getStartDateTime() != null ? booking.getStartDateTime().toString() : "");
                    trip.put("endDateTime", booking.getEndDateTime() != null ? booking.getEndDateTime().toString() : "");
                    trip.put("statusName", status);
                    cancelledTripsList.add(trip);
                }
            }
            
            // Build JSON response
            StringBuilder json = new StringBuilder();
            json.append("{\"success\":true");
            
            if (ongoingTrip != null) {
                json.append(",\"ongoingTrip\":{");
                json.append("\"bookingID\":").append(ongoingTrip.getBookingID()).append(",");
                json.append("\"contractCode\":\"").append(ongoingTrip.getContractCode() != null ? ongoingTrip.getContractCode() : "").append("\"");
                json.append("}");
            } else {
                json.append(",\"ongoingTrip\":null");
            }
            
            json.append(",\"cancelledTrips\":[");
            for (int i = 0; i < cancelledTripsList.size(); i++) {
                Map<String, Object> trip = cancelledTripsList.get(i);
                if (i > 0) json.append(",");
                json.append("{");
                json.append("\"bookingID\":").append(trip.get("bookingID")).append(",");
                json.append("\"contractCode\":\"").append(trip.get("contractCode")).append("\",");
                json.append("\"customerName\":\"").append(trip.get("customerName") != null ? trip.get("customerName") : "").append("\",");
                json.append("\"startDateTime\":\"").append(trip.get("startDateTime")).append("\",");
                json.append("\"endDateTime\":\"").append(trip.get("endDateTime")).append("\",");
                json.append("\"statusName\":\"").append(trip.get("statusName")).append("\"");
                json.append("}");
            }
            json.append("]");
            
            json.append("}");
            out.print(json.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    // Brand CRUD handlers
    private void handleAddBrand(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String brandName = request.getParameter("brandName");
            if (brandName == null || brandName.trim().isEmpty()) {
                out.print("{\"success\":false,\"error\":\"Tên hãng xe không được để trống\"}");
                return;
            }
            
            boolean success = brandDAO.addBrand(brandName.trim());
            if (success) {
                out.print("{\"success\":true,\"message\":\"Thêm hãng xe thành công\"}");
            } else {
                out.print("{\"success\":false,\"error\":\"Thêm hãng xe thất bại\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    private void handleEditBrand(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            int brandID = Integer.parseInt(request.getParameter("brandID"));
            String brandName = request.getParameter("brandName");
            
            if (brandName == null || brandName.trim().isEmpty()) {
                out.print("{\"success\":false,\"error\":\"Tên hãng xe không được để trống\"}");
                return;
            }
            
            boolean success = brandDAO.updateBrand(brandID, brandName.trim());
            if (success) {
                out.print("{\"success\":true,\"message\":\"Cập nhật hãng xe thành công\"}");
            } else {
                out.print("{\"success\":false,\"error\":\"Cập nhật hãng xe thất bại\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    private void handleDeleteBrand(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            int brandID = Integer.parseInt(request.getParameter("brandID"));
            boolean success = brandDAO.deleteBrand(brandID);
            if (success) {
                out.print("{\"success\":true,\"message\":\"Xóa hãng xe thành công\"}");
            } else {
                out.print("{\"success\":false,\"error\":\"Xóa hãng xe thất bại\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    // CarType CRUD handlers
    private void handleAddCarType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String typeName = request.getParameter("typeName");
            String seatCountStr = request.getParameter("seatCount");
            String description = request.getParameter("description");
            
            if (typeName == null || typeName.trim().isEmpty()) {
                out.print("{\"success\":false,\"error\":\"Tên loại xe không được để trống\"}");
                return;
            }
            
            int seatCount = 5;
            if (seatCountStr != null && !seatCountStr.isEmpty()) {
                seatCount = Integer.parseInt(seatCountStr);
            }
            
            boolean success = carTypeDAO.addCarType(typeName.trim(), null, seatCount, description);
            if (success) {
                out.print("{\"success\":true,\"message\":\"Thêm loại xe thành công\"}");
            } else {
                out.print("{\"success\":false,\"error\":\"Thêm loại xe thất bại\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    private void handleEditCarType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            int typeID = Integer.parseInt(request.getParameter("typeID"));
            String typeName = request.getParameter("typeName");
            String seatCountStr = request.getParameter("seatCount");
            String description = request.getParameter("description");
            
            if (typeName == null || typeName.trim().isEmpty()) {
                out.print("{\"success\":false,\"error\":\"Tên loại xe không được để trống\"}");
                return;
            }
            
            int seatCount = 5;
            if (seatCountStr != null && !seatCountStr.isEmpty()) {
                seatCount = Integer.parseInt(seatCountStr);
            }
            
            boolean success = carTypeDAO.updateCarType(typeID, typeName.trim(), null, seatCount, description);
            if (success) {
                out.print("{\"success\":true,\"message\":\"Cập nhật loại xe thành công\"}");
            } else {
                out.print("{\"success\":false,\"error\":\"Cập nhật loại xe thất bại\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    private void handleDeleteCarType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            int typeID = Integer.parseInt(request.getParameter("typeID"));
            boolean success = carTypeDAO.deleteCarType(typeID);
            if (success) {
                out.print("{\"success\":true,\"message\":\"Xóa loại xe thành công\"}");
            } else {
                out.print("{\"success\":false,\"error\":\"Xóa loại xe thất bại\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    private void handleCarImageUpload(HttpServletRequest request, int carID) throws IOException, ServletException {
        // Handle delete existing images
        String deleteImages = request.getParameter("deleteImages");
        if (deleteImages != null && !deleteImages.isEmpty()) {
            String[] imageIdsToDelete = deleteImages.split(",");
            for (String imageIdStr : imageIdsToDelete) {
                try {
                    int imageId = Integer.parseInt(imageIdStr.trim());
                    carImageDAO.deleteImage(imageId);
                } catch (NumberFormatException e) {
                    // Skip invalid IDs
                }
            }
        }
        
        // Handle new image uploads
        for (Part part : request.getParts()) {
            if (part.getName().startsWith("carImage") && part.getSize() > 0) {
                String imageType = request.getParameter("imageType_" + part.getName());
                String sortOrderStr = request.getParameter("sortOrder_" + part.getName());
                
                if (imageType == null || imageType.isEmpty()) {
                    imageType = "Gallery";
                }
                
                Integer sortOrder = null;
                if (sortOrderStr != null && !sortOrderStr.isEmpty()) {
                    try {
                        sortOrder = Integer.parseInt(sortOrderStr);
                    } catch (NumberFormatException e) {
                        sortOrder = null;
                    }
                }
                
                // Save the file
                String imageUrl = saveCarImage(request, part, carID);
                if (imageUrl != null) {
                    CarImage carImage = new CarImage();
                    carImage.setCarID(carID);
                    carImage.setImageUrl(imageUrl);
                    carImage.setImageType(imageType);
                    carImage.setSortOrder(sortOrder);
                    carImageDAO.addImage(carImage);
                }
            }
        }
        
        // Handle URL-based images
        String[] imageUrls = request.getParameterValues("imageUrls[]");
        if (imageUrls != null) {
            for (int i = 0; i < imageUrls.length; i++) {
                String url = imageUrls[i];
                if (url != null && !url.trim().isEmpty()) {
                    String imageType = request.getParameter("imageType_" + i);
                    String sortOrderStr = request.getParameter("sortOrder_" + i);
                    
                    if (imageType == null || imageType.isEmpty()) {
                        imageType = "Gallery";
                    }
                    
                    Integer sortOrder = null;
                    if (sortOrderStr != null && !sortOrderStr.isEmpty()) {
                        try {
                            sortOrder = Integer.parseInt(sortOrderStr);
                        } catch (NumberFormatException e) {
                            sortOrder = null;
                        }
                    }
                    
                    CarImage carImage = new CarImage();
                    carImage.setCarID(carID);
                    carImage.setImageUrl(url.trim());
                    carImage.setImageType(imageType);
                    carImage.setSortOrder(sortOrder != null ? sortOrder : i);
                    carImageDAO.addImage(carImage);
                }
            }
        }
    }
    
    private String saveCarImage(HttpServletRequest request, Part part, int carID) throws IOException {
        String original = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        if (original == null || original.isEmpty()) {
            return null;
        }
        
        String ext = "";
        int dotIndex = original.lastIndexOf('.');
        if (dotIndex > 0) {
            ext = original.substring(dotIndex);
        }
        
        String fileName = "car" + carID + "_" + System.currentTimeMillis() + ext;
        
        String realUploadDir = request.getServletContext().getRealPath(UPLOAD_DIR);
        File dir = new File(realUploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String absolutePath = dir.getAbsolutePath() + File.separator + fileName;
        part.write(absolutePath);
        
        return UPLOAD_DIR + "/" + fileName;
    }
}

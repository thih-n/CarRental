package com.carrental.controller.admin;

import com.carrental.constant.IConstant;
import com.carrental.dao.BookingDAO;
import com.carrental.dao.DriverProfileDAO;
import com.carrental.dao.UserDAO;
import com.carrental.entity.StaffBookingItem;
import com.carrental.entity.DriverProfile;
import com.carrental.entity.User;
import com.carrental.entity.Email;
import com.carrental.util.PasswordUtil;
import com.carrental.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet(name = "AdminUserManagementServlet", urlPatterns = {"/admin/users"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 1024 * 1024 * 5,
    maxRequestSize = 1024 * 1024 * 10
)
public class AdminUserManagementServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final DriverProfileDAO driverProfileDAO = new DriverProfileDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private static final String UPLOAD_DIR = "assets";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        String type = request.getParameter("type");
        String typeParam = (type != null) ? type : "customer";
        
        // Handle AJAX request for driver bookings
        if ("getDriverBookings".equals(action)) {
            handleGetDriverBookings(request, response);
            return;
        }
        
        // Handle AJAX request for customer bookings
        if ("getCustomerBookings".equals(action)) {
            handleGetCustomerBookings(request, response);
            return;
        }
        
        if ("edit".equals(action)) {
            int userID = Integer.parseInt(request.getParameter("id"));
            User user = userDAO.getUserById(userID);
            request.setAttribute("user", user);
            
            // Get driver profile if user is driver
            if (user.getRoleID() == IConstant.ROLE_ID_DRIVER) {
                DriverProfile driverProfile = driverProfileDAO.getByUserId(userID);
                if (driverProfile != null) {
                    request.setAttribute("driverProfile", driverProfile);
                }
            }
            
            request.getRequestDispatcher("/views/admin/edit-user.jsp").forward(request, response);
            return;
        }
        
        if ("create".equals(action)) {
            request.getRequestDispatcher("/views/admin/edit-user.jsp").forward(request, response);
            return;
        }
        
        if ("lock".equals(action) || "unlock".equals(action)) {
            int userID = Integer.parseInt(request.getParameter("id"));
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("USER_SESSION");
            
            // Prevent self-lock
            if (currentUser != null && currentUser.getUserID() == userID) {
                response.sendRedirect(request.getContextPath() + "/admin/users?type=" + typeParam + "&error=self_lock");
                return;
            }
            
            boolean success;
            int cancelledBookings = 0;
            Email emailService = new Email();
            User user = userDAO.getUserById(userID);
            
            if ("lock".equals(action)) {
                // Lock driver and cancel their bookings (except ongoing)
                if ("driver".equals(typeParam)) {
                    cancelledBookings = lockDriverAndCancelBookings(userID, currentUser.getUserID());
                }
                // Lock customer and cancel their bookings (except ongoing)
                if ("customer".equals(typeParam)) {
                    cancelledBookings = lockCustomerAndCancelBookings(userID, currentUser.getUserID());
                }
                success = userDAO.deactivateUser(userID);
                
                // Send email notification for account lock
                if (success && user != null) {
                    String reason = "Tài khoản bị khóa bởi quản trị viên";
                    emailService.sendAccountLockedEmail(user.getEmail(), user.getFullName(), reason);
                }
            } else {
                // Unlock driver - restore driver status to Available
                if ("driver".equals(typeParam)) {
                    driverProfileDAO.updateDriverStatusByUserID(userID, "Available");
                }
                success = userDAO.activateUser(userID);
            }
            
            if ("driver".equals(typeParam) && cancelledBookings > 0) {
                response.sendRedirect(request.getContextPath() + "/admin/users?type=" + typeParam + "&locked=1&cancelledBookings=" + cancelledBookings);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/users?type=" + typeParam + (success ? "&success=1" : "&error=1"));
            }
            return;
        }
        
        loadUsersList(request, typeParam);
        request.getRequestDispatcher("/views/admin/user-management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        String type = request.getParameter("type");
        String typeParam = (type != null) ? type : "customer";
        
        if ("create".equals(action)) {
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phoneNumber = request.getParameter("phoneNumber");
            String address = request.getParameter("address");
            String password = request.getParameter("password");
            
            // Store values for re-populating form
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phoneNumber", phoneNumber);
            request.setAttribute("address", address);
            
            // Validation errors map
            java.util.Map<String, String> errors = new java.util.HashMap<>();
            
            // FullName validation
            if (fullName == null || fullName.trim().isEmpty()) {
                errors.put("fullName", "Vui lòng nhập họ tên");
            } else if (fullName.trim().length() < 2) {
                errors.put("fullName", "Họ tên phải có ít nhất 2 ký tự");
            } else if (fullName.trim().length() > 100) {
                errors.put("fullName", "Họ tên không được quá 100 ký tự");
            }
            
            // Email validation
            if (email == null || email.trim().isEmpty()) {
                errors.put("email", "Vui lòng nhập email");
            } else if (!isValidEmail(email.trim())) {
                errors.put("email", "Email không hợp lệ");
            } else if (userDAO.isEmailExists(email.trim())) {
                errors.put("email", "Email đã tồn tại");
            }
            
            // Password validation
            if (password == null || password.isEmpty()) {
                errors.put("password", "Vui lòng nhập mật khẩu");
            } else if (password.length() < 6) {
                errors.put("password", "Mật khẩu phải có ít nhất 6 ký tự");
            } else if (password.length() > 50) {
                errors.put("password", "Mật khẩu không được quá 50 ký tự");
            } else if (!hasValidPassword(password)) {
                errors.put("password", "Mật khẩu phải chứa ít nhất 1 chữ cái và 1 số");
            }
            
            // Phone validation (optional - no validation)
            // Phone is stored but not validated
            
            // If there are errors, return to form
            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.getRequestDispatcher("/views/admin/edit-user.jsp").forward(request, response);
                return;
            }
            
            int roleID;
            switch (typeParam) {
                case "staff":
                    roleID = IConstant.ROLE_ID_STAFF;
                    break;
                case "driver":
                    roleID = IConstant.ROLE_ID_DRIVER;
                    break;
                case "admin":
                    roleID = IConstant.ROLE_ID_ADMIN;
                    break;
                default:
                    roleID = IConstant.ROLE_ID_CUSTOMER;
            }
            
            String hashedPassword = PasswordUtil.hashPassword(password);
            boolean created = userDAO.registerUser(fullName.trim(), email.trim(), hashedPassword, 
                    phoneNumber != null ? phoneNumber.trim() : null, 
                    address != null ? address.trim() : null, roleID);
            
            // Handle KYC fields for driver
            if (created && typeParam.equals("driver")) {
                int newUserId = userDAO.getUserIdByEmail(email.trim());
                if (newUserId > 0) {
                    String identityCardNumber = request.getParameter("identityCardNumber");
                    String licenseFront = request.getParameter("licenseImageFront");
                    String licenseBack = request.getParameter("licenseImageBack");
                    
                    // Use URL directly
                    if (licenseFront != null && !licenseFront.trim().isEmpty()) {
                        licenseFront = licenseFront.trim();
                    } else {
                        licenseFront = null;
                    }
                    
                    if (licenseBack != null && !licenseBack.trim().isEmpty()) {
                        licenseBack = licenseBack.trim();
                    } else {
                        licenseBack = null;
                    }
                    
                    // Update KYC info
                    userDAO.updateProfileKyc(newUserId, identityCardNumber, licenseFront, licenseBack);
                }
            }
            
            response.sendRedirect(request.getContextPath() + "/admin/users?type=" + typeParam + "&created=" + created);
            return;
        }
        
        if ("update".equals(action)) {
            int userID = Integer.parseInt(request.getParameter("userID"));
            String fullName = request.getParameter("fullName");
            String phoneNumber = request.getParameter("phoneNumber");
            String address = request.getParameter("address");
            String password = request.getParameter("password");
            String identityCardNumber = request.getParameter("identityCardNumber");
            String existingLicenseFront = request.getParameter("existingLicenseFront");
            String existingLicenseBack = request.getParameter("existingLicenseBack");
            
            // Store values for re-populating form
            request.setAttribute("fullName", fullName);
            request.setAttribute("phoneNumber", phoneNumber);
            request.setAttribute("address", address);
            
            // Validation errors map
            java.util.Map<String, String> errors = new java.util.HashMap<>();
            
            // FullName validation
            if (fullName == null || fullName.trim().isEmpty()) {
                errors.put("fullName", "Vui lòng nhập họ tên");
            } else if (fullName.trim().length() < 2) {
                errors.put("fullName", "Họ tên phải có ít nhất 2 ký tự");
            } else if (fullName.trim().length() > 100) {
                errors.put("fullName", "Họ tên không được quá 100 ký tự");
            }
            
            // Phone validation (optional - no validation)
            // Phone is stored but not validated
            
            // Password validation (optional for update)
            if (password != null && !password.isEmpty()) {
                if (password.length() < 6) {
                    errors.put("password", "Mật khẩu phải có ít nhất 6 ký tự");
                } else if (password.length() > 50) {
                    errors.put("password", "Mật khẩu không được quá 50 ký tự");
                } else if (!hasValidPassword(password)) {
                    errors.put("password", "Mật khẩu phải chứa ít nhất 1 chữ cái và 1 số");
                }
            }
            
            // If there are errors, reload user and driver profile then forward
            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                User user = userDAO.getUserById(userID);
                request.setAttribute("user", user);
                if ("driver".equals(typeParam)) {
                    DriverProfile dp = driverProfileDAO.getByUserId(userID);
                    request.setAttribute("driverProfile", dp);
                }
                request.getRequestDispatcher("/views/admin/edit-user.jsp").forward(request, response);
                return;
            }
            
            // Basic profile update
            boolean updated = userDAO.updateUserProfile(userID, fullName.trim(), 
                    phoneNumber != null ? phoneNumber.trim() : null, 
                    address != null ? address.trim() : null);
            
            // Password update if provided
            if (password != null && !password.isEmpty()) {
                String hashedPassword = PasswordUtil.hashPassword(password);
                userDAO.updatePassword(userID, hashedPassword);
            }
            
            // Handle KYC fields for customer and driver (use URL)
            if (typeParam.equals("customer") || typeParam.equals("driver")) {
                String licenseFront = existingLicenseFront;
                String licenseBack = existingLicenseBack;
                
                // Get URL from form input
                String newLicenseFront = request.getParameter("licenseImageFront");
                String newLicenseBack = request.getParameter("licenseImageBack");
                
                // Use URL if provided
                if (newLicenseFront != null && !newLicenseFront.trim().isEmpty()) {
                    licenseFront = newLicenseFront.trim();
                }
                
                if (newLicenseBack != null && !newLicenseBack.trim().isEmpty()) {
                    licenseBack = newLicenseBack.trim();
                }
                
                // Update KYC info
                userDAO.updateProfileKyc(userID, identityCardNumber, licenseFront, licenseBack);
                
                // Handle driver-specific fields
                if (typeParam.equals("driver")) {
                    String licenseNumber = request.getParameter("licenseNumber");
                    String licenseExpiryStr = request.getParameter("licenseExpiry");
                    String experienceYearsStr = request.getParameter("experienceYears");
                    
                    Date licenseExpiry = null;
                    if (licenseExpiryStr != null && !licenseExpiryStr.isEmpty()) {
                        licenseExpiry = Date.valueOf(licenseExpiryStr);
                    }
                    
                    Integer experienceYears = null;
                    if (experienceYearsStr != null && !experienceYearsStr.isEmpty()) {
                        experienceYears = Integer.parseInt(experienceYearsStr);
                    }
                    
                    DriverProfile dp = driverProfileDAO.getByUserId(userID);
                    if (dp == null) {
                        dp = new DriverProfile();
                        dp.setUserID(userID);
                    }
                    dp.setLicenseNumber(licenseNumber);
                    dp.setLicenseExpiry(licenseExpiry);
                    dp.setExperienceYears(experienceYears);
                    
                    if (dp.getDriverID() > 0) {
                        driverProfileDAO.updateDriver(dp);
                    } else {
                        driverProfileDAO.createDriverProfile(dp);
                    }
                }
            }
            
            response.sendRedirect(request.getContextPath() + "/admin/users?type=" + typeParam + "&success=1");
            return;
        }
        
        // Handle lock driver with POST (from modal)
        if ("lock".equals(action) && "driver".equals(typeParam)) {
            int userID = Integer.parseInt(request.getParameter("userID"));
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("USER_SESSION");
            
            if (currentUser != null && currentUser.getUserID() == userID) {
                response.sendRedirect(request.getContextPath() + "/admin/users?type=driver&error=self_lock");
                return;
            }
            
            int cancelledBookings = lockDriverAndCancelBookings(userID, currentUser.getUserID());
            userDAO.deactivateUser(userID);
            
            // Send email notification for account lock
            User user = userDAO.getUserById(userID);
            if (user != null) {
                Email emailService = new Email();
                emailService.sendAccountLockedEmail(user.getEmail(), user.getFullName(), "Tài khoản tài xế bị khóa bởi quản trị viên");
            }
            
            response.sendRedirect(request.getContextPath() + "/admin/users?type=driver&locked=1&cancelledBookings=" + cancelledBookings);
            return;
        }
        
        // Handle lock customer with POST (from modal)
        if ("lock".equals(action) && "customer".equals(typeParam)) {
            int userID = Integer.parseInt(request.getParameter("userID"));
            String lockReason = request.getParameter("reason");
            
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("USER_SESSION");
            int cancelledBy = currentUser != null ? currentUser.getUserID() : 0;
            
            int cancelledBookings = lockCustomerAndCancelBookings(userID, cancelledBy);
            userDAO.deactivateUser(userID);
            
            // Send email notification for account lock
            User user = userDAO.getUserById(userID);
            if (user != null) {
                Email emailService = new Email();
                String reason = (lockReason != null && !lockReason.isEmpty()) ? lockReason : "Tài khoản khách hàng bị khóa bởi quản trị viên";
                emailService.sendAccountLockedEmail(user.getEmail(), user.getFullName(), reason);
            }
            
            response.sendRedirect(request.getContextPath() + "/admin/users?type=customer&locked=1&cancelledBookings=" + cancelledBookings);
            return;
        }
        
        doGet(request, response);
    }
    
    private void handleGetDriverBookings(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            int userID = Integer.parseInt(request.getParameter("userID"));
            
            // Get all bookings for this driver that are not completed/cancelled
            List<StaffBookingItem> driverBookings = bookingDAO.getBookingsByDriverId(userID);
            
            // Filter bookings to find:
            // 1. Ongoing trip (status = 'In Progress' or 'Running')
            // 2. Trips to cancel (Pending, Confirmed, etc. - not ongoing)
            
            StaffBookingItem ongoingTrip = null;
            List<StaffBookingItem> cancelledTripsList = driverBookings.stream()
                    .filter(b -> {
                        String status = b.getStatusName();
                        return status != null && (status.equalsIgnoreCase("InUse") || 
                                 status.equalsIgnoreCase("In Progress") || 
                                 status.equalsIgnoreCase("Đang chạy"));
                    })
                    .collect(Collectors.toList());
            
            if (!cancelledTripsList.isEmpty()) {
                ongoingTrip = cancelledTripsList.get(0);
            }
            
            final StaffBookingItem ongoing = ongoingTrip;
            List<StaffBookingItem> tripsToCancel = driverBookings.stream()
                    .filter(b -> {
                        String status = b.getStatusName();
                        return status != null && 
                               !status.equalsIgnoreCase("InUse") && 
                               !status.equalsIgnoreCase("In Progress") &&
                               !status.equalsIgnoreCase("Đang chạy") &&
                               !status.equalsIgnoreCase("Completed") &&
                               !status.equalsIgnoreCase("Hoàn thành") &&
                               !status.equalsIgnoreCase("Cancelled") &&
                               !status.equalsIgnoreCase("Đã hủy");
                    })
                    .collect(Collectors.toList());
            
            // Build JSON response
            StringBuilder json = new StringBuilder();
            json.append("{");
            
            // Ongoing trip
            if (ongoing != null) {
                json.append("\"ongoingTrip\":{");
                json.append("\"bookingID\":").append(ongoing.getBookingID()).append(",");
                json.append("\"statusName\":\"").append(ongoing.getStatusName() != null ? ongoing.getStatusName() : "").append("\"");
                json.append("},");
            } else {
                json.append("\"ongoingTrip\":null,");
            }
            
            // Cancelled trips
            json.append("\"cancelledTrips\":[");
            for (int i = 0; i < tripsToCancel.size(); i++) {
                StaffBookingItem b = tripsToCancel.get(i);
                if (i > 0) json.append(",");
                json.append("{");
                json.append("\"bookingID\":").append(b.getBookingID()).append(",");
                json.append("\"customerName\":\"").append(b.getCustomerName() != null ? b.getCustomerName().replace("\"", "\\\"") : "").append("\",");
                json.append("\"pickupDate\":\"").append(b.getStartDateTime() != null ? b.getStartDateTime().toString() : "").append("\",");
                json.append("\"returnDate\":\"").append(b.getEndDateTime() != null ? b.getEndDateTime().toString() : "").append("\",");
                json.append("\"statusName\":\"").append(b.getStatusName() != null ? b.getStatusName().replace("\"", "\\\"") : "").append("\"");
                json.append("}");
            }
            json.append("]");
            
            json.append("}");
            out.print(json.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\":\"Failed to load bookings\"}");
        }
    }
    
    private void handleGetCustomerBookings(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            int userID = Integer.parseInt(request.getParameter("userID"));
            
            // Get all bookings for this customer
            List<StaffBookingItem> customerBookings = bookingDAO.getBookingsByCustomerId(userID);
            
            // Filter: ongoing trip vs trips to cancel
            StaffBookingItem ongoingTrip = null;
            List<Map<String, Object>> cancelledTripsList = new java.util.ArrayList<>();
            
            for (StaffBookingItem booking : customerBookings) {
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
    
    private int lockDriverAndCancelBookings(int driverID, int cancelledBy) {
        int cancelledCount = 0;
        Email emailService = new Email();
        String cancelReason = "Tài xế không hợp lệ";
        
        // Get all bookings for this driver
        List<StaffBookingItem> driverBookings = bookingDAO.getBookingsByDriverId(driverID);
        
        // Cancel bookings that are not completed, cancelled, or ongoing (InUse)
        for (StaffBookingItem booking : driverBookings) {
            String status = booking.getStatusName();
            if (status != null && 
                !status.equalsIgnoreCase("InUse") && 
                !status.equalsIgnoreCase("In Progress") &&
                !status.equalsIgnoreCase("Đang chạy") &&
                !status.equalsIgnoreCase("Completed") &&
                !status.equalsIgnoreCase("Hoàn thành") &&
                !status.equalsIgnoreCase("Cancelled") &&
                !status.equalsIgnoreCase("Đã hủy")) {
                
                // Cancel this booking
                if (bookingDAO.cancelBooking(booking.getBookingID(), cancelReason, cancelledBy)) {
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
                }
            }
        }
        
        // Update driver status in DriverProfiles
        driverProfileDAO.updateDriverStatus(driverID, "Unavailable");
        
        return cancelledCount;
    }
    
    private int lockCustomerAndCancelBookings(int customerID, int cancelledBy) {
        int cancelledCount = 0;
        Email emailService = new Email();
        String cancelReason = "Tài khoản khách hàng bị khóa";
        
        // Get all bookings for this customer
        List<StaffBookingItem> customerBookings = bookingDAO.getBookingsByCustomerId(customerID);
        
        // Cancel bookings that are not completed, cancelled, or ongoing (InUse)
        for (StaffBookingItem booking : customerBookings) {
            String status = booking.getStatusName();
            if (status != null && 
                !status.equalsIgnoreCase("InUse") && 
                !status.equalsIgnoreCase("In Progress") &&
                !status.equalsIgnoreCase("Đang chạy") &&
                !status.equalsIgnoreCase("Completed") &&
                !status.equalsIgnoreCase("Hoàn thành") &&
                !status.equalsIgnoreCase("Cancelled") &&
                !status.equalsIgnoreCase("Đã hủy")) {
                
                // Cancel this booking
                if (bookingDAO.cancelBooking(booking.getBookingID(), cancelReason, cancelledBy)) {
                    cancelledCount++;
                    
                    // Send email to customer about cancellation
                    if (booking.getCustomerEmail() != null && !booking.getCustomerEmail().isEmpty()) {
                        emailService.sendTripCancelledEmail(
                            booking.getCustomerEmail(),
                            booking.getCustomerName(),
                            String.valueOf(booking.getContractID()),
                            cancelReason
                        );
                    }
                }
            }
        }
        
        return cancelledCount;
    }
    
    private String saveFile(HttpServletRequest request, Part part, String side, int userID) throws IOException {
        String original = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        String ext = ValidationUtil.safeFileExtension(original);
        String fileName = "u" + userID + "_" + side + "_" + UUID.randomUUID().toString().replace("-", "") + ext;

        String realUploadDir = request.getServletContext().getRealPath(UPLOAD_DIR);
        File dir = new File(realUploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String absolutePath = dir.getAbsolutePath() + File.separator + fileName;
        part.write(absolutePath);

        return UPLOAD_DIR + "/" + fileName;
    }
    
    private void loadUsersList(HttpServletRequest request, String type) {
        List<User> allUsers = userDAO.getAllUsers();
        
        // Apply search filter
        String search = request.getParameter("search");
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase().trim();
            allUsers = allUsers.stream()
                    .filter(u -> (u.getFullName() != null && u.getFullName().toLowerCase().contains(searchLower)) ||
                                 (u.getEmail() != null && u.getEmail().toLowerCase().contains(searchLower)) ||
                                 (u.getPhoneNumber() != null && u.getPhoneNumber().contains(searchLower)))
                    .collect(Collectors.toList());
        }
        
        // Apply status filter
        String status = request.getParameter("status");
        if (status != null && !status.isEmpty()) {
            boolean isActive = "active".equals(status);
            allUsers = allUsers.stream()
                    .filter(u -> u.isActive() == isActive)
                    .collect(Collectors.toList());
        }
        
        // Filter by type/role
        List<User> users;
        if (type == null || "customer".equals(type)) {
            users = allUsers.stream()
                    .filter(u -> u.getRoleID() == IConstant.ROLE_ID_CUSTOMER)
                    .collect(Collectors.toList());
        } else if ("staff".equals(type)) {
            users = allUsers.stream()
                    .filter(u -> u.getRoleID() == IConstant.ROLE_ID_STAFF)
                    .collect(Collectors.toList());
        } else if ("driver".equals(type)) {
            users = allUsers.stream()
                    .filter(u -> u.getRoleID() == IConstant.ROLE_ID_DRIVER)
                    .collect(Collectors.toList());
        } else if ("admin".equals(type)) {
            users = allUsers.stream()
                    .filter(u -> u.getRoleID() == IConstant.ROLE_ID_ADMIN)
                    .collect(Collectors.toList());
        } else {
            users = allUsers;
        }
        
        request.setAttribute("users", users);
    }
    
    // Validation helper methods
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    private boolean isValidPhone(String phone) {
        String phoneRegex = "^0\\d{9,10}$";
        return phone.matches(phoneRegex);
    }
    
    private boolean hasValidPassword(String password) {
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasLetter && hasDigit;
    }
}

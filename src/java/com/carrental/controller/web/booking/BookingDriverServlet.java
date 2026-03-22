package com.carrental.controller.web.booking;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.BookingLookupDAO;
import com.carrental.dao.CarDAO;
import com.carrental.dao.CarHoldingDAO;
import com.carrental.dao.DriverLeaveDAO;
import com.carrental.dao.DriverProfileDAO;
import com.carrental.entity.Car;
import com.carrental.entity.CarHolding;
import com.carrental.entity.DriverProfile;
import com.carrental.entity.Location;
import com.carrental.entity.User;
import com.carrental.util.ValidationUtil;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BookingDriverServlet extends BaseAuthentication {

    @Override
    protected boolean shouldCheckPermission() {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer carID = ValidationUtil.parseIntegerOrNull(request.getParameter("carID"));
        Integer pickUpLocationID = ValidationUtil.parseIntegerOrNull(request.getParameter("pickUpLocationID"));
        String startDateTime = request.getParameter("startDateTime");
        String endDateTime = request.getParameter("endDateTime");

        // Convert datetime format from HTML datetime-local (YYYY-MM-DDTHH:MM) to SQL Server format (YYYY-MM-DD HH:MM:SS)
        if (startDateTime != null && startDateTime.contains("T")) {
            startDateTime = startDateTime.replace("T", " ") + ":00";
        }
        if (endDateTime != null && endDateTime.contains("T")) {
            endDateTime = endDateTime.replace("T", " ") + ":00";
        }

        // Search/filter params
        String keyword = request.getParameter("keyword");
        String minRating = request.getParameter("minRating");

        if (carID == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu carID.");
            return;
        }
        if (pickUpLocationID == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu pickUpLocationID.");
            return;
        }
        if (ValidationUtil.isBlank(startDateTime) || ValidationUtil.isBlank(endDateTime)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu thời gian nhận/trả xe.");
            return;
        }

        // Validate car exists and is available
        CarDAO carDAO = new CarDAO();
        Car car = carDAO.getById(carID);
        if (car == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy xe.");
            return;
        }
        if (car.getStatusID() != 1) {
            request.setAttribute("error", "Xe này hiện không khả dụng để thuê.");
            request.getRequestDispatcher("/views/booking/search.jsp").forward(request, response);
            return;
        }

        // Get car info for display
        request.setAttribute("car", car);

        // Get location info
        BookingLookupDAO lookupDAO = new BookingLookupDAO();
        Location location = null;
        for (Location loc : lookupDAO.getAllLocations()) {
            if (loc.getLocationID() == pickUpLocationID) {
                location = loc;
                break;
            }
        }
        request.setAttribute("location", location);

        // Get and filter drivers
        DriverProfileDAO driverDAO = new DriverProfileDAO();
        List<DriverProfile> drivers;

        // Parse datetime for availability check
        Timestamp checkStart = ValidationUtil.parseTimestampOrNull(startDateTime);
        Timestamp checkEnd = ValidationUtil.parseTimestampOrNull(endDateTime);

        // Use DriverLeaveDAO to get truly available drivers for the time range
        DriverLeaveDAO driverLeaveDAO = new DriverLeaveDAO();
        if (checkStart != null && checkEnd != null) {
            drivers = driverLeaveDAO.getAvailableDriversForBooking(pickUpLocationID, checkStart, checkEnd);
            // Apply keyword and rating filters
            if (keyword != null && !keyword.trim().isEmpty()) {
                drivers.removeIf(d -> d.getFullName() == null || !d.getFullName().toLowerCase().contains(keyword.toLowerCase()));
            }
            if (minRating != null && !minRating.isEmpty()) {
                try {
                    float rating = Float.parseFloat(minRating);
                    drivers.removeIf(d -> d.getRating() == null || d.getRating().floatValue() < rating);
                } catch (NumberFormatException e) {
                    // ignore invalid rating
                }
            }
        } else {
            // Fallback to old behavior if datetime parsing fails
            drivers = driverDAO.searchDrivers(pickUpLocationID, keyword, minRating);
        }

        request.setAttribute("drivers", drivers);
        request.setAttribute("carID", carID);
        request.setAttribute("pickUpLocationID", pickUpLocationID);
        request.setAttribute("startDateTime", startDateTime);
        request.setAttribute("endDateTime", endDateTime);
        request.setAttribute("rentPrice", request.getParameter("rentPrice"));
        
        // Keep filter params
        request.setAttribute("keyword", keyword);
        request.setAttribute("minRating", minRating);

        request.getRequestDispatcher("/views/booking/drivers.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Integer carID = ValidationUtil.parseIntegerOrNull(request.getParameter("carID"));
        Integer pickUpLocationID = ValidationUtil.parseIntegerOrNull(request.getParameter("pickUpLocationID"));
        Integer driverID = ValidationUtil.parseIntegerOrNull(request.getParameter("driverID"));
        String startDateTimeStr = request.getParameter("startDateTime");
        String endDateTimeStr = request.getParameter("endDateTime");

        if (carID == null || pickUpLocationID == null || ValidationUtil.isBlank(startDateTimeStr)
                || ValidationUtil.isBlank(endDateTimeStr)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false,\"message\":\"Thiếu dữ liệu.\"}");
            return;
        }

        // Parse datetime
        Timestamp startDateTime = ValidationUtil.parseTimestampOrNull(startDateTimeStr);
        Timestamp endDateTime = ValidationUtil.parseTimestampOrNull(endDateTimeStr);
        
        if (startDateTime == null || endDateTime == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false,\"message\":\"Thời gian không hợp lệ.\"}");
            return;
        }

        // Validate car again
        CarDAO carDAO = new CarDAO();
        Car car = carDAO.getById(carID);
        if (car == null || car.getStatusID() != 1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false,\"message\":\"Xe không còn khả dụng.\"}");
            return;
        }

        // Get user
        User user = (User) request.getSession().getAttribute(IConstant.SESSION_USER);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"Vui lòng đăng nhập.\"}");
            return;
        }

        // Check and create holding
        CarHoldingDAO holdingDAO = new CarHoldingDAO();
        
        // Delete expired holdings first
        holdingDAO.deleteExpiredHoldings();

        // Check if car is on hold by another user
        if (holdingDAO.isCarOnHoldByOther(carID, user.getUserID())) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write("{\"success\":false,\"message\":\"Xe này đang được giữ bởi người khác. Vui lòng chọn xe khác.\"}");
            return;
        }

        // Also check CarSchedules for booked status
        if (!holdingDAO.isCarAvailableForBooking(carID, startDateTime, endDateTime)) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write("{\"success\":false,\"message\":\"Xe này đã được đặt trong khoảng thời gian bạn chọn.\"}");
            return;
        }

        // Create 10-minute holding (from current time)
        Timestamp expiryTime = new Timestamp(System.currentTimeMillis() + 10 * 60 * 1000); // 10 minutes
        CarHolding holding = new CarHolding(carID, driverID, user.getUserID(), startDateTime, endDateTime, expiryTime);
        
        // Delete old holding if exists, then create new one
        holdingDAO.deleteHoldingByCar(carID);
        boolean holdingCreated = holdingDAO.createHolding(holding);

        if (!holdingCreated) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\":false,\"message\":\"Không thể giữ xe.\"}");
            return;
        }

        // Return success with holding info
        response.getWriter().write("{\"success\":true,\"holdingExpiry\":\"" + expiryTime.toString() + "\",\"remainingMinutes\":10}");
    }
}

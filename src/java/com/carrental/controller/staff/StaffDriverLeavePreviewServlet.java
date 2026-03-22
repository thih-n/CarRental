package com.carrental.controller.staff;

import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.BookingDAO;
import com.carrental.dao.DriverLeaveRequestDAO;
import com.carrental.entity.StaffBookingItem;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "StaffDriverLeavePreviewServlet", urlPatterns = {"/staff/driver/leave/preview"})
public class StaffDriverLeavePreviewServlet extends BaseAuthentication {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject result = new JSONObject();
        String leaveIdParam = request.getParameter("leaveID");
        if (leaveIdParam == null || leaveIdParam.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "Missing leaveID");
            response.getWriter().write(result.toString());
            return;
        }

        try {
            int leaveID = Integer.parseInt(leaveIdParam);
            DriverLeaveRequestDAO leaveDAO = new DriverLeaveRequestDAO();
            DriverLeaveRequestDAO.DriverLeaveRequest leave = leaveDAO.getLeaveById(leaveID);
            if (leave == null) {
                result.put("success", false);
                result.put("message", "Leave not found");
                response.getWriter().write(result.toString());
                return;
            }

            BookingDAO bookingDAO = new BookingDAO();
            List<StaffBookingItem> bookings = bookingDAO.getBookingsByDriverAndRange(
                    leave.getDriverID(),
                    leave.getLeaveStart(),
                    leave.getLeaveEnd()
            );

            JSONArray cancelledTrips = new JSONArray();
            JSONObject ongoingTrip = null;

            for (StaffBookingItem booking : bookings) {
                String status = booking.getDetailStatus();
                if (status != null && status.equalsIgnoreCase("InUse")) {
                    JSONObject ongoing = new JSONObject();
                    ongoing.put("bookingID", booking.getContractID());
                    ongoing.put("contractCode", booking.getContractCode());
                    ongoingTrip = ongoing;
                    continue;
                }

                if (status != null && (status.equalsIgnoreCase("Completed") || status.equalsIgnoreCase("Cancelled"))) {
                    continue;
                }

                JSONObject item = new JSONObject();
                item.put("bookingID", booking.getContractID());
                item.put("contractCode", booking.getContractCode());
                item.put("customerName", booking.getCustomerName());
                item.put("startDateTime", booking.getStartDateTime() != null ? booking.getStartDateTime().toString() : "");
                item.put("endDateTime", booking.getEndDateTime() != null ? booking.getEndDateTime().toString() : "");
                item.put("statusName", booking.getDetailStatus());
                cancelledTrips.put(item);
            }

            result.put("success", true);
            result.put("cancelledTrips", cancelledTrips);
            if (ongoingTrip != null) {
                result.put("ongoingTrip", ongoingTrip);
            }
            response.getWriter().write(result.toString());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Server error: " + e.getMessage());
            response.getWriter().write(result.toString());
        }
    }
}

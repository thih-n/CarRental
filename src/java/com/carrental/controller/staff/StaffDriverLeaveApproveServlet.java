package com.carrental.controller.staff;

import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.BookingDAO;
import com.carrental.dao.DriverLeaveRequestDAO;
import com.carrental.entity.Email;
import com.carrental.entity.StaffBookingItem;
import com.carrental.entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

public class StaffDriverLeaveApproveServlet extends BaseAuthentication {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int leaveID = Integer.parseInt(request.getParameter("leaveID"));
            
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("USER_SESSION");
            int staffID = user.getUserID();

            DriverLeaveRequestDAO dao = new DriverLeaveRequestDAO();
            DriverLeaveRequestDAO.DriverLeaveRequest leave = dao.getLeaveById(leaveID);
            boolean success = dao.approveLeave(leaveID, staffID);

            int cancelledCount = 0;
            if (success && leave != null) {
                BookingDAO bookingDAO = new BookingDAO();
                List<StaffBookingItem> bookings = bookingDAO.getBookingsByDriverAndRange(
                        leave.getDriverID(),
                        leave.getLeaveStart(),
                        leave.getLeaveEnd()
                );

                Email emailService = new Email();
                String cancelReason = "Tài xế hủy chuyến";

                for (StaffBookingItem booking : bookings) {
                    String status = booking.getDetailStatus();
                    if (status != null &&
                        !status.equalsIgnoreCase("InUse") &&
                        !status.equalsIgnoreCase("In Progress") &&
                        !status.equalsIgnoreCase("Đang chạy") &&
                        !status.equalsIgnoreCase("Completed") &&
                        !status.equalsIgnoreCase("Hoàn thành") &&
                        !status.equalsIgnoreCase("Cancelled") &&
                        !status.equalsIgnoreCase("Đã hủy")) {

                        if (bookingDAO.cancelBooking(booking.getBookingID(), cancelReason, staffID)) {
                            cancelledCount++;
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
            }

            if (success) {
                response.sendRedirect(request.getContextPath() + "/staff/driver/requests?approved=1&cancelledBookings=" + cancelledCount);
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/driver/requests?error=action_failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/staff/driver/requests?error=action_failed");
        }
    }
}

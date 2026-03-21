package com.carrental.controller.staff;

import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.BookingDAO;
import com.carrental.dao.DriverProfileDAO;
import com.carrental.dao.UserDAO;
import com.carrental.entity.Email;
import com.carrental.entity.StaffBookingItem;
import com.carrental.entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

public class StaffDriverToggleStatusServlet extends BaseAuthentication {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int driverID = Integer.parseInt(request.getParameter("driverID"));
            boolean lock = "true".equals(request.getParameter("lock"));

            DriverProfileDAO dao = new DriverProfileDAO();

            if (lock) {
                HttpSession session = request.getSession();
                User staffUser = (User) session.getAttribute("USER_SESSION");
                Integer cancelledBy = staffUser != null ? staffUser.getUserID() : null;

                BookingDAO bookingDAO = new BookingDAO();
                List<StaffBookingItem> driverBookings = bookingDAO.getBookingsByDriverId(driverID);
                Email emailService = new Email();
                String cancelReason = "Tài xế bị khóa";

                int cancelledCount = 0;
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

                        if (bookingDAO.cancelBooking(booking.getBookingID(), cancelReason, cancelledBy)) {
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

                boolean success = dao.toggleUserActiveStatus(driverID, false);
                if (success) {
                    UserDAO userDAO = new UserDAO();
                    User user = userDAO.getUserById(driverID);
                    if (user != null) {
                        emailService.sendAccountLockedEmail(user.getEmail(), user.getFullName(), cancelReason);
                    }
                    response.sendRedirect(request.getContextPath() + "/staff/drivers?statusChanged=1&cancelledBookings=" + cancelledCount);
                } else {
                    response.sendRedirect(request.getContextPath() + "/staff/drivers?error=toggle_failed");
                }
            } else {
                boolean success = dao.toggleUserActiveStatus(driverID, true);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/staff/drivers?statusChanged=1");
                } else {
                    response.sendRedirect(request.getContextPath() + "/staff/drivers?error=toggle_failed");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/staff/drivers?error=toggle_failed");
        }
    }
}

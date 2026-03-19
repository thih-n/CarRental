package com.carrental.controller.staff;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.StaffBookingDAO;
import com.carrental.entity.User;
import com.carrental.util.ValidationUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StaffBookingCancelServlet extends BaseAuthentication {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer contractID = ValidationUtil.parseIntegerOrNull(request.getParameter("contractID"));
        String cancelReason = request.getParameter("cancelReason");

        if (contractID == null || ValidationUtil.isBlank(cancelReason)) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings?error=missing_data");
            return;
        }

        // Get current staff user ID
        User user = (User) request.getSession().getAttribute(IConstant.SESSION_USER);
        int staffUserID = (user != null) ? user.getUserID() : 0;

        StaffBookingDAO dao = new StaffBookingDAO();
        boolean success = dao.cancelBooking(contractID, cancelReason.trim(), staffUserID);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings?cancelled=1");
        } else {
            response.sendRedirect(request.getContextPath() + "/staff/bookings?error=cancel_failed");
        }
    }
}

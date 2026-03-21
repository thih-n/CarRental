package com.carrental.controller.staff;

import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.DriverLeaveRequestDAO;
import com.carrental.entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class StaffDriverLeaveRejectServlet extends BaseAuthentication {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int leaveID = Integer.parseInt(request.getParameter("leaveID"));
            String rejectReason = request.getParameter("rejectReason");
            
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("USER_SESSION");
            int staffID = user.getUserID();

            DriverLeaveRequestDAO dao = new DriverLeaveRequestDAO();
            boolean success = dao.rejectLeave(leaveID, staffID, rejectReason);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/staff/driver/requests?rejected=1");
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/driver/requests?error=action_failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/staff/driver/requests?error=action_failed");
        }
    }
}

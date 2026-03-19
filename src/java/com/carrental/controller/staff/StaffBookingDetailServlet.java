package com.carrental.controller.staff;

import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.StaffBookingDAO;
import com.carrental.entity.StaffBookingItem;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "StaffBookingDetailServlet", urlPatterns = {"/staff/booking/detail"})
public class StaffBookingDetailServlet extends BaseAuthentication {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String contractIdStr = request.getParameter("id");
        
        if (contractIdStr == null || contractIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings");
            return;
        }
        
        try {
            int contractId = Integer.parseInt(contractIdStr);
            StaffBookingDAO dao = new StaffBookingDAO();
            StaffBookingItem booking = dao.getBookingById(contractId);
            
            if (booking == null) {
                response.sendRedirect(request.getContextPath() + "/staff/bookings?error=notfound");
                return;
            }
            
            request.setAttribute("booking", booking);
            request.getRequestDispatcher("/views/staff/booking-detail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings?error=invalid");
        }
    }
}

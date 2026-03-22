package com.carrental.controller.admin;

import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.AdminDashboardDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminDashboardServlet extends BaseAuthentication {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AdminDashboardDAO dao = new AdminDashboardDAO();
        request.setAttribute("totalUsers", dao.countUsers());
        request.setAttribute("activeCars", dao.countActiveCars());
        request.setAttribute("todayBookings", dao.countTodayBookings());
        request.setAttribute("revenuePoints", dao.getRevenueLastSixMonths());
        request.setAttribute("carStatusCounts", dao.getCarStatusCounts());
        
        // Load refund list
        String refundStatus = request.getParameter("refundStatus");
        String refundSearch = request.getParameter("refundSearch");
        request.setAttribute("refundList", dao.getRefunds(refundStatus, refundSearch));
        request.setAttribute("refundStatus", refundStatus);
        request.setAttribute("refundSearch", refundSearch);
        
        request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        AdminDashboardDAO dao = new AdminDashboardDAO();
        
        if ("completeRefund".equals(action)) {
            int paymentID = Integer.parseInt(request.getParameter("paymentID"));
            boolean success = dao.updateRefundStatus(paymentID, "Completed");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?success=" + success);
        }
    }
}

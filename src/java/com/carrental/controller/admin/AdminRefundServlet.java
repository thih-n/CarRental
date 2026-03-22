package com.carrental.controller.admin;

import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.AdminDashboardDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminRefundServlet extends BaseAuthentication {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AdminDashboardDAO dao = new AdminDashboardDAO();
        
        String status = request.getParameter("status");
        String search = request.getParameter("search");
        
        request.setAttribute("refundList", dao.getRefunds(status, search));
        request.setAttribute("refundStatus", status);
        request.setAttribute("refundSearch", search);
        
        request.getRequestDispatcher("/views/admin/refunds.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        AdminDashboardDAO dao = new AdminDashboardDAO();
        
        if ("completeRefund".equals(action)) {
            int paymentID = Integer.parseInt(request.getParameter("paymentID"));
            boolean success = dao.updateRefundStatus(paymentID, "Completed");
            response.sendRedirect(request.getContextPath() + "/admin/refunds?success=" + success);
        }
    }
}

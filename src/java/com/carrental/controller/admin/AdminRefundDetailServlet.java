package com.carrental.controller.admin;

import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.AdminDashboardDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminRefundDetailServlet extends BaseAuthentication {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int paymentID = Integer.parseInt(request.getParameter("paymentID"));
        AdminDashboardDAO dao = new AdminDashboardDAO();
        request.setAttribute("refundDetail", dao.getRefundDetail(paymentID));
        request.getRequestDispatcher("/views/admin/refund-detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        AdminDashboardDAO dao = new AdminDashboardDAO();
        int paymentID = Integer.parseInt(request.getParameter("paymentID"));

        if ("completeRefund".equals(action)) {
            boolean success = dao.updateRefundStatus(paymentID, "Completed");
            response.sendRedirect(request.getContextPath() + "/admin/refund-detail?paymentID=" + paymentID + (success ? "&success=true" : "&error=true"));
        }
    }
}

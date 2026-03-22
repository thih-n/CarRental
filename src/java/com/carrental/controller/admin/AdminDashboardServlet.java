package com.carrental.controller.admin;

import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.AdminDashboardDAO;
import com.carrental.dao.ReviewDAO;
import java.io.IOException;
import java.time.LocalDate;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminDashboardServlet extends BaseAuthentication {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AdminDashboardDAO dao = new AdminDashboardDAO();

        LocalDate now = LocalDate.now();
        Integer selectedMonth = parseIntOrDefault(request.getParameter("month"), now.getMonthValue());
        Integer selectedYear = parseIntOrDefault(request.getParameter("year"), now.getYear());

        request.setAttribute("selectedMonth", selectedMonth);
        request.setAttribute("selectedYear", selectedYear);
        request.setAttribute("totalUsers", dao.countUsers());
        request.setAttribute("activeCars", dao.countActiveCars());
        request.setAttribute("todayBookings", dao.countTodayBookings());
        request.setAttribute("revenuePoints", dao.getRevenueLastSixMonths(selectedMonth, selectedYear));
        request.setAttribute("selectedMonthRevenue", dao.getRevenueForMonth(selectedMonth, selectedYear));
        request.setAttribute("carStatusCounts", dao.getCarStatusCounts());

        // Load refund list
        String refundStatus = request.getParameter("refundStatus");
        String refundSearch = request.getParameter("refundSearch");
        request.setAttribute("refundList", dao.getRefundsByTime(refundStatus, refundSearch, selectedMonth, selectedYear));
        request.setAttribute("refundStatus", refundStatus);
        request.setAttribute("refundSearch", refundSearch);

        request.setAttribute("latestReviews", new ReviewDAO().getNewestReviews(10, selectedMonth, selectedYear));

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

    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}

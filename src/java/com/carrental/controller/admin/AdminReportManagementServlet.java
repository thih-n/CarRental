package com.carrental.controller.admin;

import com.carrental.dao.AdminDashboardDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdminReportManagementServlet", urlPatterns = {"/admin/reports"})
public class AdminReportManagementServlet extends HttpServlet {

    private final AdminDashboardDAO dashboardDAO = new AdminDashboardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get revenue from Booked + Completed bookings
        long totalRevenue = dashboardDAO.getTotalRevenueFromBookings();
        request.setAttribute("totalRevenue", totalRevenue);
        
        // Get completed bookings count
        int completedBookings = dashboardDAO.getCompletedBookingsCount();
        request.setAttribute("completedBookings", completedBookings);
        
        // Get cancelled bookings count
        int cancelledBookings = dashboardDAO.getCancelledBookingsCount();
        request.setAttribute("cancelledBookings", cancelledBookings);
        
        request.getRequestDispatcher("/views/admin/report-management.jsp").forward(request, response);
    }
}

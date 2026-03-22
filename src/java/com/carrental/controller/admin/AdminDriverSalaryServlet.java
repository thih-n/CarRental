package com.carrental.controller.admin;

import com.carrental.dao.AdminDashboardDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminDriverSalaryServlet", urlPatterns = {"/admin/driver-salaries"})
public class AdminDriverSalaryServlet extends HttpServlet {

    private final AdminDashboardDAO dashboardDAO = new AdminDashboardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get search parameter
        String search = request.getParameter("search");
        
        // Get month and year parameters - default to current month/year on first load
        String monthStr = request.getParameter("month");
        String yearStr = request.getParameter("year");
        
        int currentMonth = java.time.LocalDate.now().getMonthValue();
        int currentYear = java.time.Year.now().getValue();
        
        Integer month = null;
        Integer year = null;
        
        // Check if this is first load (no params)
        boolean isFirstLoad = (monthStr == null || monthStr.isEmpty()) && (yearStr == null || yearStr.isEmpty());
        
        try {
            if (monthStr != null && !monthStr.isEmpty()) {
                month = Integer.parseInt(monthStr);
            } else if (isFirstLoad) {
                month = currentMonth;
            }
            if (yearStr != null && !yearStr.isEmpty()) {
                year = Integer.parseInt(yearStr);
            } else if (isFirstLoad) {
                year = currentYear;
            }
        } catch (NumberFormatException e) {
            // Ignore invalid numbers
        }
        
        // Redirect to add default month/year on first load
        if (isFirstLoad) {
            String redirectURL = request.getContextPath() + "/admin/driver-salaries?month=" + currentMonth + "&year=" + currentYear;
            response.sendRedirect(redirectURL);
            return;
        }
        
        // Get driver salaries with filters
        List<Map<String, Object>> driverSalaries = dashboardDAO.getDriverSalaries(search, month, year);
        request.setAttribute("driverSalaries", driverSalaries);
        
        // Calculate total driver salaries
        BigDecimal totalDriverSalaries = BigDecimal.ZERO;
        BigDecimal totalBaseSalaries = BigDecimal.ZERO;
        for (Map<String, Object> s : driverSalaries) {
            Object salary = s.get("totalSalary");
            Object baseSalary = s.get("baseSalary");
            if (salary != null) {
                totalDriverSalaries = totalDriverSalaries.add((BigDecimal) salary);
            }
            if (baseSalary != null) {
                totalBaseSalaries = totalBaseSalaries.add((BigDecimal) baseSalary);
            }
        }
        
        // Total = base salary + commission
        BigDecimal grandTotal = totalDriverSalaries.add(totalBaseSalaries);
        request.setAttribute("totalDriverSalaries", grandTotal.longValue());
        
        // Pass filter values back to view
        request.setAttribute("search", search);
        request.setAttribute("month", month);
        request.setAttribute("year", year);
        
        // Get current year for year filter options
        request.setAttribute("currentYear", java.time.Year.now().getValue());
        
        request.getRequestDispatcher("/views/admin/driver-salary.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("update".equals(action)) {
            try {
                int userID = Integer.parseInt(request.getParameter("userID"));
                BigDecimal baseSalary = new BigDecimal(request.getParameter("baseSalary"));
                BigDecimal commissionRate = new BigDecimal(request.getParameter("commissionRate"));
                
                boolean updated = dashboardDAO.updateDriverSalary(userID, baseSalary, commissionRate);
                
                if (updated) {
                    response.sendRedirect(request.getContextPath() + "/admin/driver-salaries?success=update");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/driver-salaries?error=updateFailed");
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/admin/driver-salaries?error=invalidInput");
                return;
            }
        }
        
        doGet(request, response);
    }
}

package com.carrental.controller.staff;

import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.DriverProfileDAO;
import com.carrental.entity.DriverProfile;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.Date;

public class StaffDriverUpdateServlet extends BaseAuthentication {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int driverID = Integer.parseInt(request.getParameter("driverID"));
            String licenseNumber = request.getParameter("licenseNumber");
            Date licenseExpiry = Date.valueOf(request.getParameter("licenseExpiry"));
            Integer experienceYears = null;
            if (request.getParameter("experienceYears") != null && !request.getParameter("experienceYears").isEmpty()) {
                experienceYears = Integer.parseInt(request.getParameter("experienceYears"));
            }
            String driverStatus = request.getParameter("driverStatus");
            BigDecimal baseSalary = null;
            if (request.getParameter("baseSalary") != null && !request.getParameter("baseSalary").isEmpty()) {
                baseSalary = new BigDecimal(request.getParameter("baseSalary"));
            }
            BigDecimal commissionRate = null;
            if (request.getParameter("commissionRate") != null && !request.getParameter("commissionRate").isEmpty()) {
                commissionRate = new BigDecimal(request.getParameter("commissionRate"));
            }
            BigDecimal hourlyRate = null;
            if (request.getParameter("hourlyRate") != null && !request.getParameter("hourlyRate").isEmpty()) {
                hourlyRate = new BigDecimal(request.getParameter("hourlyRate"));
            }
            BigDecimal dailyRate = null;
            if (request.getParameter("dailyRate") != null && !request.getParameter("dailyRate").isEmpty()) {
                dailyRate = new BigDecimal(request.getParameter("dailyRate"));
            }

            DriverProfile driver = new DriverProfile();
            driver.setDriverID(driverID);
            driver.setLicenseNumber(licenseNumber);
            driver.setLicenseExpiry(licenseExpiry);
            driver.setExperienceYears(experienceYears);
            driver.setDriverStatus(driverStatus);
            driver.setBaseSalary(baseSalary);
            driver.setCommissionRate(commissionRate);
            driver.setHourlyRate(hourlyRate);
            driver.setDailyRate(dailyRate);

            DriverProfileDAO dao = new DriverProfileDAO();
            boolean success = dao.updateDriver(driver);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/staff/drivers?updated=1");
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/drivers?error=update_failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/staff/drivers?error=update_failed");
        }
    }
}

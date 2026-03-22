package com.carrental.controller.staff;

import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.DriverProfileDAO;
import com.carrental.entity.DriverProfile;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public class StaffDriverListServlet extends BaseAuthentication {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String statusFilter = request.getParameter("status");
        String search = request.getParameter("search");

        DriverProfileDAO dao = new DriverProfileDAO();
        
        // Get all drivers with user info
        List<DriverProfile> drivers = dao.getAllDrivers(statusFilter, search);
        
        request.setAttribute("drivers", drivers);
        request.setAttribute("currentStatus", statusFilter != null ? statusFilter : "all");
        request.setAttribute("currentSearch", search);

        request.getRequestDispatcher("/views/staff/driver-account.jsp").forward(request, response);
    }
}

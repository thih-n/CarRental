package com.carrental.controller.staff;

import com.carrental.controller.web.auth.BaseAuthentication;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StaffDashboardServlet extends BaseAuthentication {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/staff/dashboard.jsp").forward(request, response);
    }
}

package com.carrental.controller.staff;

import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.DriverLeaveRequestDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public class StaffDriverLeaveListServlet extends BaseAuthentication {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String statusFilter = request.getParameter("status");
        String search = request.getParameter("search");

        DriverLeaveRequestDAO dao = new DriverLeaveRequestDAO();
        
        List<DriverLeaveRequestDAO.DriverLeaveRequest> requests = dao.getAllRequests(statusFilter, search);
        
        request.setAttribute("requests", requests);
        request.setAttribute("currentStatus", statusFilter != null ? statusFilter : "all");
        request.setAttribute("currentSearch", search);

        request.getRequestDispatcher("/views/staff/driver-requests.jsp").forward(request, response);
    }
}

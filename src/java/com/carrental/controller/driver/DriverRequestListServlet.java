package com.carrental.controller.driver;

import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.DriverLeaveRequestDAO;
import com.carrental.entity.User;
import com.carrental.constant.IConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class DriverRequestListServlet extends BaseAuthentication {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        User user = (User) request.getSession().getAttribute(IConstant.SESSION_USER);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        int driverID = user.getUserID();
        
        DriverLeaveRequestDAO dao = new DriverLeaveRequestDAO();
        List<DriverLeaveRequestDAO.DriverLeaveRequest> requests = dao.getRequestsByDriver(driverID);
        
        request.setAttribute("requests", requests);
        request.getRequestDispatcher("/views/driver/requests.jsp").forward(request, response);
    }
}

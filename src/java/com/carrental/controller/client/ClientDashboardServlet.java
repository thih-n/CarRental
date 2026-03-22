package com.carrental.controller.client;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.ClientBookingDAO;
import com.carrental.entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ClientDashboardServlet extends BaseAuthentication {

    @Override
    protected boolean shouldCheckPermission() {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(IConstant.SESSION_USER);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        ClientBookingDAO dao = new ClientBookingDAO();
        request.setAttribute("currentTrips", dao.getCurrentOrders(user.getUserID()));
        request.setAttribute("historyTrips", dao.getHistory(user.getUserID()));
        request.getRequestDispatcher("/views/client/dashboard.jsp").forward(request, response);
    }
}

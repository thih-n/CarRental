package com.carrental.controller.client;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.ClientBookingDAO;
import com.carrental.entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ClientHistoryServlet extends BaseAuthentication {

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

        request.setAttribute("history", new ClientBookingDAO().getHistory(user.getUserID()));
        request.getRequestDispatcher("/views/client/history.jsp").forward(request, response);
    }
}

package com.carrental.controller.web.profile;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.UserDAO;
import com.carrental.entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ProfileViewServlet extends BaseAuthentication {

    @Override
    protected boolean shouldCheckPermission() {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User sessionUser = session == null ? null : (User) session.getAttribute(IConstant.SESSION_USER);
        if (sessionUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = new UserDAO().getUserById(sessionUser.getUserID());
        request.setAttribute("profile", user);
        request.getRequestDispatcher("/views/profile/profile-view.jsp").forward(request, response);
    }
}

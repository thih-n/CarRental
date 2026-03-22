package com.carrental.controller.web.profile;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.UserDAO;
import com.carrental.entity.User;
import com.carrental.util.ValidationUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ProfileUpdateServlet extends BaseAuthentication {

    @Override
    protected boolean shouldCheckPermission() {
        return false;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User sessionUser = session == null ? null : (User) session.getAttribute(IConstant.SESSION_USER);
        if (sessionUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String fullName = request.getParameter("fullName");
        String phoneNumber = request.getParameter("phoneNumber");
        String address = request.getParameter("address");

        if (ValidationUtil.isBlank(fullName)) {
            response.sendRedirect(request.getContextPath() + "/profile?error=fullname_required");
            return;
        }

        UserDAO dao = new UserDAO();
        boolean ok = dao.updateUserProfile(sessionUser.getUserID(), fullName.trim(), phoneNumber, address);
        if (!ok) {
            response.sendRedirect(request.getContextPath() + "/profile?error=update_failed");
            return;
        }

        User latest = dao.getUserById(sessionUser.getUserID());
        session.setAttribute(IConstant.SESSION_USER, latest);
        response.sendRedirect(request.getContextPath() + "/profile?updated=1");
    }
}

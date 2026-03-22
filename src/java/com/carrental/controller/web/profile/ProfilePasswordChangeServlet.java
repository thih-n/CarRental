package com.carrental.controller.web.profile;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.UserDAO;
import com.carrental.entity.User;
import com.carrental.util.PasswordUtil;
import com.carrental.util.ValidationUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ProfilePasswordChangeServlet extends BaseAuthentication {

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

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!ValidationUtil.isValidPassword(newPassword)) {
            response.sendRedirect(request.getContextPath() + "/profile?error=invalid_new_password");
            return;
        }

        if (confirmPassword == null || !confirmPassword.equals(newPassword)) {
            response.sendRedirect(request.getContextPath() + "/profile?error=password_confirm_not_match");
            return;
        }

        UserDAO dao = new UserDAO();
        boolean changed = dao.changePassword(
                sessionUser.getUserID(),
                PasswordUtil.hashPassword(currentPassword),
                PasswordUtil.hashPassword(newPassword)
        );

        if (!changed) {
            response.sendRedirect(request.getContextPath() + "/profile?error=change_password_failed");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/profile?passwordChanged=1");
    }
}

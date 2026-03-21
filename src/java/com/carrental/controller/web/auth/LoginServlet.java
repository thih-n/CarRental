package com.carrental.controller.web.auth;

import com.carrental.constant.IConstant;
import com.carrental.dao.UserDAO;
import com.carrental.entity.User;
import com.carrental.util.PasswordUtil;
import java.io.IOException;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String rememberedEmail = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("rememberEmail".equals(cookie.getName())) {
                    rememberedEmail = cookie.getValue();
                    break;
                }
            }
        }

        request.setAttribute("rememberedEmail", rememberedEmail);
        request.setAttribute("rememberChecked", rememberedEmail != null && !rememberedEmail.isEmpty());
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Email và mật khẩu không được để trống.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        String hashedPassword = PasswordUtil.hashPassword(password);

        UserDAO dao = new UserDAO();
        User user = dao.checkLogin(email.trim(), hashedPassword);

        if (user == null) {
            request.setAttribute("error", "Sai email hoặc mật khẩu.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        String remember = request.getParameter("remember");
        if ("on".equals(remember)) {
            Cookie cookie = new Cookie("rememberEmail", email.trim());
            cookie.setMaxAge(7 * 24 * 60 * 60);
            cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
            response.addCookie(cookie);
        } else {
            Cookie cookie = new Cookie("rememberEmail", "");
            cookie.setMaxAge(0);
            cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
            response.addCookie(cookie);
        }

        String newToken = UUID.randomUUID().toString();
        dao.updateSessionToken(user.getUserID(), newToken);

        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(IConstant.SESSION_USER, user);
        session.setAttribute(IConstant.SESSION_CURRENT_TOKEN, newToken);

        int roleID = user.getRoleID();
        if (roleID == IConstant.ROLE_ID_CUSTOMER) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        if (roleID == IConstant.ROLE_ID_ADMIN) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }
        if (roleID == IConstant.ROLE_ID_STAFF) {
            response.sendRedirect(request.getContextPath() + "/staff/dashboard");
            return;
        }
        if (roleID == IConstant.ROLE_ID_DRIVER) {
            response.sendRedirect(request.getContextPath() + "/driver/dashboard");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/home");
    }
}

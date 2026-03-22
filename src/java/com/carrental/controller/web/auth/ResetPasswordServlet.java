package com.carrental.controller.web.auth;

import com.carrental.dao.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/reset-password"})
public class ResetPasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("resetUserId") : null;
        
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        
        request.setAttribute("userId", userId);
        request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("resetUserId") : null;
        
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        if (password == null || password.isEmpty() || 
            confirmPassword == null || confirmPassword.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin");
            request.setAttribute("userId", userId);
            request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp");
            request.setAttribute("userId", userId);
            request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
            return;
        }
        
        if (password.length() < 6) {
            request.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự");
            request.setAttribute("userId", userId);
            request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
            return;
        }
        
        // Update password
        boolean success = userDAO.resetPassword(userId, password);
        
        if (success) {
            // Clear the reset token and session
            userDAO.clearResetToken(userId);
            if (session != null) {
                session.removeAttribute("resetToken");
                session.removeAttribute("resetUserId");
                session.removeAttribute("resetEmail");
            }
            response.sendRedirect(request.getContextPath() + "/login?message=password_reset_success");
        } else {
            request.setAttribute("error", "Đã xảy ra lỗi. Vui lòng thử lại");
            request.setAttribute("userId", userId);
            request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
        }
    }
}

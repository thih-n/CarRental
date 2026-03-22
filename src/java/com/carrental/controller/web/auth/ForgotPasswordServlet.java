package com.carrental.controller.web.auth;

import com.carrental.entity.Email;
import com.carrental.entity.User;
import com.carrental.dao.UserDAO;
import java.io.IOException;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final Email emailService = new Email();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Clear any existing session data
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("resetToken");
            session.removeAttribute("resetUserId");
        }
        request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập địa chỉ email");
            request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
            return;
        }
        
        // Check if email exists
        User user = userDAO.getUserByEmail(email.trim());
        
        if (user == null) {
            // Don't reveal if email exists for security
            request.setAttribute("success", "Nếu email tồn tại, mã xác nhận sẽ được gửi đến bạn");
            request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
            return;
        }
        
        // Generate reset token (6 digits)
        String resetToken = String.format("%06d", new java.util.Random().nextInt(999999));
        
        // Save token to database with expiry (15 minutes)
        boolean tokenSaved = userDAO.savePasswordResetToken(user.getUserID(), resetToken);
        
        if (!tokenSaved) {
            request.setAttribute("error", "Đã xảy ra lỗi. Vui lòng thử lại sau");
            request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
            return;
        }
        
        // Send email with reset token (not link)
        boolean emailSent = emailService.sendForgotPasswordEmail(
            user.getEmail(), 
            user.getFullName(), 
            resetToken
        );
        
        if (emailSent) {
            // Store email in session for display
            HttpSession session = request.getSession();
            session.setAttribute("resetEmail", email.trim());
            response.sendRedirect(request.getContextPath() + "/confirm-token");
        } else {
            request.setAttribute("error", "Không thể gửi email. Vui lòng thử lại sau");
            request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
        }
    }
}

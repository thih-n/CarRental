package com.carrental.controller.web.auth;

import com.carrental.dao.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ConfirmTokenServlet", urlPatterns = {"/confirm-token"})
public class ConfirmTokenServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String resetEmail = (session != null) ? (String) session.getAttribute("resetEmail") : null;
        
        if (resetEmail == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        
        request.setAttribute("resetEmail", resetEmail);
        request.getRequestDispatcher("/views/auth/confirm-token.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String resetEmail = (session != null) ? (String) session.getAttribute("resetEmail") : null;
        
        if (resetEmail == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        
        String token = request.getParameter("token");
        
        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mã xác nhận");
            request.setAttribute("resetEmail", resetEmail);
            request.getRequestDispatcher("/views/auth/confirm-token.jsp").forward(request, response);
            return;
        }
        
        // Verify token
        int userId = userDAO.verifyResetToken(token.trim());
        
        if (userId <= 0) {
            request.setAttribute("error", "Mã xác nhận không đúng hoặc đã hết hạn");
            request.setAttribute("resetEmail", resetEmail);
            request.getRequestDispatcher("/views/auth/confirm-token.jsp").forward(request, response);
            return;
        }
        
        // Token is valid, store in session and redirect to reset password
        if (session != null) {
            session.setAttribute("resetToken", token.trim());
            session.setAttribute("resetUserId", userId);
        }
        
        response.sendRedirect(request.getContextPath() + "/reset-password");
    }
}

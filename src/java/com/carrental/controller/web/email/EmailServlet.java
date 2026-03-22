package com.carrental.controller.web.email;

import com.carrental.entity.Email;
import com.carrental.entity.User;
import com.carrental.dao.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet(name = "EmailServlet", urlPatterns = {"/api/email/*"})
public class EmailServlet extends HttpServlet {

    private final Email emailService = new Email();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();
        JSONObject result = new JSONObject();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                result.put("success", false);
                result.put("message", "Invalid endpoint");
                response.setStatus(400);
                out.print(result.toString());
                return;
            }
            
            String action = pathInfo.substring(1); // Remove leading slash
            
            switch (action) {
                case "forgot-password":
                    handleForgotPassword(request, result);
                    break;
                case "account-locked":
                    handleAccountLocked(request, result);
                    break;
                case "trip-cancelled":
                    handleTripCancelled(request, result);
                    break;
                default:
                    result.put("success", false);
                    result.put("message", "Unknown action: " + action);
                    response.setStatus(404);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Server error: " + e.getMessage());
            response.setStatus(500);
            e.printStackTrace();
        }
        
        out.print(result.toString());
    }

    private void handleForgotPassword(HttpServletRequest request, JSONObject result) throws Exception {
        String emailOrPhone = request.getParameter("email");
        
        if (emailOrPhone == null || emailOrPhone.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "Email is required");
            return;
        }
        
        User user = userDAO.getUserByEmail(emailOrPhone);
        if (user == null) {
            // Don't reveal if email exists or not for security
            result.put("success", true);
            result.put("message", "If email exists, reset link will be sent");
            return;
        }
        
        // Generate reset token (in real app, save to DB with expiry)
        String resetToken = java.util.UUID.randomUUID().toString();
        
        boolean sent = emailService.sendForgotPasswordEmail(
            user.getEmail(), 
            user.getFullName(), 
            resetToken
        );
        
        if (sent) {
            // Save token to database with expiry time
            userDAO.savePasswordResetToken(user.getUserID(), resetToken);
            result.put("success", true);
            result.put("message", "Reset link sent to your email");
        } else {
            result.put("success", false);
            result.put("message", "Failed to send email");
        }
    }

    private void handleAccountLocked(HttpServletRequest request, JSONObject result) throws Exception {
        int userId = Integer.parseInt(request.getParameter("userId"));
        String reason = request.getParameter("reason");
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            result.put("success", false);
            result.put("message", "User not found");
            return;
        }
        
        boolean sent = emailService.sendAccountLockedEmail(
            user.getEmail(),
            user.getFullName(),
            reason != null ? reason : "Vi phạm quy định sử dụng"
        );
        
        result.put("success", sent);
        result.put("message", sent ? "Email sent successfully" : "Failed to send email");
    }

    private void handleTripCancelled(HttpServletRequest request, JSONObject result) throws Exception {
        int userId = Integer.parseInt(request.getParameter("userId"));
        String bookingId = request.getParameter("bookingId");
        String reason = request.getParameter("reason");
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            result.put("success", false);
            result.put("message", "User not found");
            return;
        }
        
        boolean sent = emailService.sendTripCancelledEmail(
            user.getEmail(),
            user.getFullName(),
            bookingId != null ? bookingId : "N/A",
            reason != null ? reason : "Hủy bởi quản trị viên"
        );
        
        result.put("success", sent);
        result.put("message", sent ? "Email sent successfully" : "Failed to send email");
    }

    @Override
    public String getServletInfo() {
        return "Email Servlet for sending notifications";
    }
}

package com.carrental.controller.web.auth;

import com.carrental.dao.UserDAO;
import com.carrental.entity.User;
import com.carrental.util.PasswordUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        // Store values for re-populating form
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
        request.setAttribute("address", address);

        // Validation errors map
        java.util.Map<String, String> errors = new java.util.HashMap<>();

        // FullName validation
        if (fullName == null || fullName.trim().isEmpty()) {
            errors.put("fullName", "Vui lòng nhập họ tên");
        } else if (fullName.trim().length() < 2) {
            errors.put("fullName", "Họ tên phải có ít nhất 2 ký tự");
        } else if (fullName.trim().length() > 100) {
            errors.put("fullName", "Họ tên không được quá 100 ký tự");
        }

        // Email validation
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Vui lòng nhập email");
        } else if (!isValidEmail(email.trim())) {
            errors.put("email", "Email không hợp lệ");
        } else {
            // Check if email exists
            UserDAO dao = new UserDAO();
            if (dao.isEmailExists(email.trim())) {
                errors.put("email", "Email đã được sử dụng");
            }
        }

        // Password validation
        if (password == null || password.isEmpty()) {
            errors.put("password", "Vui lòng nhập mật khẩu");
        } else if (password.length() < 6) {
            errors.put("password", "Mật khẩu phải có ít nhất 6 ký tự");
        } else if (password.length() > 50) {
            errors.put("password", "Mật khẩu không được quá 50 ký tự");
        } else if (!hasValidPassword(password)) {
            errors.put("password", "Mật khẩu phải chứa ít nhất 1 chữ cái và 1 số");
        }

        // ConfirmPassword validation
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            errors.put("confirmPassword", "Vui lòng xác nhận mật khẩu");
        } else if (!password.equals(confirmPassword)) {
            errors.put("confirmPassword", "Mật khẩu xác nhận không khớp");
        }

        // Phone validation (optional - no validation)
        // Phone is stored but not validated

        // If there are errors, return to form
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Create new user
        User newUser = new User();
        newUser.setFullName(fullName.trim());
        newUser.setEmail(email.trim());
        newUser.setPhoneNumber(phone == null ? null : phone.trim());
        newUser.setAddress(address == null ? null : address.trim());
        newUser.setPasswordHash(PasswordUtil.hashPassword(password));

        UserDAO dao = new UserDAO();
        boolean ok = dao.registerUser(newUser);

        if (ok) {
            response.sendRedirect(request.getContextPath() + "/login?message=registered");
        } else {
            request.setAttribute("error", "Không thể đăng ký, vui lòng thử lại.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhone(String phone) {
        // Vietnamese phone: 10-11 digits, starts with 0
        String phoneRegex = "^0\\d{9,10}$";
        return phone.matches(phoneRegex);
    }

    private boolean hasValidPassword(String password) {
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasLetter && hasDigit;
    }
}

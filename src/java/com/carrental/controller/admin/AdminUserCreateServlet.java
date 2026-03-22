package com.carrental.controller.admin;

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

public class AdminUserCreateServlet extends BaseAuthentication {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/admin/user-create.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        Integer roleID = ValidationUtil.parseIntegerOrNull(request.getParameter("roleID"));

        if (ValidationUtil.isBlank(fullName) || ValidationUtil.isBlank(email) || ValidationUtil.isBlank(password) || roleID == null) {
            request.setAttribute("error", "Thiếu dữ liệu bắt buộc.");
            request.getRequestDispatcher("/views/admin/user-create.jsp").forward(request, response);
            return;
        }

        if (roleID == IConstant.ROLE_ID_CUSTOMER) {
            request.setAttribute("error", "Admin không tạo role Customer ở màn này.");
            request.getRequestDispatcher("/views/admin/user-create.jsp").forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();
        if (dao.isEmailExists(email.trim())) {
            request.setAttribute("error", "Email đã tồn tại.");
            request.getRequestDispatcher("/views/admin/user-create.jsp").forward(request, response);
            return;
        }

        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setPasswordHash(PasswordUtil.hashPassword(password));
        user.setPhoneNumber(request.getParameter("phoneNumber"));
        user.setAddress(request.getParameter("address"));

        boolean ok = dao.createUserByAdmin(user, roleID);
        if (!ok) {
            request.setAttribute("error", "Tạo tài khoản thất bại.");
            request.getRequestDispatcher("/views/admin/user-create.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/users/create?success=1");
    }
}

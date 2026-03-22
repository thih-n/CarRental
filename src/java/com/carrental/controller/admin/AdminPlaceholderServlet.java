package com.carrental.controller.admin;

import com.carrental.controller.web.auth.BaseAuthentication;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminPlaceholderServlet extends BaseAuthentication {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/admin/placeholder.jsp").forward(request, response);
    }
}

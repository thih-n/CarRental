package com.carrental.controller.web;

import com.carrental.controller.web.auth.BaseAuthentication;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HomeServlet extends BaseAuthentication {

    @Override
    protected boolean isPublicEndpoint() {
        return true;
    }

    @Override
    protected boolean shouldCheckPermission() {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
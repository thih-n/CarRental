package com.carrental.controller.web.auth;

import com.carrental.dao.AuthorizationDAO;
import com.carrental.entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Base class for protected controllers.
 *
 * Usage:
 * - Extend this class for controllers that require login/authorization.
 * - Override isPublicEndpoint() if endpoint does not need login.
 * - Override shouldCheckPermission() if endpoint only needs authentication.
 */
public abstract class BaseAuthentication extends HttpServlet {

    private final AuthorizationDAO authorizationDAO = new AuthorizationDAO();

    @Override
    protected final void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (isPublicEndpoint()) {
            super.service(req, resp);
            return;
        }

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("USER_SESSION");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (shouldCheckPermission()) {
            String endpoint = normalizeEndpoint(req);
            String method = req.getMethod();
            boolean allowed = authorizationDAO.hasPermission(user.getRoleID(), endpoint, method);

            if (!allowed) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this resource.");
                return;
            }
        }

        super.service(req, resp);
    }

    protected boolean isPublicEndpoint() {
        return false;
    }

    protected boolean shouldCheckPermission() {
        return true;
    }

    protected String normalizeEndpoint(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String endpoint = uri.startsWith(contextPath) ? uri.substring(contextPath.length()) : uri;
        return endpoint.isEmpty() ? "/" : endpoint;
    }
}

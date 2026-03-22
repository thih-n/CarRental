package com.carrental.controller.web.booking;

import com.carrental.controller.web.auth.BaseAuthentication;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BookingSuccessServlet extends BaseAuthentication {

    @Override
    protected boolean shouldCheckPermission() {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("contractId", request.getParameter("contractId"));
        request.getRequestDispatcher("/views/booking/success.jsp").forward(request, response);
    }
}

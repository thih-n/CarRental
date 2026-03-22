package com.carrental.controller.admin;

import com.carrental.dao.BookingLookupDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "AdminBookingDetailServlet", urlPatterns = {"/admin/booking/detail"})
public class AdminBookingDetailServlet extends HttpServlet {

    private final BookingLookupDAO bookingDAO = new BookingLookupDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int bookingID = Integer.parseInt(request.getParameter("id"));
            Map<String, Object> booking = bookingDAO.getBookingById(bookingID);
            request.setAttribute("booking", booking);
            request.getRequestDispatcher("/views/admin/booking-detail.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
        }
    }
}
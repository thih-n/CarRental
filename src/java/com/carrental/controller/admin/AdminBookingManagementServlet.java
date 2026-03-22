package com.carrental.controller.admin;

import com.carrental.dao.BookingLookupDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminBookingManagementServlet", urlPatterns = {"/admin/bookings"})
public class AdminBookingManagementServlet extends HttpServlet {

    private final BookingLookupDAO bookingDAO = new BookingLookupDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("detail".equals(action)) {
            int bookingID = Integer.parseInt(request.getParameter("id"));
            var booking = bookingDAO.getBookingById(bookingID);
            request.setAttribute("booking", booking);
            request.getRequestDispatcher("/views/admin/booking-detail.jsp").forward(request, response);
            return;
        }
        
        // Handle search
        String contractCode = request.getParameter("contractCode");
        String customerName = request.getParameter("customerName");
        
        List<Map<String, Object>> bookings;
        if ((contractCode != null && !contractCode.trim().isEmpty()) 
                || (customerName != null && !customerName.trim().isEmpty())) {
            bookings = bookingDAO.searchBookings(contractCode, customerName);
        } else {
            bookings = bookingDAO.getAllBookings();
        }
        
        // Get all customer names for autocomplete
        List<String> customerNames = bookingDAO.getAllCustomerNames();
        
        request.setAttribute("bookings", bookings);
        request.setAttribute("contractCode", contractCode);
        request.setAttribute("customerName", customerName);
        request.setAttribute("customerNames", customerNames);
        
        request.getRequestDispatcher("/views/admin/booking-management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("approve".equals(action)) {
            int bookingID = Integer.parseInt(request.getParameter("bookingID"));
            boolean approved = bookingDAO.updateBookingStatus(bookingID, "Approved");
            response.sendRedirect(request.getContextPath() + "/admin/bookings?success=approve");
            return;
        }
        
        if ("reject".equals(action)) {
            int bookingID = Integer.parseInt(request.getParameter("bookingID"));
            boolean rejected = bookingDAO.updateBookingStatus(bookingID, "Rejected");
            response.sendRedirect(request.getContextPath() + "/admin/bookings?success=reject");
            return;
        }
        
        if ("cancel".equals(action)) {
            int bookingID = Integer.parseInt(request.getParameter("bookingID"));
            boolean cancelled = bookingDAO.updateBookingStatus(bookingID, "Cancelled");
            response.sendRedirect(request.getContextPath() + "/admin/bookings?success=cancel");
            return;
        }
        
        doGet(request, response);
    }
}

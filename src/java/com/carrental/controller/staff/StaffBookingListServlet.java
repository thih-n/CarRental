package com.carrental.controller.staff;

import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.BookingLookupDAO;
import com.carrental.dao.StaffBookingDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StaffBookingListServlet extends BaseAuthentication {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get filter parameters from request
        String statusFilter = request.getParameter("status");
        String searchCode = request.getParameter("search");

        StaffBookingDAO dao = new StaffBookingDAO();

        // If no filters provided, default to "Booked" status
        if ((statusFilter == null || statusFilter.isEmpty()) && (searchCode == null || searchCode.isEmpty())) {
            request.setAttribute("bookings", dao.getBookingsWithFilter("Booked", null));
            request.setAttribute("currentStatus", "Booked");
        } else {
            // Use the filter method with provided parameters
            request.setAttribute("bookings", dao.getBookingsWithFilter(statusFilter, searchCode));
            request.setAttribute("currentStatus", statusFilter);
        }

        // Pass search value back to the view
        request.setAttribute("currentSearch", searchCode);

        request.setAttribute("locations", new BookingLookupDAO().getAllLocations());

        request.getRequestDispatcher("/views/staff/bookings.jsp").forward(request, response);
    }
}

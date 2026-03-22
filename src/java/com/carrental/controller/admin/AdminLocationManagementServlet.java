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

@WebServlet(name = "AdminLocationManagementServlet", urlPatterns = {"/admin/locations"})
public class AdminLocationManagementServlet extends HttpServlet {

    private final BookingLookupDAO lookupDAO = new BookingLookupDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("edit".equals(action)) {
            int locationID = Integer.parseInt(request.getParameter("id"));
            var location = lookupDAO.getLocationById(locationID);
            request.setAttribute("location", location);
            request.getRequestDispatcher("/views/admin/location-edit.jsp").forward(request, response);
            return;
        }
        
        if ("delete".equals(action)) {
            int locationID = Integer.parseInt(request.getParameter("id"));
            boolean deleted = lookupDAO.deleteLocation(locationID);
            response.sendRedirect(request.getContextPath() + "/admin/locations?success=delete");
            return;
        }
        
        request.setAttribute("locations", lookupDAO.getAllLocationsAdmin());
        request.getRequestDispatcher("/views/admin/location-management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            String locationName = request.getParameter("locationName");
            String address = request.getParameter("address");
            
            boolean added = lookupDAO.addLocation(locationName, address);
            response.sendRedirect(request.getContextPath() + "/admin/locations?success=add");
            return;
        }
        
        if ("update".equals(action)) {
            int locationID = Integer.parseInt(request.getParameter("locationID"));
            String locationName = request.getParameter("locationName");
            String address = request.getParameter("address");
            
            boolean updated = lookupDAO.updateLocation(locationID, locationName, address);
            response.sendRedirect(request.getContextPath() + "/admin/locations?success=update");
            return;
        }
        
        doGet(request, response);
    }
}

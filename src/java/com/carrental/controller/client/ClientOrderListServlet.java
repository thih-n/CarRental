package com.carrental.controller.client;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.ClientBookingDAO;
import com.carrental.dao.ReviewDAO;
import com.carrental.entity.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ClientOrderListServlet extends BaseAuthentication {

    @Override
    protected boolean shouldCheckPermission() {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute(IConstant.SESSION_USER);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Get filter parameters from request
        String statusFilter = request.getParameter("status");
        String searchCode = request.getParameter("search");

        ClientBookingDAO dao = new ClientBookingDAO();
        ReviewDAO reviewDAO = new ReviewDAO();

        List<com.carrental.entity.ClientBookingItem> orders;
        
        // If no filters, get current orders by default
        if ((statusFilter == null || statusFilter.isEmpty()) && (searchCode == null || searchCode.isEmpty())) {
            orders = dao.getCurrentOrders(user.getUserID());
        } else {
            // Use the new filter method
            orders = dao.getOrdersWithFilter(user.getUserID(), statusFilter, searchCode);
        }
        
        // Check which orders can be reviewed (Completed + has driver + not yet reviewed)
        for (com.carrental.entity.ClientBookingItem item : orders) {
            boolean canReview = "Completed".equals(item.getDetailStatus()) 
                    && item.getDriverID() != null 
                    && !reviewDAO.hasReviewed(item.getContractID(), item.getDriverID());
            request.setAttribute("canReview_" + item.getContractID(), canReview);
        }

        // Pass filter values back to the view
        request.setAttribute("orders", orders);
        request.setAttribute("currentStatus", statusFilter);
        request.setAttribute("currentSearch", searchCode);

        request.getRequestDispatcher("/views/client/orders.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute(IConstant.SESSION_USER);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if ("review".equals(action)) {
            Integer contractID = com.carrental.util.ValidationUtil.parseIntegerOrNull(request.getParameter("contractID"));
            Integer driverID = com.carrental.util.ValidationUtil.parseIntegerOrNull(request.getParameter("driverID"));
            Integer rating = com.carrental.util.ValidationUtil.parseIntegerOrNull(request.getParameter("rating"));
            String comment = request.getParameter("comment");

            if (contractID == null || driverID == null || rating == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            ReviewDAO reviewDAO = new ReviewDAO();
            boolean success = reviewDAO.insertReview(contractID, user.getUserID(), "driver", driverID, rating, comment);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/client/orders?success=reviewed");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            return;
        }

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
}

package com.carrental.controller.client;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.ClientBookingDAO;
import com.carrental.dao.DriverProfileDAO;
import com.carrental.dao.ReviewDAO;
import com.carrental.entity.ClientBookingItem;
import com.carrental.entity.DriverProfile;
import com.carrental.entity.User;
import com.carrental.util.ValidationUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ClientTripDetailServlet extends BaseAuthentication {

    @Override
    protected boolean shouldCheckPermission() {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute(IConstant.SESSION_USER);
        Integer contractID = ValidationUtil.parseIntegerOrNull(request.getParameter("id"));
        if (user == null || contractID == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        ClientBookingItem item = new ClientBookingDAO().getDetail(user.getUserID(), contractID);
        if (item == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("trip", item);
        
        // If trip has driver, get full driver profile
        if (item.getDriverID() != null) {
            DriverProfileDAO driverDAO = new DriverProfileDAO();
            DriverProfile driver = driverDAO.getById(item.getDriverID());
            request.setAttribute("driver", driver);
        }

        boolean hasDriverReview = false;
        if (item.getDriverID() != null) {
            hasDriverReview = new ReviewDAO().hasReviewed(item.getContractID(), item.getDriverID());
        }
        request.setAttribute("hasDriverReview", hasDriverReview);
        
        request.getRequestDispatcher("/views/client/trip-detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute(IConstant.SESSION_USER);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String action = request.getParameter("action");
        Integer contractID = ValidationUtil.parseIntegerOrNull(request.getParameter("contractID"));
        
        if (contractID == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        ClientBookingDAO dao = new ClientBookingDAO();
        
        if ("cancel".equals(action)) {
            String cancelReason = request.getParameter("cancelReason");
            if (cancelReason == null || cancelReason.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cancel reason is required");
                return;
            }
            try {
                boolean success = dao.cancelBooking(contractID, user.getUserID(), cancelReason.trim());
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/client/orders?success=cancelled");
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to cancel booking");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
            return;
        }

        if ("review-driver".equals(action)) {
            Integer driverID = ValidationUtil.parseIntegerOrNull(request.getParameter("driverID"));
            Integer rating = ValidationUtil.parseIntegerOrNull(request.getParameter("rating"));
            String comment = request.getParameter("comment");

            if (driverID == null || rating == null || rating < 1 || rating > 5) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid review data");
                return;
            }

            ReviewDAO reviewDAO = new ReviewDAO();
            if (reviewDAO.hasReviewed(contractID, driverID)) {
                response.sendRedirect(request.getContextPath() + "/client/trip/detail?id=" + contractID + "&success=reviewed");
                return;
            }

            boolean inserted = reviewDAO.insertReview(
                    contractID,
                    user.getUserID(),
                    "driver",
                    driverID,
                    rating,
                    comment != null ? comment.trim() : null
            );

            if (inserted) {
                response.sendRedirect(request.getContextPath() + "/client/trip/detail?id=" + contractID + "&success=reviewed");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to submit review");
            }
            return;
        }

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
}

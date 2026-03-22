package com.carrental.controller.admin;

import com.carrental.dao.ReviewDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminReviewManagementServlet", urlPatterns = {"/admin/reviews"})
public class AdminReviewManagementServlet extends HttpServlet {

    private final ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List reviews = reviewDAO.getAllReviews();
        request.setAttribute("reviews", reviews);
        request.getRequestDispatcher("/views/admin/review-management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("delete".equals(action)) {
            int reviewID = Integer.parseInt(request.getParameter("reviewID"));
            boolean deleted = reviewDAO.deleteReview(reviewID);
            response.sendRedirect(request.getContextPath() + "/admin/reviews?success=delete");
            return;
        }
        
        doGet(request, response);
    }
}

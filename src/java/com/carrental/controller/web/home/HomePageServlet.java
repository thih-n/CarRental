package com.carrental.controller.web.home;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.BookingLookupDAO;
import com.carrental.dao.CarDAO;
import com.carrental.dao.DriverProfileDAO;
import com.carrental.dao.ReviewDAO;
import com.carrental.entity.CarSearchResult;
import com.carrental.util.ValidationUtil;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HomePageServlet extends BaseAuthentication {

    @Override
    protected boolean shouldCheckPermission() {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int page = ValidationUtil.normalizePage(
                ValidationUtil.parseIntOrDefault(request.getParameter("page"), IConstant.DEFAULT_PAGE));
        int pageSize = ValidationUtil.normalizePageSize(
                ValidationUtil.parseIntOrDefault(request.getParameter("pageSize"), IConstant.DEFAULT_PAGE_SIZE),
                IConstant.MAX_PAGE_SIZE,
                IConstant.DEFAULT_PAGE_SIZE);

        String sortBy = ValidationUtil.normalizeSortBy(request.getParameter("sortBy"));
        String sortOrder = ValidationUtil.normalizeSortOrder(request.getParameter("sortOrder"));

        CarDAO carDAO = new CarDAO();
        List<CarSearchResult> cars = carDAO.getCarsPaging(page, pageSize, sortBy, sortOrder);
        int totalItems = carDAO.countCars();
        int totalPages = (int) Math.ceil(totalItems * 1.0 / pageSize);

        BookingLookupDAO lookupDAO = new BookingLookupDAO();

        request.setAttribute("cars", cars);
        request.setAttribute("brands", lookupDAO.getAllBrands());
        request.setAttribute("bodyStyles", lookupDAO.getAllBodyStyles());
        request.setAttribute("carTypes", lookupDAO.getAllCarTypes());
        request.setAttribute("amenities", lookupDAO.getAllAmenities());
        request.setAttribute("locations", lookupDAO.getAllLocations());
        request.setAttribute("topDrivers", new DriverProfileDAO().getTopDrivers(4));
        request.setAttribute("latestReviews", new ReviewDAO().getNewestHighRatingReviews(6));

        request.setAttribute("page", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);

        request.getRequestDispatcher("/views/home/home.jsp").forward(request, response);
    }
}

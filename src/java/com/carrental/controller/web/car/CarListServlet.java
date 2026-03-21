package com.carrental.controller.web.car;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.BookingLookupDAO;
import com.carrental.dao.CarDAO;
import com.carrental.entity.CarSearchResult;
import com.carrental.util.ValidationUtil;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CarListServlet extends BaseAuthentication {

    @Override
    protected boolean shouldCheckPermission() {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int page = ValidationUtil.normalizePage(
                ValidationUtil.parseIntOrDefault(request.getParameter("page"), IConstant.DEFAULT_PAGE));
        int pageSize = IConstant.DEFAULT_PAGE_SIZE;

        String startDateTimeRaw = request.getParameter("startDateTime");
        String endDateTimeRaw = request.getParameter("endDateTime");

        // Convert datetime format from HTML datetime-local (YYYY-MM-DDTHH:MM) to SQL Server format (YYYY-MM-DD HH:MM:SS)
        String startDateTimeSql = startDateTimeRaw;
        String endDateTimeSql = endDateTimeRaw;
        if (startDateTimeSql != null && startDateTimeSql.contains("T")) {
            startDateTimeSql = startDateTimeSql.replace("T", " ") + ":00";
        }
        if (endDateTimeSql != null && endDateTimeSql.contains("T")) {
            endDateTimeSql = endDateTimeSql.replace("T", " ") + ":00";
        }

        Integer locationID = ValidationUtil.parseIntegerOrNull(request.getParameter("pickUpLocationID"));
        List<Integer> brandIDs = ValidationUtil.parseIntegerList(request.getParameterValues("brandID"));
        List<Integer> typeIDs = ValidationUtil.parseIntegerList(request.getParameterValues("typeID"));
        List<Integer> amenityIDs = ValidationUtil.parseIntegerList(request.getParameterValues("amenityID"));

        String sortBy = IConstant.SORT_BY_PRICE;
        String sortOrder = ValidationUtil.normalizeSortOrder(request.getParameter("sortOrder"));
        String priceRange = request.getParameter("priceRange");
        java.math.BigDecimal minPrice = null;
        java.math.BigDecimal maxPrice = null;
        if ("under_500".equals(priceRange)) {
            maxPrice = new java.math.BigDecimal("500000");
        } else if ("500_1000".equals(priceRange)) {
            minPrice = new java.math.BigDecimal("500000");
            maxPrice = new java.math.BigDecimal("1000000");
        } else if ("over_1000".equals(priceRange)) {
            minPrice = new java.math.BigDecimal("1000000");
        }

        BookingLookupDAO lookupDAO = new BookingLookupDAO();
        request.setAttribute("brands", lookupDAO.getAllBrands());
        request.setAttribute("bodyStyles", lookupDAO.getAllBodyStyles());
        request.setAttribute("carTypes", lookupDAO.getAllCarTypes());
        request.setAttribute("amenities", lookupDAO.getAllAmenities());
        request.setAttribute("locations", lookupDAO.getAllLocations());

        CarDAO carDAO = new CarDAO();
        List<CarSearchResult> cars;
        int totalItems;

        boolean hasDateTimeFilter = !ValidationUtil.isBlank(startDateTimeSql) && !ValidationUtil.isBlank(endDateTimeSql);
        Integer selectedLocationID = (locationID == null ? 1 : locationID);

        // Only filter by availability (schedule + buffer) when start/end are provided.
        // If user visits /cars directly (no datetime), show all cars (do NOT filter by default location=1).
        if (hasDateTimeFilter) {
            cars = carDAO.searchAvailableCars(
                    startDateTimeSql,
                    endDateTimeSql,
                    selectedLocationID,
                    null, // carID
                    minPrice,
                    maxPrice,
                    null, // keyword
                    brandIDs,
                    typeIDs,
                    amenityIDs,
                    null, // seatCount
                    null, // transmission
                    null, // fuelType.
                    sortBy,
                    sortOrder,
                    page,
                    pageSize
            );
            totalItems = carDAO.countAvailableCars(
                    startDateTimeSql,
                    endDateTimeSql,
                    selectedLocationID,
                    null,
                    minPrice,
                    maxPrice,
                    null,
                    brandIDs,
                    typeIDs,
                    amenityIDs,
                    null,
                    null,
                    null
            );
        } else {
            cars = carDAO.searchCars(
                    null,
                    minPrice,
                    maxPrice,
                    brandIDs,
                    typeIDs,
                    amenityIDs,
                    sortBy,
                    sortOrder,
                    page,
                    pageSize
            );
            totalItems = carDAO.countCars(null, minPrice, maxPrice, brandIDs, typeIDs, amenityIDs);
        }

        int totalPages = (int) Math.ceil(totalItems * 1.0 / pageSize);

        request.setAttribute("cars", cars);
        request.setAttribute("page", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);
        request.setAttribute("priceRange", priceRange);
        request.setAttribute("startDateTime", startDateTimeRaw);
        request.setAttribute("endDateTime", endDateTimeRaw);
        request.setAttribute("pickUpLocationID", selectedLocationID);
        request.setAttribute("brandIDs", brandIDs);
        request.setAttribute("typeIDs", typeIDs);
        request.setAttribute("amenityIDs", amenityIDs);

        request.getRequestDispatcher("/views/car/car-list.jsp").forward(request, response);
    }
}

package com.carrental.controller.web.booking;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.BookingLookupDAO;
import com.carrental.dao.CarDAO;
import com.carrental.entity.Car;
import com.carrental.entity.CarSearchResult;
import com.carrental.util.ValidationUtil;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BookingSearchServlet extends BaseAuthentication {

    @Override
    protected boolean shouldCheckPermission() {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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

        int page = ValidationUtil.normalizePage(
                ValidationUtil.parseIntOrDefault(request.getParameter("page"), IConstant.DEFAULT_PAGE));
        int pageSize = IConstant.DEFAULT_PAGE_SIZE;

        String keyword = request.getParameter("keyword");
        Integer pickUpLocationID = ValidationUtil.parseIntegerOrNull(request.getParameter("pickUpLocationID"));
        Integer carID = ValidationUtil.parseIntegerOrNull(request.getParameter("carID"));

        // If carID is provided but pickUpLocationID is missing, get location from car
        if (carID != null && pickUpLocationID == null) {
            CarDAO carDAO = new CarDAO();
            Car car = carDAO.getById(carID);
            if (car != null) {
                pickUpLocationID = car.getLocationID();
            }
        }

        List<Integer> brandIDs = ValidationUtil.parseIntegerList(request.getParameterValues("brandID"));
        List<Integer> typeIDs = ValidationUtil.parseIntegerList(request.getParameterValues("typeID"));
        List<Integer> amenityIDs = ValidationUtil.parseIntegerList(request.getParameterValues("amenityID"));
        Integer seatCount = ValidationUtil.parseIntegerOrNull(request.getParameter("seatCount"));
        String transmission = request.getParameter("transmission");
        String fuelType = request.getParameter("fuelType");

        String sortBy = "price";
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

        if (ValidationUtil.isBlank(startDateTimeSql) || ValidationUtil.isBlank(endDateTimeSql) || pickUpLocationID == null) {
            request.setAttribute("error", "Vui lòng chọn thời gian và địa điểm nhận xe.");
            request.setAttribute("startDateTime", startDateTimeRaw);
            request.setAttribute("endDateTime", endDateTimeRaw);
            request.setAttribute("pickUpLocationID", pickUpLocationID == null ? 1 : pickUpLocationID);
            request.getRequestDispatcher("/views/booking/search.jsp").forward(request, response);
            return;
        }

        CarDAO carDAO = new CarDAO();
        List<CarSearchResult> cars = carDAO.searchAvailableCars(
                startDateTimeSql,
                endDateTimeSql,
                pickUpLocationID,
                carID,
                minPrice,
                maxPrice,
                keyword,
                brandIDs,
                typeIDs,
                amenityIDs,
                seatCount,
                ValidationUtil.isBlank(transmission) ? null : transmission,
                ValidationUtil.isBlank(fuelType) ? null : fuelType,
                sortBy,
                sortOrder,
                page,
                pageSize
        );

        int totalItems = carDAO.countAvailableCars(
                startDateTimeSql,
                endDateTimeSql,
                pickUpLocationID,
                carID,
                minPrice,
                maxPrice,
                keyword,
                brandIDs,
                typeIDs,
                amenityIDs,
                seatCount,
                ValidationUtil.isBlank(transmission) ? null : transmission,
                ValidationUtil.isBlank(fuelType) ? null : fuelType
        );

        int totalPages = (int) Math.ceil(totalItems * 1.0 / pageSize);

        request.setAttribute("cars", cars);
        request.setAttribute("page", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("totalPages", totalPages);

        request.setAttribute("startDateTime", startDateTimeRaw);
        request.setAttribute("endDateTime", endDateTimeRaw);
        request.setAttribute("pickUpLocationID", pickUpLocationID == null ? 1 : pickUpLocationID);
        request.setAttribute("carID", carID);
        request.setAttribute("keyword", keyword);
        request.setAttribute("brandIDs", brandIDs);
        request.setAttribute("typeIDs", typeIDs);
        request.setAttribute("amenityIDs", amenityIDs);
        request.setAttribute("seatCount", seatCount);
        request.setAttribute("transmission", transmission);
        request.setAttribute("fuelType", fuelType);
        request.setAttribute("priceRange", priceRange);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);

        request.getRequestDispatcher("/views/booking/search.jsp").forward(request, response);
    }
}

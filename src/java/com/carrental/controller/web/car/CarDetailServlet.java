package com.carrental.controller.web.car;

import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.CarDAO;
import com.carrental.dao.CarImageDAO;
import com.carrental.dao.CarScheduleDAO;
import com.carrental.entity.Car;
import com.carrental.entity.CarImage;
import com.carrental.entity.CarSchedule;
import com.carrental.util.ValidationUtil;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CarDetailServlet extends BaseAuthentication {

    @Override
    protected boolean shouldCheckPermission() {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer carID = ValidationUtil.parseIntegerOrNull(request.getParameter("id"));
        if (carID == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu hoặc sai id xe.");
            return;
        }

        Car car = new CarDAO().getById(carID);
        if (car == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy xe.");
            return;
        }

        request.setAttribute("car", car);

        // Get car images from CarImages table
        List<CarImage> carImages = new CarImageDAO().getImagesByCarId(carID);
        request.setAttribute("carImages", carImages);
        
        List<CarSchedule> schedules = new CarScheduleDAO().findActiveSchedulesByCar(carID);
        request.setAttribute("carSchedules", schedules);
        request.setAttribute("carScheduleJson", buildScheduleJson(schedules));
        
        com.carrental.dao.BookingLookupDAO lookupDAO = new com.carrental.dao.BookingLookupDAO();
        request.setAttribute("locations", lookupDAO.getAllLocations());
        
        // Get location name for display
        String locationName = "";
        if (car.getLocationID() != null) {
            var allLocations = lookupDAO.getAllLocations();
            for (var loc : allLocations) {
                if (loc.getLocationID() == car.getLocationID()) {
                    locationName = loc.getLocationName();
                    break;
                }
            }
        }
        request.setAttribute("locationName", locationName);
        
        request.setAttribute("startDateTime", request.getParameter("startDateTime"));
        request.setAttribute("endDateTime", request.getParameter("endDateTime"));
        
        // If pickUpLocationID not provided, use car's location
        Integer pickUpLocationID = ValidationUtil.parseIntegerOrNull(request.getParameter("pickUpLocationID"));
        if (pickUpLocationID == null && car.getLocationID() != null) {
            pickUpLocationID = car.getLocationID();
        }
        request.setAttribute("pickUpLocationID", pickUpLocationID);
        
        request.getRequestDispatcher("/views/car/car-detail.jsp").forward(request, response);
    }

    private String buildScheduleJson(List<CarSchedule> schedules) {
        DateTimeFormatter iso = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        StringBuilder builder = new StringBuilder("[");
        boolean first = true;
        for (CarSchedule schedule : schedules) {
            if (schedule == null) {
                continue;
            }
            String start = schedule.getStartDateTime() != null
                    ? schedule.getStartDateTime().toLocalDateTime().format(iso)
                    : "";
            String end = schedule.getEndDateTime() != null
                    ? schedule.getEndDateTime().toLocalDateTime().format(iso)
                    : "";
            String status = schedule.getScheduleStatus() != null ? schedule.getScheduleStatus() : "";

            if (!first) {
                builder.append(",");
            }
            first = false;
            builder.append("{\"start\":\"").append(escapeJson(start))
                    .append("\",\"end\":\"").append(escapeJson(end))
                    .append("\",\"status\":\"").append(escapeJson(status)).append("\"}");
        }
        builder.append("]");
        return builder.toString();
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

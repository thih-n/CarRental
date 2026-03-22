package com.carrental.controller.driver;

import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.DriverLeaveRequestDAO;
import com.carrental.dao.DriverTripDAO;
import com.carrental.entity.DriverScheduleDay;
import com.carrental.entity.StaffBookingItem;
import com.carrental.entity.User;
import com.carrental.constant.IConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverScheduleServlet extends BaseAuthentication {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        User user = (User) request.getSession().getAttribute(IConstant.SESSION_USER);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        int driverID = user.getUserID();
        
        String yearParam = request.getParameter("year");
        String monthParam = request.getParameter("month");
        
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        
        if (yearParam != null && !yearParam.isEmpty()) {
            year = Integer.parseInt(yearParam);
        }
        if (monthParam != null && !monthParam.isEmpty()) {
            month = Integer.parseInt(monthParam);
        }
        
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(year, month - 1, 1);
        
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        
        Calendar todayCal = Calendar.getInstance();
        Date today = new Date(todayCal.getTimeInMillis());
        
        DriverTripDAO tripDAO = new DriverTripDAO();
        DriverLeaveRequestDAO leaveDAO = new DriverLeaveRequestDAO();
        
        List<StaffBookingItem> monthTrips = tripDAO.getTripsByDriverForMonth(driverID, year, month);
        
        Map<String, List<DriverScheduleDay.DriverDayTrip>> tripsByDate = new HashMap<>();
        for (StaffBookingItem item : monthTrips) {
            if (item.getStartDateTime() != null) {
                String dateKey = item.getStartDateTime().toString().substring(0, 10);
                DriverScheduleDay.DriverDayTrip dayTrip = new DriverScheduleDay.DriverDayTrip();
                dayTrip.setContractID(item.getContractID());
                dayTrip.setContractCode(item.getContractCode());
                dayTrip.setCustomerName(item.getCustomerName());
                dayTrip.setCarName(item.getCarName());
                dayTrip.setStartDateTime(item.getStartDateTime());
                dayTrip.setEndDateTime(item.getEndDateTime());
                dayTrip.setDetailStatus(item.getDetailStatus());
                dayTrip.setScheduleStatus(item.getScheduleStatus());
                
                tripsByDate.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(dayTrip);
            }
        }
        
        List<DriverScheduleDay> days = new ArrayList<>();
        
        for (int d = 1; d <= daysInMonth; d++) {
            cal.set(year, month - 1, d);
            Date date = new Date(cal.getTimeInMillis());
            
            DriverScheduleDay day = new DriverScheduleDay(date);
            
            String dateKey = date.toString();
            if (tripsByDate.containsKey(dateKey)) {
                day.setTrips(tripsByDate.get(dateKey));
            }
            
            if (date.equals(today)) {
                day.setToday(true);
            }
            
            days.add(day);
        }
        
        request.setAttribute("year", year);
        request.setAttribute("month", month);
        request.setAttribute("days", days);
        request.setAttribute("daysInMonth", daysInMonth);
        request.setAttribute("firstDayOfWeek", firstDayOfWeek);
        
        request.getRequestDispatcher("/views/driver/schedule.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        User user = (User) request.getSession().getAttribute(IConstant.SESSION_USER);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        int driverID = user.getUserID();
        String action = request.getParameter("action");
        
        if ("create_leave".equals(action)) {
            handleCreateLeave(request, response, driverID);
            return;
        }
        
        if ("cancel_leave".equals(action)) {
            handleCancelLeave(request, response, driverID);
            return;
        }
        
        response.sendRedirect(request.getContextPath() + "/driver/schedule");
    }
    
    private void handleCancelLeave(HttpServletRequest request, HttpServletResponse response, int driverID)
            throws IOException {
        
        try {
            int leaveID = Integer.parseInt(request.getParameter("leaveID"));
            
            DriverLeaveRequestDAO leaveDAO = new DriverLeaveRequestDAO();
            boolean success = leaveDAO.cancelLeave(leaveID, driverID);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/driver/requests?cancelled=1");
            } else {
                response.sendRedirect(request.getContextPath() + "/driver/requests?error=cancel_failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/driver/requests?error=cancel_failed");
        }
    }
    
    private void handleCreateLeave(HttpServletRequest request, HttpServletResponse response, int driverID)
            throws IOException {
        
        try {
            String leaveType = request.getParameter("leaveType");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            String reason = request.getParameter("reason");
            
            if (startDate == null || startDate.isEmpty() || reason == null || reason.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/driver/schedule?error=missing_fields");
                return;
            }
            
            Timestamp leaveStart;
            Timestamp leaveEnd;
            
            if ("single".equals(leaveType)) {
                leaveStart = Timestamp.valueOf(startDate + " 00:00:00");
                leaveEnd = Timestamp.valueOf(startDate + " 23:59:59");
            } else if ("range".equals(leaveType)) {
                if (endDate == null || endDate.isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/driver/schedule?error=end_date_required");
                    return;
                }
                leaveStart = Timestamp.valueOf(startDate + " 00:00:00");
                leaveEnd = Timestamp.valueOf(endDate + " 23:59:59");
            } else {
                leaveStart = Timestamp.valueOf(startDate + " 00:00:00");
                leaveEnd = Timestamp.valueOf(endDate != null ? endDate + " 23:59:59" : startDate + " 23:59:59");
            }
            
            if (leaveStart.after(leaveEnd)) {
                response.sendRedirect(request.getContextPath() + "/driver/schedule?error=invalid_dates");
                return;
            }
            
            DriverLeaveRequestDAO leaveDAO = new DriverLeaveRequestDAO();
            List<DriverLeaveRequestDAO.DriverLeaveRequest> conflicts = leaveDAO.getConflictingLeaves(driverID, leaveStart, leaveEnd);
            
            if (!conflicts.isEmpty()) {
                request.getSession().setAttribute("leaveConflicts", conflicts);
                response.sendRedirect(request.getContextPath() + "/driver/schedule?error=conflict");
                return;
            }
            
            boolean success = leaveDAO.createLeaveRequest(driverID, leaveStart, leaveEnd, reason);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/driver/schedule?success=1");
            } else {
                response.sendRedirect(request.getContextPath() + "/driver/schedule?error=create_failed");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/driver/schedule?error=invalid_date");
        }
    }
}

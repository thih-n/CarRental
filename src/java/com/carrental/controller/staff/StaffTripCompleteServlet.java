package com.carrental.controller.staff;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.CarDAO;
import com.carrental.dao.CarScheduleDAO;
import com.carrental.dao.ContractDAO;
import com.carrental.dao.ContractDetailDAO;
import com.carrental.dao.DriverProfileDAO;
import com.carrental.dao.StaffBookingDAO;
import com.carrental.entity.ContractReturnInfo;
import com.carrental.entity.User;
import com.carrental.util.ValidationUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StaffTripCompleteServlet extends BaseAuthentication {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer contractID = ValidationUtil.parseIntegerOrNull(request.getParameter("contractID"));
        Integer driverID = ValidationUtil.parseIntegerOrNull(request.getParameter("driverID"));
        if (contractID == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu contractID");
            return;
        }

        StaffBookingDAO staffDAO = new StaffBookingDAO();
        if (!staffDAO.canCompleteTrip(contractID)) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings?error=invalid_state");
            return;
        }

        String staffNote = request.getParameter("staffNote");
        
        // Complete trip with note
        boolean tripCompleted = staffDAO.completeTrip(contractID, staffNote);

        boolean driverReleased = true;
        if (driverID != null) {
            driverReleased = new DriverProfileDAO().updateDriverStatus(driverID, "Available");
        }

        Integer staffID = null;
        Object sessionUser = request.getSession().getAttribute(IConstant.SESSION_USER);
        if (sessionUser instanceof User) {
            staffID = ((User) sessionUser).getUserID();
        }

        Integer dropOffLocationID = ValidationUtil.parseIntegerOrNull(request.getParameter("dropOffLocationID"));

        String returnCondition = request.getParameter("returnCondition");
        if (ValidationUtil.isBlank(returnCondition) || dropOffLocationID == null) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings?error=return_condition_required");
            return;
        }

        boolean contractUpdated = new ContractDAO().updateReturnInfo(
                contractID,
                staffID,
                returnCondition.trim(),
                dropOffLocationID
        );

        boolean carUpdated = true;
        ContractReturnInfo returnInfo = new ContractDAO().getReturnInfo(contractID);
        if (returnInfo != null) {
            Integer targetLocation = dropOffLocationID;
            carUpdated = new CarDAO().updateCarLocationAndStatus(
                    returnInfo.getCarID(),
                    targetLocation,
                    1
            );
        }

        if (!tripCompleted || !driverReleased || !carUpdated || !contractUpdated) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings?error=complete_failed");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/staff/bookings?completed=1");
    }
}

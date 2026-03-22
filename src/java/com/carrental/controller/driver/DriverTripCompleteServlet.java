package com.carrental.controller.driver;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.CarScheduleDAO;
import com.carrental.dao.ContractDetailDAO;
import com.carrental.dao.DriverProfileDAO;
import com.carrental.dao.DriverTripDAO;
import com.carrental.entity.User;
import com.carrental.util.ValidationUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DriverTripCompleteServlet extends BaseAuthentication {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute(IConstant.SESSION_USER);
        if (user == null || user.getRoleID() != IConstant.ROLE_ID_DRIVER) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Integer contractID = ValidationUtil.parseIntegerOrNull(request.getParameter("contractID"));
        if (contractID == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu contractID");
            return;
        }

        DriverTripDAO driverTripDAO = new DriverTripDAO();
        if (!driverTripDAO.canCompleteTrip(contractID, user.getUserID())) {
            response.sendRedirect(request.getContextPath() + "/driver/trips?error=invalid_state");
            return;
        }

        boolean detailUpdated = new ContractDetailDAO().updateDetailStatus(contractID, "Completed", "Completed");
        boolean scheduleUpdated = new CarScheduleDAO().updateScheduleStatusByContract(contractID, "Completed");
        boolean driverUpdated = new DriverProfileDAO().updateDriverStatus(user.getUserID(), "Available");

        if (!detailUpdated || !scheduleUpdated || !driverUpdated) {
            response.sendRedirect(request.getContextPath() + "/driver/trips?error=complete_failed");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/driver/trips?completed=1");
    }
}

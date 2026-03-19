package com.carrental.controller.staff;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.CarScheduleDAO;
import com.carrental.dao.ContractDAO;
import com.carrental.dao.ContractDetailDAO;
import com.carrental.dao.StaffBookingDAO;
import com.carrental.dao.UserDAO;
import com.carrental.util.ValidationUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StaffTripStartServlet extends BaseAuthentication {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer contractID = ValidationUtil.parseIntegerOrNull(request.getParameter("contractID"));
        Integer customerID = ValidationUtil.parseIntegerOrNull(request.getParameter("customerID"));

        if (contractID == null || customerID == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu contractID/customerID");
            return;
        }

        StaffBookingDAO staffDAO = new StaffBookingDAO();
        if (!staffDAO.canStartTrip(contractID)) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings?error=invalid_state");
            return;
        }

        // Only require KYC if no driver is selected
        boolean needsKyc = staffDAO.needsKycForTrip(contractID);
        
        if (needsKyc) {
            String identityCardNumber = request.getParameter("identityCardNumber");
            String licenseImageFrontUrl = request.getParameter("licenseImageFrontUrl");
            String licenseImageBackUrl = request.getParameter("licenseImageBackUrl");

            if (ValidationUtil.isBlank(identityCardNumber)
                    || ValidationUtil.isBlank(licenseImageFrontUrl)
                    || ValidationUtil.isBlank(licenseImageBackUrl)) {
                response.sendRedirect(request.getContextPath() + "/staff/bookings?error=kyc_required");
                return;
            }

            boolean kycUpdated = new UserDAO().updateProfileKyc(
                    customerID,
                    identityCardNumber.trim(),
                    licenseImageFrontUrl.trim(),
                    licenseImageBackUrl.trim()
            );

            if (!kycUpdated) {
                response.sendRedirect(request.getContextPath() + "/staff/bookings?error=kyc_update_failed");
                return;
            }
        }

        Integer staffID = null;
        Object sessionUser = request.getSession().getAttribute(IConstant.SESSION_USER);
        if (sessionUser instanceof com.carrental.entity.User) {
            staffID = ((com.carrental.entity.User) sessionUser).getUserID();
        }

        boolean staffUpdated = new ContractDAO().updateStartStaff(contractID, staffID);
        if (!staffUpdated) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings?error=start_failed");
            return;
        }

        boolean detailUpdated = new ContractDetailDAO().updateDetailStatusOnly(contractID, "InUse");
        if (!detailUpdated) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings?error=detail_update_failed");
            return;
        }

        boolean scheduleUpdated = new CarScheduleDAO().updateScheduleStatusByContract(contractID, IConstant.SCHEDULE_IN_PROGRESS);
        if (!scheduleUpdated) {
            response.sendRedirect(request.getContextPath() + "/staff/bookings?error=schedule_update_failed");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/staff/bookings?started=1");
    }

}

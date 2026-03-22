package com.carrental.controller.web.booking;

import com.carrental.constant.IConstant;
import com.carrental.controller.web.auth.BaseAuthentication;
import com.carrental.dao.BookingDAO;
import com.carrental.dao.BookingLookupDAO;
import com.carrental.dao.CarDAO;
import com.carrental.dao.CarHoldingDAO;
import com.carrental.dao.DriverProfileDAO;
import com.carrental.entity.BookingPriceResult;
import com.carrental.entity.Car;
import com.carrental.entity.CarHolding;
import com.carrental.entity.CarSearchResult;
import com.carrental.entity.DriverProfile;
import com.carrental.entity.Location;
import com.carrental.entity.CarSchedule;
import com.carrental.entity.Contract;
import com.carrental.entity.ContractDetail;
import com.carrental.entity.Payment;
import com.carrental.entity.User;
import com.carrental.service.BookingPricingService;
import com.carrental.util.ValidationUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BookingCheckoutServlet extends BaseAuthentication {

    @Override
    protected boolean shouldCheckPermission() {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer carID = ValidationUtil.parseIntegerOrNull(request.getParameter("carID"));
        Integer pickUpLocationID = ValidationUtil.parseIntegerOrNull(request.getParameter("pickUpLocationID"));
        Integer driverID = ValidationUtil.parseIntegerOrNull(request.getParameter("driverID"));
        String startDateTime = request.getParameter("startDateTime");
        String endDateTime = request.getParameter("endDateTime");

        // Convert datetime format from HTML datetime-local (YYYY-MM-DDTHH:MM) to SQL Server format (YYYY-MM-DD HH:MM:SS)
        if (startDateTime != null && startDateTime.contains("T")) {
            startDateTime = startDateTime.replace("T", " ") + ":00";
        }
        if (endDateTime != null && endDateTime.contains("T")) {
            endDateTime = endDateTime.replace("T", " ") + ":00";
        }

        if (carID == null || pickUpLocationID == null || ValidationUtil.isBlank(startDateTime)
                || ValidationUtil.isBlank(endDateTime)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu dữ liệu đặt xe.");
            return;
        }

        // Get car info
        Car car = new CarDAO().getById(carID);
        if (car == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy xe.");
            return;
        }

        // Get location info
        BookingLookupDAO lookupDAO = new BookingLookupDAO();
        Location pickUpLocation = null;
        Location dropOffLocation = null;
        for (Location loc : lookupDAO.getAllLocations()) {
            if (loc.getLocationID() == pickUpLocationID) {
                pickUpLocation = loc;
            }
        }

        // Get driver info if selected
        DriverProfile driver = null;
        if (driverID != null) {
            DriverProfileDAO driverDAO = new DriverProfileDAO();
            driver = driverDAO.getById(driverID);
        }

        // Validate holding exists and is valid
        CarHoldingDAO holdingDAO = new CarHoldingDAO();
        holdingDAO.deleteExpiredHoldings();
        
        User user = (User) request.getSession().getAttribute(IConstant.SESSION_USER);
        CarHolding holding = null;
        if (user != null) {
            holding = holdingDAO.getHoldingByCarAndUser(carID, user.getUserID());
        }
        
        if (holding == null) {
            // No holding found, redirect back to drivers
            response.sendRedirect(request.getContextPath() + "/booking/drivers?carID=" + carID 
                + "&pickUpLocationID=" + pickUpLocationID + "&startDateTime=" + startDateTime 
                + "&endDateTime=" + endDateTime + "&error=Holding expired");
            return;
        }

        // Calculate pricing
        BookingPriceResult pricing = new BookingPricingService().calculate(carID, driverID, startDateTime, endDateTime);

        // Set attributes for JSP
        request.setAttribute("car", car);
        request.setAttribute("pickUpLocation", pickUpLocation);
        request.setAttribute("dropOffLocation", pickUpLocation);
        request.setAttribute("driver", driver);
        request.setAttribute("startDateTime", startDateTime);
        request.setAttribute("endDateTime", endDateTime);
        request.setAttribute("rentPrice", pricing.getRentPrice());
        request.setAttribute("driverFee", pricing.getDriverFee());
        request.setAttribute("totalAmount", pricing.getTotalAmount());
        request.setAttribute("depositAmount", pricing.getDepositAmount());
        request.setAttribute("holdingExpiry", holding.getExpiryTime());
        request.setAttribute("holdingID", holding.getHoldingID());

        request.getRequestDispatcher("/views/booking/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer carID = ValidationUtil.parseIntegerOrNull(request.getParameter("carID"));
        Integer pickUpLocationID = ValidationUtil.parseIntegerOrNull(request.getParameter("pickUpLocationID"));
        Integer dropOffLocationID = ValidationUtil.parseIntegerOrNull(request.getParameter("dropOffLocationID"));
        Integer driverID = ValidationUtil.parseIntegerOrNull(request.getParameter("driverID"));

        String startDateTimeStr = request.getParameter("startDateTime");
        String endDateTimeStr = request.getParameter("endDateTime");
        String paymentMethod = request.getParameter("paymentMethod");
        String notes = request.getParameter("notes");

        if (carID == null || pickUpLocationID == null || ValidationUtil.isBlank(startDateTimeStr)
                || ValidationUtil.isBlank(endDateTimeStr)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu dữ liệu đặt xe.");
            return;
        }

        User user = (User) request.getSession().getAttribute(IConstant.SESSION_USER);

        Timestamp startDateTime = ValidationUtil.parseTimestampOrNull(startDateTimeStr);
        Timestamp endDateTime = ValidationUtil.parseTimestampOrNull(endDateTimeStr);

        BookingPriceResult pricing = new BookingPricingService().calculate(carID, driverID, startDateTimeStr, endDateTimeStr);
        BigDecimal rentPrice = pricing.getRentPrice();
        BigDecimal driverFee = pricing.getDriverFee();
        BigDecimal totalAmount = pricing.getTotalAmount();
        BigDecimal depositAmount = pricing.getDepositAmount();

        if (user == null || startDateTime == null || endDateTime == null || rentPrice == null || totalAmount == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dữ liệu thời gian/chi phí không hợp lệ.");
            return;
        }

        Contract contract = new Contract();
        contract.setContractCode("CT" + System.currentTimeMillis());
        contract.setCustomerID(user.getUserID());
        contract.setStaffID(null);
        contract.setPickUpLocationID(pickUpLocationID);
        contract.setDropOffLocationID(dropOffLocationID == null ? pickUpLocationID : dropOffLocationID);
        contract.setStartDateTime(startDateTime);
        contract.setEndDateTime(endDateTime);
        contract.setTotalAmount(totalAmount);
        contract.setDepositAmount(depositAmount);
        contract.setCancelReason(null);
        contract.setCancelledBy(null);
        contract.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        contract.setNotes(notes);
        contract.setStatusID(1);

        ContractDetail detail = new ContractDetail();
        detail.setCarID(carID);
        detail.setHasDriver(driverID != null);
        detail.setDriverID(driverID);
        detail.setRentPrice(rentPrice);
        detail.setDriverFee(driverFee);
        detail.setDetailStatus("Booked");
        detail.setDriverStatus(driverID == null ? null : "Assigned");

        CarSchedule schedule = new CarSchedule();
        schedule.setCarID(carID);
        schedule.setStartDateTime(startDateTime);
        schedule.setEndDateTime(endDateTime);
        schedule.setScheduleStatus("Booked");
        schedule.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        Payment payment = null;
        if (depositAmount != null && depositAmount.compareTo(BigDecimal.ZERO) > 0) {
            payment = new Payment();
            payment.setAmount(depositAmount);
            payment.setPaymentMethod(paymentMethod != null && !paymentMethod.isEmpty() ? paymentMethod : "VNPay");
            payment.setPaymentType("Deposit");
            payment.setPaymentStatus("Completed");
            payment.setNote("Khách hàng thanh toán trước 30% cọc trực tuyến");
        }

        int contractID = new BookingDAO().createBookingTransaction(contract, detail, schedule, driverID, payment);
        if (contractID <= 0) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể tạo booking.");
            return;
        }

        // Delete holding after successful booking
        Integer holdingID = ValidationUtil.parseIntegerOrNull(request.getParameter("holdingID"));
        if (holdingID != null) {
            CarHoldingDAO holdingDAO = new CarHoldingDAO();
            holdingDAO.deleteHolding(holdingID);
        }

        response.sendRedirect(request.getContextPath() + "/booking/success?contractId=" + contractID);
    }
}

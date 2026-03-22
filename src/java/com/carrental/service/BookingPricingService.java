package com.carrental.service;

import com.carrental.dao.CarDAO;
import com.carrental.dao.DriverProfileDAO;
import com.carrental.entity.BookingPriceResult;
import com.carrental.entity.Car;
import com.carrental.entity.DriverProfile;
import com.carrental.util.ValidationUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Duration;

public class BookingPricingService {

    public BookingPriceResult calculate(Integer carID, Integer driverID, String startDateTimeStr, String endDateTimeStr) {
        BookingPriceResult result = new BookingPriceResult();
        Timestamp startDateTime = ValidationUtil.parseTimestampOrNull(startDateTimeStr);
        Timestamp endDateTime = ValidationUtil.parseTimestampOrNull(endDateTimeStr);
        result.setStartDateTime(startDateTime);
        result.setEndDateTime(endDateTime);

        if (carID == null || startDateTime == null || endDateTime == null) {
            return result;
        }

        Car car = new CarDAO().getById(carID);
        if (car == null || car.getDefaultPricePerDay() == null) {
            return result;
        }

        BigDecimal dailyRate = car.getDefaultPricePerDay();
        BigDecimal rentPrice = calculateCarRent(dailyRate, startDateTime, endDateTime);

        BigDecimal driverFee = BigDecimal.ZERO;
        if (driverID != null) {
            DriverProfile driver = new DriverProfileDAO().getById(driverID);
            if (driver != null && driver.getDailyRate() != null && driver.getHourlyRate() != null) {
                driverFee = calculateDriverFee(driver.getDailyRate(), driver.getHourlyRate(), startDateTime, endDateTime);
            }
        }

        BigDecimal totalAmount = rentPrice.add(driverFee);
        BigDecimal depositAmount = totalAmount.multiply(new BigDecimal("0.2")).setScale(2, RoundingMode.HALF_UP);

        result.setRentPrice(rentPrice);
        result.setDriverFee(driverFee);
        result.setTotalAmount(totalAmount);
        result.setDepositAmount(depositAmount);
        return result;
    }

    private BigDecimal calculateCarRent(BigDecimal dailyRate, Timestamp start, Timestamp end) {
        long hours = Duration.between(start.toInstant(), end.toInstant()).toHours();
        long days = hours / 24;
        long extraHours = hours % 24;
        BigDecimal rent = dailyRate.multiply(BigDecimal.valueOf(days));
        if (extraHours > 0) {
            BigDecimal extra = dailyRate.multiply(BigDecimal.valueOf(extraHours))
                    .divide(BigDecimal.valueOf(24), 2, RoundingMode.HALF_UP);
            rent = rent.add(extra);
        }
        return rent.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDriverFee(BigDecimal dailyRate, BigDecimal hourlyRate, Timestamp start, Timestamp end) {
        long hours = Duration.between(start.toInstant(), end.toInstant()).toHours();
        long days = hours / 24;
        long extraHours = hours % 24;

        BigDecimal fee = dailyRate.multiply(BigDecimal.valueOf(days));
        if (extraHours > 0) {
            BigDecimal extra = hourlyRate.multiply(BigDecimal.valueOf(extraHours));
            if (extra.compareTo(dailyRate) > 0) {
                extra = dailyRate; // giờ lẻ không vượt quá 1 ngày
            }
            fee = fee.add(extra);
        }
        return fee.setScale(2, RoundingMode.HALF_UP);
    }
}

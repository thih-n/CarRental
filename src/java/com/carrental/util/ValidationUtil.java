package com.carrental.util;

import com.carrental.constant.IConstant;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class ValidationUtil {

    private ValidationUtil() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static int parseIntOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Integer parseIntegerOrNull(String value) {
        try {
            return value == null || value.trim().isEmpty() ? null : Integer.parseInt(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public static BigDecimal parseBigDecimalOrDefault(String value, BigDecimal defaultValue) {
        try {
            return isBlank(value) ? defaultValue : new BigDecimal(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Timestamp parseTimestampOrNull(String value) {
        try {
            if (isBlank(value)) {
                return null;
            }
            String input = value.trim();
            if (input.contains("T")) {
                input = input.replace("T", " ");
                if (input.length() == 16) {
                    input += ":00";
                }
            }
            return Timestamp.valueOf(input);
        } catch (Exception e) {
            return null;
        }
    }

    public static String normalizeSortOrder(String value) {
        if (IConstant.ORDER_DESC.equalsIgnoreCase(value)) {
            return IConstant.ORDER_DESC;
        }
        return IConstant.ORDER_ASC;
    }

    public static String normalizeSortBy(String sortBy) {
        if (IConstant.SORT_BY_NAME.equalsIgnoreCase(sortBy)) {
            return IConstant.SORT_BY_NAME;
        }
        if (IConstant.SORT_BY_YEAR.equalsIgnoreCase(sortBy)) {
            return IConstant.SORT_BY_YEAR;
        }
        return IConstant.SORT_BY_PRICE;
    }

    public static int normalizePage(int page) {
        return Math.max(1, page);
    }

    public static int normalizePageSize(int pageSize, int maxPageSize, int defaultPageSize) {
        if (pageSize <= 0) {
            return defaultPageSize;
        }
        return Math.min(pageSize, maxPageSize);
    }

    public static java.util.List<Integer> parseIntegerList(String[] values) {
        java.util.List<Integer> result = new java.util.ArrayList<>();
        if (values == null) {
            return result;
        }
        for (String value : values) {
            Integer parsed = parseIntegerOrNull(value);
            if (parsed != null) {
                result.add(parsed);
            }
        }
        return result;
    }

    public static boolean isValidPhone(String phone) {
        if (isBlank(phone)) {
            return true;
        }
        return phone.trim().matches("^[0-9+\\-\\s]{8,15}$");
    }

    public static boolean isValidPassword(String password) {
        if (isBlank(password)) {
            return false;
        }
        return password.length() >= 6;
    }

    public static boolean isImageContentType(String contentType) {
        if (isBlank(contentType)) {
            return false;
        }
        String value = contentType.toLowerCase();
        return value.equals("image/jpeg")
                || value.equals("image/jpg")
                || value.equals("image/png")
                || value.equals("image/webp");
    }

    public static String safeFileExtension(String fileName) {
        if (isBlank(fileName) || !fileName.contains(".")) {
            return ".bin";
        }
        String ext = fileName.substring(fileName.lastIndexOf('.')).toLowerCase();
        if (ext.matches("\\.(jpg|jpeg|png|webp)")) {
            return ext;
        }
        return ".bin";
    }
}

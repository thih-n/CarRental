package com.carrental.constant;

public interface IConstant {
    String SESSION_USER = "USER_SESSION";
    String SESSION_MESSAGE = "SESSION_MESSAGE";
    String SESSION_CURRENT_TOKEN = "CURRENT_TOKEN";

    String REQUEST_ERROR = "error";
    String REQUEST_SUCCESS = "success";

    String ROLE_ADMIN = "Admin";
    String ROLE_STAFF = "Staff";
    String ROLE_DRIVER = "Driver";
    String ROLE_CUSTOMER = "Customer";

    int ROLE_ID_ADMIN = 1;
    int ROLE_ID_STAFF = 3;
    int ROLE_ID_DRIVER = 4;
    int ROLE_ID_CUSTOMER = 5;

    String ORDER_ASC = "asc";
    String ORDER_DESC = "desc";

    String SORT_BY_PRICE = "price";
    String SORT_BY_NAME = "name";
    String SORT_BY_YEAR = "year";

    String SCHEDULE_BOOKED = "Booked";
    String SCHEDULE_IN_PROGRESS = "InProgress";
    String SCHEDULE_MAINTENANCE = "Maintenance";

    String DRIVER_AVAILABLE = "Available";
    String DRIVER_BOOKED = "Booked";

    int DEFAULT_PAGE = 1;
    int DEFAULT_PAGE_SIZE = 9;
    int MAX_PAGE_SIZE = 50;
}

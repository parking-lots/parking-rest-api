package parking.helper;

public enum ExceptionMessage {
    USER_ALREADY_LOGGED("user_already_logged"),
    USER_ALREADY_EXIST("user_already_exist"),
    WRONG_CREDENTIALS("wrong_credentials"),
    PARKING_OWNED_BY_ANOTHER("parking_owned_by_another"),
    PARKING_NOT_AVAILABLE("parking_not_available"),
    USER_NOT_FOUND("user_not_found"),
    PARKING_ALREADY_EXISTS("parking_already_exists"),
    PARKING_DOES_NOT_EXIST("parking_does_not_exist"),
    NOT_LOGGED("not_logged"),
    NO_COOKIE_DATA("no_cookie_data"),
    END_DATE_IN_THE_PAST("end_date_in_the_past"),
    START_DATE_LATER_THAN_END_DATE("start_date_later_than_end_date"),
    DATE_IN_THE_PAST("date_in_the_past"),
    DATE_DOES_NOT_EXIST("date_does_not_exist"),
    OVERLAPPING_PERIOD("overlapping_period"),
    DUBLICATE_DATES("dublicate_dates"),
    DOES_NOT_HAVE_PARKING("does_not_have_parking"),
    EMPTY_CAR_REG_NO("empty_car_reg_no"),
    COULD_NOT_SEND_EMAIL("could_not_send_email"),
    USER_INACTIVE("user_inactive");

    private String msg;

    ExceptionMessage(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}

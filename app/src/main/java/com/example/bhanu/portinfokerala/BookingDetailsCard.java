package com.example.bhanu.portinfokerala;

public class BookingDetailsCard {



    int index;
    String booking_id;
    String booking_date;
    String booking_quantity;
    String booking_request_method;
    String status;

    public BookingDetailsCard() {

    }

    public BookingDetailsCard(int index, String booking_id, String booking_date, String booking_quantity, String booking_request_method) {

        this.index = index;
        this.booking_id = booking_id;
        this.booking_date = booking_date;
        this.booking_quantity = booking_quantity;
        this.booking_request_method = booking_request_method;

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public String getBooking_quantity() {
        return booking_quantity;
    }

    public void setBooking_quantity(String booking_quantity) {
        this.booking_quantity = booking_quantity;
    }

    public String getBooking_request_method() {
        return booking_request_method;
    }

    public void setBooking_request_method(String booking_request_method) {
        this.booking_request_method = booking_request_method;
    }
}

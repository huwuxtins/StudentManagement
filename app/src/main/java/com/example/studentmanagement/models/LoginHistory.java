package com.example.studentmanagement.models;

import java.io.Serializable;
import java.util.Date;

public class LoginHistory implements Serializable {

    private String phoneNumber;
    private String dateTime;

    public LoginHistory() {
    }

    public LoginHistory(String phoneNumber, String dateTime) {
        this.phoneNumber = phoneNumber;
        this.dateTime = dateTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}

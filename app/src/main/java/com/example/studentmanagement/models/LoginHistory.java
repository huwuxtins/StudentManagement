package com.example.studentmanagement.models;

import java.util.Date;

public class LoginHistory {
    private Date loginDate;
    private String email;

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LoginHistory(){}
}

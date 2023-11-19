package com.example.studentmanagement.models;

public class Role {

    private String Phonenumber;
    private String Rolename;

    public Role() {
    }

    public Role(String phonenumber, String rolename) {
        Phonenumber = phonenumber;
        Rolename = rolename;
    }

    public String getPhonenumber() {
        return Phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        Phonenumber = phonenumber;
    }

    public String getRolename() {
        return Rolename;
    }

    public void setRolename(String rolename) {
        Rolename = rolename;
    }
}

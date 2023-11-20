package com.example.studentmanagement.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

@IgnoreExtraProperties
@SuppressWarnings("serial")
public class User implements Serializable {
    private int age;
    private String name;
    private String phoneNumber;
    private Boolean locked;
    private String pictureLink;

    private String role;

    public User(){}

    public User(int age, String name, String phonenumber, Boolean isLocked, String pictureLink, String role) {
        this.age = age;
        this.name = name;
        this.phoneNumber = phonenumber;
        this.locked = isLocked;
        this.pictureLink = pictureLink;
        this.role = role;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("age", this.age);
        result.put("name", this.name);
        result.put("phoneNumber", this.phoneNumber);
        result.put("locked",this.locked);
        result.put("pictureLink",this.pictureLink);
        result.put("role",this.role);
        return result;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

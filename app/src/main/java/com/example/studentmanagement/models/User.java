package com.example.studentmanagement.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    private int Age;
    private String Name;
    private String Phonenumber;
    private Boolean isLocked;
    private String pictureLink;

    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getPhoneNumber() {
        return Phonenumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.Phonenumber = phoneNumber;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        this.Age = age;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }

    public User(){}

    public User(int age, String name, String phonenumber, Boolean isLocked, String pictureLink, String role) {
        Age = age;
        Name = name;
        Phonenumber = phonenumber;
        this.isLocked = isLocked;
        this.pictureLink = pictureLink;
        this.role = role;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Age", this.Age);
        result.put("Name", this.Name);
        result.put("Phonenumber", this.Phonenumber);
        result.put("locked",this.isLocked);
        result.put("pictureLink",this.pictureLink);
        result.put("role",this.role);
        return result;
    }
}

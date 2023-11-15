package com.example.studentmanagement.models;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private int Age;
    private String Name;
    private String Phonenumber;
    private Boolean isLocked;
    private String pictureLink;

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

    public User(int age, String name, String phone_number, Boolean isLocked, String pictureLink) {
        Age = age;
        Name = name;
        Phonenumber = phone_number;
        this.isLocked = isLocked;
        this.pictureLink = pictureLink;
    }
}

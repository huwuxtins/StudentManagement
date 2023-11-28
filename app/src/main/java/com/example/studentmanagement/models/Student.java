package com.example.studentmanagement.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Student implements Serializable {
    public String id;
    public String name;
    public Date bod;
    public String className;
    public String gender;
    public String phoneNumber;
    public String faculties;
    public String pictureLink;
    public ArrayList<Certificate> certificates;

    public Student(){}

    public Student(String name, Date bod, String className,
                   String gender, String phoneNumber, String faculties,
                   String pictureLink, ArrayList<Certificate> certificates) {
        this.name = name;
        this.bod = bod;
        this.className = className;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.faculties = faculties;
        this.pictureLink = pictureLink;
        this.certificates = certificates;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBod() {
        return bod;
    }

    public void setBod(Date bod) {
        this.bod = bod;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFaculties() {
        return faculties;
    }

    public void setFaculties(String faculties) {
        this.faculties = faculties;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }

    public ArrayList<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(ArrayList<Certificate> certificates) {
        this.certificates = certificates;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("bod", bod);
        result.put("className", className);
        result.put("gender", gender);
        result.put("phoneNumber", phoneNumber);
        result.put("faculties", faculties);
        result.put("pictureLink", pictureLink);
        result.put("certificates", certificates);
        return result;
    }
}

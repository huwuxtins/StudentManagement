package com.example.studentmanagement.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Certificate implements Serializable {
    public String name;
    public Date createdAt;
    public Date expiredAt;
    public Double score;
    public String pictureLink;
    public Certificate(){}

    public Certificate(String name, Date createdAt, Date expiredAt, Double score, String pictureLink) {
        this.name = name;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.score = score;
        this.pictureLink = pictureLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("createdAt", createdAt);
        result.put("expiredAt", expiredAt);
        result.put("score", score);
        result.put("pictureLink", pictureLink);
        return result;
    }
}

package com.example.studentmanagement.models;

public class UserSelect {

    private Boolean selected = false;

    private User user;

    public UserSelect() {
    }

    public UserSelect(Boolean selected, User user) {
        this.selected = selected;
        this.user = user;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getSelected() {
        return selected;
    }

    public User getUser() {
        return user;
    }
}

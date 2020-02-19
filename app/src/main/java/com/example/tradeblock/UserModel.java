package com.example.tradeblock;

public class UserModel {
    private final String displayName;
    private final String email;
    private final String password;

    UserModel() {
        this.displayName = "";
        this.email = "";
        this.password = "";
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

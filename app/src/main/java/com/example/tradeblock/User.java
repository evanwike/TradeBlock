package com.example.tradeblock;

public class User {
    private String displayName;
    private String email;
    private String password;

    User() {
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

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

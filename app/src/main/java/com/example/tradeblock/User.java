package com.example.tradeblock;

public class User {
    private String username;
    private String email;
    private String password;

    User() {
        this.username = "";
        this.email = "";
        this.password = "";
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

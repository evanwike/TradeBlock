package com.example.tradeblock;

import android.net.Uri;

public class User {
    private Uri imageURL;
    private String displayName;
    private String email;
    private String password;
    private String confirmPassword;

    User() {
        this.imageURL = Uri.parse("");
        this.displayName = "";
        this.email = "";
        this.password = "";
        this.confirmPassword = "";
    }

//    public Uri getImageURL() {
//        return imageURL;
//    }

    public String getImageURL() {
        return imageURL.toString();
    }

    public void setImageURL(String imageURL) {
        this.imageURL = Uri.parse(imageURL);
    }

//    public void setImageURL(Uri imageURL) {
//        this.imageURL = imageURL;
//    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}

package com.example.tradeblock.updateprofile;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UpdateModel extends ViewModel {
    private MutableLiveData<Uri> imageUri;
    private MutableLiveData<String> displayName;
    private MutableLiveData<String> email;
    private MutableLiveData<String> password;
    private MutableLiveData<String> confirmPassword;

    public MutableLiveData<Uri> getImageUri() {
        return imageUri;
    }

    public void setImageUri(MutableLiveData<Uri> imageUri) {
        this.imageUri = imageUri;
    }

    public MutableLiveData<String> getDisplayName() {
        return displayName;
    }

    public void setDisplayName(MutableLiveData<String> displayName) {
        this.displayName = displayName;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public void setEmail(MutableLiveData<String> email) {
        this.email = email;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(MutableLiveData<String> password) {
        this.password = password;
    }

    public MutableLiveData<String> getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(MutableLiveData<String> confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}

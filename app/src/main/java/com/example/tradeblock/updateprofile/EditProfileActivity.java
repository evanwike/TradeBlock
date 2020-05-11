package com.example.tradeblock.updateprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.example.tradeblock.R;
import com.example.tradeblock.User;
import com.example.tradeblock.databinding.ActivityAccountDetailsBinding;
import com.firebase.ui.auth.util.ui.fieldvalidators.EmailFieldValidator;
import com.firebase.ui.auth.util.ui.fieldvalidators.PasswordFieldValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "AccountDetailsActivity";

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    User userModel = new User();
    TextInputLayout imageLayout, displayNameLayout, emailLayout, passwordLayout, confirmPasswordLayout;
    EmailFieldValidator emailFieldValidator;
    PasswordFieldValidator passwordFieldValidator;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        ActivityAccountDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_account_details);
        binding.setUser(userModel);

        imageLayout = findViewById(R.id.account_photoUriTextField);
        displayNameLayout = findViewById(R.id.account_displayNameTextField);
        emailLayout = findViewById(R.id.account_emailTextField);
        passwordLayout = findViewById(R.id.account_passwordTextField);
        confirmPasswordLayout = findViewById(R.id.account_passwordConfirmTextField);

        emailFieldValidator = new EmailFieldValidator(emailLayout);
        passwordFieldValidator = new PasswordFieldValidator(passwordLayout, 8);

        // Populate text fields
        initUser();
    }

    private void initUser() {
        Log.d(TAG, "Initializing user...");
        assert user != null;
        Uri imageURL = user.getPhotoUrl();
        String displayName = user.getDisplayName();
        String email = user.getEmail();

        if (imageURL != null) {
            Log.d(TAG, "Setting up profile image...");
            userModel.setImageURL(imageURL.toString());
            ImageView img = findViewById(R.id.account_profileImg);
            img.setImageURI(imageURL);
        }

        userModel.setDisplayName(displayName);
        userModel.setEmail(email);
    }

    public void save(View view) {
        UserProfileChangeRequest.Builder updates = new UserProfileChangeRequest.Builder();
        final String imageURL = userModel.getImageURL();
        final String displayName = userModel.getDisplayName();
        final String email = userModel.getEmail();
        final String password = userModel.getPassword();
        final String confirmPassword = userModel.getConfirmPassword();

        if (!imageURL.equals("") && validateImageURL(imageURL)) {
            Log.d(TAG, "Updating profile image.");
            updates.setPhotoUri(Uri.parse(imageURL));
        }

//        if (!displayName.equals("") && validateDisplayName(displayName)) {
//            Log.d(TAG, "Updating display name.");
//            updates.setDisplayName(displayName);
//        }

        user.updateProfile(updates.build())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Profile successfully updated.");
                        }
                    }
                });

        if (!email.equals("") && validateEmail(email)) {
            Log.d(TAG, "Updating email.");
            user.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email successfully updated.");
                            }
                        }
                    });
        }

        if (!password.equals("") && validatePassword(password, confirmPassword)) {
            Log.d(TAG, "Updating password.");
            user.updatePassword(password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Password successfully updated.");
                            }
                        }
                    });
        }

        finish();
    }

    private boolean validateImageURL(String imageURL) {
        Log.d(TAG, String.format("Validating image URL: %s", imageURL));

        if (!URLUtil.isValidUrl(imageURL)) {
            imageLayout.setError("Please provide a valid URL.");
            return false;
        }
        return true;
    }

    private boolean validateDisplayName(String displayName) {
        Log.d(TAG, String.format("Validating username: %s", displayName));
        // TODO: Check user database for duplicate display names

        if (displayName.length() == 0) {
            displayNameLayout.setError("Please enter a display name.");
            return false;
        }
        return true;
    }

    @SuppressLint("RestrictedApi")
    private boolean validateEmail(String email) {
        Log.d(TAG, String.format("Validating email: %s", email));

        return emailFieldValidator.validate(email);
    }

    @SuppressLint("RestrictedApi")
    private boolean validatePassword(String password, String confirmPassword) {
        Log.d(TAG, String.format("Validating Password: %s", password));

        if (!password.equals(confirmPassword)) {
            Log.d(TAG, "Passwords don't match.");
            confirmPasswordLayout.setError("Passwords don't match.");
            return false;
        }

        return passwordFieldValidator.validate(password);
    }
}

package com.example.tradeblock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tradeblock.databinding.ActivityRegisterBinding;
import com.firebase.ui.auth.util.ui.fieldvalidators.EmailFieldValidator;
import com.firebase.ui.auth.util.ui.fieldvalidators.PasswordFieldValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    public static final int MIN_PASSWORD_LENGTH = 8;

    TextInputLayout mDisplayNameLayout;
    TextInputLayout mEmailLayout;
    TextInputLayout mPasswordLayout;
    EmailFieldValidator mEmailFieldValidator;
    PasswordFieldValidator mPasswordFieldValidator;
    FirebaseAuth mAuth;
    ProgressBar mProgressBar;
    User mUser;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActivityRegisterBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        mUser = new User();
        binding.setUser(mUser);

        mDisplayNameLayout = findViewById(R.id.register_displayNameTextField);
        mEmailLayout = findViewById(R.id.register_emailTextField);
        mPasswordLayout = findViewById(R.id.register_passwordTextField);
        mProgressBar = new ProgressBar(this);

        mAuth = FirebaseAuth.getInstance();

        mEmailFieldValidator = new EmailFieldValidator(mEmailLayout);
        mPasswordFieldValidator = new PasswordFieldValidator(mPasswordLayout, MIN_PASSWORD_LENGTH);
    }

    private boolean validateDisplayName(String displayName) {
        Log.d(TAG, String.format("Validating username: %s", displayName));
        // TODO: Check user database for duplicate display names

        if (displayName.length() == 0) {
            mDisplayNameLayout.setError("Please enter a display name.");
            return false;
        }
        return true;
    }

    @SuppressLint("RestrictedApi")
    private boolean validateEmail(String email) {
        Log.d(TAG, String.format("Validating email: %s", email));

        return mEmailFieldValidator.validate(email);
    }

    @SuppressLint("RestrictedApi")
    private boolean validatePassword(String password) {
        Log.d(TAG, String.format("Validating Password: %s", password));

        return mPasswordFieldValidator.validate(password);
    }

    public void register(View v) {
        // Hide keyboard and clear focus
        hideKeyboard(v);
        View focus = getCurrentFocus();
        if (focus != null) focus.clearFocus();

        mProgressBar.setVisibility(View.VISIBLE);

        final String displayName = mUser.getDisplayName();
        final String email = mUser.getEmail();
        final String password = mUser.getPassword();

        // Cancel registration if any input is invalid
        if (!validateDisplayName(displayName) || !validateEmail(email) || !validatePassword(password))
            return;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressBar.setVisibility(View.GONE);

                        // Account creation success...
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(RegisterActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                            // Set display name
                            // TODO: Set profile picture
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest updates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build();

                            // Update profile
                            assert user != null;
                            user.updateProfile(updates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> update) {
                                            if (update.isSuccessful()) {
                                                Log.d(TAG, "Profile successfully updated.");
                                            }

                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Account creation failure...
                        Log.d(TAG, "Failed to create user with email.");

                        if (e instanceof FirebaseAuthUserCollisionException) {
                            mEmailLayout.setError(e.getLocalizedMessage());
                        } else {
                            Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void back(View v) {
        Log.d(TAG, "Registration canceled.");
        setResult(RESULT_CANCELED);
        finish();
    }
}

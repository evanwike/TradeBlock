package com.example.tradeblock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tradeblock.databinding.ActivityLoginBinding;
import com.firebase.ui.auth.util.ui.fieldvalidators.EmailFieldValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    public static final int RC_REGISTER = 1;
    public static final int RC_SIGN_IN = 2;
    private static final String TAG = "LoginActivity";

    TextInputLayout mEmailLayout;
    EmailFieldValidator mEmailFieldValidator;
    TextInputLayout mPasswordLayout;
    User mUser;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkCurrentUser();

        ActivityLoginBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        mUser = new User();
        binding.setUser(mUser);

        mEmailLayout = findViewById(R.id.emailTextField);
        mEmailFieldValidator = new EmailFieldValidator(mEmailLayout);
        mPasswordLayout = findViewById(R.id.passwordTextField);
    }

    public void login(View v) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        String email = mUser.getEmail();
        String password = mUser.getPassword();

        clearErrorMessages();

        if (email.length() == 0) {
            mEmailLayout.setErrorEnabled(true);
            mEmailLayout.setError("Email field is empty.");
            return;
        }

        if (password.length() == 0) {
            mPasswordLayout.setErrorEnabled(true);
            mPasswordLayout.setError("Password field is empty.");
            return;
        }

        auth.signInWithEmailAndPassword(mUser.getEmail(), mUser.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            // TODO: Send to league activity
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            mEmailLayout.setErrorEnabled(true);
                            mEmailLayout.setError(Objects.requireNonNull(task.getException()).getMessage());

                            // TODO: Sign-in failure
                        }
                    }
                });
    }

    public void register(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(intent, RC_REGISTER);
    }

    @SuppressLint("RestrictedApi")
    public void forgotPassword(View v) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = mUser.getEmail();

        if (!mEmailFieldValidator.validate(email))
            return;

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password reset email sent.");
                            Toast.makeText(LoginActivity.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Password reset email failed.");

                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                mEmailLayout.setErrorEnabled(true);
                                mEmailLayout.setError(e.getMessage());
                            }
                        }
                    }
                });
    }

    private void checkCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // User already signed in
            Log.d(TAG, "User already logged in.");

            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // TODO: Main entry point
        } else {
            Log.d(TAG, "User not logged in.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO: Consider refactoring this, log in user from registration activity
        // Registration
        if (requestCode == RC_REGISTER) {
            if (resultCode == RESULT_OK) {
                // Successful registration
                Log.d(TAG, "Successful registration.");
                // TODO: Sign-in user
//                this.finish();
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG,"Registration activity finished.");
            }
        }
        // Sign-in
        else if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Successful login
                Log.d(TAG, "Successful login.");
                // TODO: Send to main entry page
            } else {
                // TODO: Implement login failure
            }
        }
    }

    // Clear input layout error messages
    private void clearErrorMessages() {
        mEmailLayout.setError(null);
        mEmailLayout.setErrorEnabled(false);

        mPasswordLayout.setError(null);
        mPasswordLayout.setErrorEnabled(false);
    }
}

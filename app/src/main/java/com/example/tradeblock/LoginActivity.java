package com.example.tradeblock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tradeblock.databinding.ActivityLoginBinding;
import com.firebase.ui.auth.util.ui.fieldvalidators.EmailFieldValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // User already signed in, sent to Main
        if (user != null) {
            Log.d(TAG, "User already logged in.");
            startMainActivity();
        }

        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mUser = new User();
        binding.setUser(mUser);

        mEmailLayout = findViewById(R.id.emailTextField);
        mEmailFieldValidator = new EmailFieldValidator(mEmailLayout);
        mPasswordLayout = findViewById(R.id.passwordTextField);
    }

    public void login(View v) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        final String email = mUser.getEmail();
        final String password = mUser.getPassword();

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

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Login success...
                        if (task.isSuccessful()) {
                            // Send user to Main
                            Log.d(TAG, "signInWithEmail:success");
                            startMainActivity();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    // Login failure...
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to sign user in with email.");
                        Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        mPasswordLayout.setErrorEnabled(true);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            mPasswordLayout.setError("Incorrect password.");
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            mEmailLayout.setError("Unregistered email address.");
                        } else {
                            mEmailLayout.setError(e.getLocalizedMessage());
                        }
                    }
                });
    }

    // Send user to Main
    private void startMainActivity() {
        Log.d(TAG, "Logging in user.");
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // Send user to Register
    public void register(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(intent, RC_REGISTER);
    }

    // Send password reset email
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
                        // Reset email successfully sent...
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password reset email sent.");
                            Toast.makeText(LoginActivity.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Reset email failure...
                        Log.d(TAG, "Password reset email failed.");
                        mEmailLayout.setErrorEnabled(true);
                        mEmailLayout.setError(e.getLocalizedMessage());
                    }
                });
    }

    // Result of user registration
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_REGISTER) {
            // Registration successful -> Main
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Successful registration.");
                startMainActivity();
            }
            else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Registration activity finished.");
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

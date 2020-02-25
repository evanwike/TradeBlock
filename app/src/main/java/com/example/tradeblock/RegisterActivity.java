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
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    TextInputLayout mDisplayNameLayout;
    TextInputLayout mEmailLayout;
    TextInputLayout mPasswordLayout;
    EmailFieldValidator mEmailFieldValidator;
    PasswordFieldValidator mPasswordFieldValidator;
    FirebaseDatabase mDb;
    DatabaseReference mRef;
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

        mDb = FirebaseDatabase.getInstance();
        mRef = mDb.getReference("users");
        mAuth = FirebaseAuth.getInstance();

        mEmailFieldValidator = new EmailFieldValidator(mEmailLayout);
        mPasswordFieldValidator = new PasswordFieldValidator(mPasswordLayout, 8);
    }

    private boolean validateUsername() {
        String username = mUser.getUsername();
        Log.d(TAG, String.format("Validating username: %s", username));
        // TODO: Check user database for duplicate usernames
        if (username.length() == 0)
            mDisplayNameLayout.setError("Please enter a display name.");
        return false;
    }

    @SuppressLint("RestrictedApi")
    private boolean validateEmail() {
        String email = mUser.getEmail();
        Log.d(TAG, String.format("Validating email: %s", email));

        return mEmailFieldValidator.validate(email);
    }

    @SuppressLint("RestrictedApi")
    private boolean validatePassword() {
        String password = mUser.getPassword();
        Log.d(TAG, String.format("Validating Password: %s", password));

        return mPasswordFieldValidator.validate(password);
    }

    public void register(View v) {
        // Hide keyboard and clear focus
        hideKeyboard(v);
        View focus = getCurrentFocus();
        if (focus != null) focus.clearFocus();

        mProgressBar.setVisibility(View.VISIBLE);

        String username = mUser.getUsername();
        String email = mUser.getEmail();
        String password = mUser.getPassword();

        // Cancel registration if any user attributes are invalid
        if (!validateUsername() || !validateEmail() || !validatePassword())
            return;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                            // TODO: Send to logged in page
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            // TODO: Registration failed

                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthUserCollisionException e) {
                                Log.e(TAG, e.getMessage());
                                mEmailLayout.setError(e.getMessage());
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }
                });
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void back(View view) {
        Log.d(TAG, "Registration canceled.");
        setResult(RESULT_CANCELED);
        finish();
    }
}

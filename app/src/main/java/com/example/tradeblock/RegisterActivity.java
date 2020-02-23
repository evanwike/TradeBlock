package com.example.tradeblock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

    Button btnRegister;
    TextInputLayout displayNameLayout, emailLayout, passwordLayout;
    EditText displayNameLayoutText, emailLayoutText, passwordLayoutText;

    FirebaseDatabase db;
    DatabaseReference ref;
    FirebaseAuth auth;
    EmailFieldValidator emailFieldValidator;
    PasswordFieldValidator passwordFieldValidator;
    ProgressBar progressBar;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.register_btnRegister);
        displayNameLayout = findViewById(R.id.register_displayNameTextField);
        displayNameLayoutText = displayNameLayout.getEditText();
        emailLayout = findViewById(R.id.register_emailTextField);
        emailLayoutText = emailLayout.getEditText();
        passwordLayout = findViewById(R.id.register_passwordTextField);
        passwordLayoutText = passwordLayout.getEditText();
        progressBar = new ProgressBar(this);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("users");
        auth = FirebaseAuth.getInstance();

        emailFieldValidator = new EmailFieldValidator(emailLayout);
        passwordFieldValidator = new PasswordFieldValidator(passwordLayout, 8);
    }

    private String getDisplayNameText() { return displayNameLayoutText.getText().toString().trim(); }
    private String getEmailText() { return emailLayoutText.getText().toString().trim(); }
    private String getPasswordText() { return passwordLayoutText.getText().toString().trim(); }

    @SuppressLint("RestrictedApi")
    private boolean validateEmail(String email) {
        Log.d(TAG, String.format("Validating email: %s", email));
        return emailFieldValidator.validate(email);
    }

    @SuppressLint("RestrictedApi")
    private boolean validatePassword(String password) {
        Log.d(TAG, String.format("Validating Password: %s", password));
        return passwordFieldValidator.validate(password);
    }

    public void register(View v) {
        // Hide keyboard and clear focus
        hideKeyboard(v);
        View focus = getCurrentFocus();
        if (focus != null) focus.clearFocus();

        progressBar.setVisibility(View.VISIBLE);

        String displayName = getDisplayNameText();
        String email = getEmailText();
        String password = getPasswordText();

        if (!validateEmail(email)) {
            return;
        } else if (!validatePassword(password)) {
            return;
        }

        // TODO: Validate display name in db

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"createUserWithEmail:success");
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                            // TODO: Send to logged in page
                            // Do I even need any of this?
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Log.w(TAG,"createUserWithEmail:failure", task.getException());
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                            // TODO: Registration failed

                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch(FirebaseAuthUserCollisionException e) {
                                Log.e(TAG, e.getMessage());
                                emailLayout.setError(e.getMessage());
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }
                });
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void back(View view) {
        Log.d(TAG,"Registration canceled.");
        setResult(RESULT_CANCELED);
        finish();
    }
}

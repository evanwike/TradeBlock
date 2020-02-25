package com.example.tradeblock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final int RC_REGISTER = 1;
    public static final int RC_SIGN_IN = 2;
    private static final String TAG = "MainActivity";

    TextInputLayout emailLayout;
    EditText emailText;
    TextInputLayout passwordLayout;
    EditText passwordText;
    Button btnLogin;
    Button btnRegister;
    Button btnForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkCurrentUser();

        emailLayout = findViewById(R.id.emailTextField);
        emailText = emailLayout.getEditText();
        passwordLayout = findViewById(R.id.passwordTextField);
        passwordText = passwordLayout.getEditText();

        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnForgot = findViewById(R.id.btn_forgot);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });
    }

    private String getEmail() { return emailText.getText().toString().trim(); }
    private String getPassword() { return passwordText.getText().toString().trim(); }

    private void login() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = getEmail();
        String password = getPassword();

        auth.signInWithEmailAndPassword(email, password)
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
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // TODO: Sign-in failure
                        }

                        // ...
                    }
                });
    }

    private void register() {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivityForResult(intent, RC_REGISTER);
    }

    private void forgotPassword() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = getEmail();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password reset email sent.");
                            Toast.makeText(MainActivity.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Password reset email failed.");

                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                emailLayout.setError(e.getMessage());
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
            } else {
                // TODO: Implement login failure
            }
        }
    }
}

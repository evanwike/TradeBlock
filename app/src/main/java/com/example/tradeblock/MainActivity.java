package com.example.tradeblock;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public static final int RC_REGISTER = 1;
    public static final int RC_SIGN_IN = 2;
    private static final String TAG = "MainActivity";

    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkCurrentUser();

        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivityForResult(intent, RC_REGISTER);
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
        if (requestCode == RC_REGISTER) {
            if (resultCode == RESULT_OK) {
                // Successful registration
                Log.d(TAG, "Successful registration.");
                // TODO: Sign-in user
                this.finish();
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG,"Registration activity finished.");
            }
        } else if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Successful login
                Log.d(TAG, "Successful login.");
            } else {
                // TODO: Implement login failure
            }
        }
    }
}

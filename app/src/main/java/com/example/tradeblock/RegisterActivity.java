package com.example.tradeblock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {
    EditText displayName, email, password;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        displayName = findViewById(R.id.txt_register_display);
        email = findViewById(R.id.txt_register_email);
        password = findViewById(R.id.txt_register_password);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private boolean validateDisplayName() {
        // TODO: Validate display name
        return false;
    }

    private boolean validateEmail() {
        // TODO: Validate email
        return false;
    }

    private boolean validatePassword() {
        // TODO: Validate password
        return false;
    }
}

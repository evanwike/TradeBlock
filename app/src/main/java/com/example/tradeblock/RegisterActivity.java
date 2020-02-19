package com.example.tradeblock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.tradeblock.databinding.ActivityRegisterBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    public boolean validDisplay, validEmail, validPassword;

    Button btnRegister;
    FirebaseDatabase db;
    DatabaseReference ref;
    UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActivityRegisterBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        user = new UserModel();
        binding.setUser(user);

        validDisplay = false;
        validEmail = false;
        validPassword = false;

        btnRegister = findViewById(R.id.btn_register);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("users");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        // TODO: Attempt to register user
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

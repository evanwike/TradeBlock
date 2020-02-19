package com.example.tradeblock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    public boolean validDisplay, validEmail, validPassword;

    EditText editDisplay, editEmail, editPassword;
    Button btnRegister;
    FirebaseDatabase db;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        validDisplay = false;
        validEmail = false;
        validPassword = false;

        editDisplay = findViewById(R.id.edit_register_display);
        editEmail = findViewById(R.id.edit_register_email);
        editPassword = findViewById(R.id.edit_register_password);
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

    public void validateDisplayName() {
        // TODO: Validate display name
        final String displayName = editDisplay.getText().toString();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(displayName)) {
                    // Username taken
                    Log.e(TAG, "Username taken.");
                } else {
                    validDisplay = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, databaseError.toException());
            }
        });
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

package com.example.tradeblock.updateprofile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.example.tradeblock.R;

public class UpdateProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        UpdateModel model = new ViewModelProvider.NewInstanceFactory().create(UpdateModel.class);

    }
}

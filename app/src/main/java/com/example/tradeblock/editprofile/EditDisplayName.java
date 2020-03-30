package com.example.tradeblock.editprofile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tradeblock.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditDisplayName extends Fragment {
    private static final String TAG = "EditDisplayName";
    private TextInputLayout layout;
    private UpdateModel model;

    public EditDisplayName() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_edit_display_name, container, false);

        layout = root.findViewById(R.id.edit_displayNameTextField);
        Button btnUpdate = root.findViewById(R.id.btn_update_display);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayName = Objects.requireNonNull(layout.getEditText()).toString();
                update(displayName);
            }
        });

        return root;
    }

    private boolean validateDisplayName(String displayName) {
        Log.d(TAG, String.format("Validating username: %s", displayName));
        // TODO: Check user database for duplicate display names

        if (displayName.length() == 0) {
            layout.setError("Please enter a display name.");
            return false;
        }
        return true;
    }

    private void update(String displayName) {
        if (!displayName.equals("") && validateDisplayName(displayName)) {
            Log.d(TAG, "Updating display name.");
        }
    }
}

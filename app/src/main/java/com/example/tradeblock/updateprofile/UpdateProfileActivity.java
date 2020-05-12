package com.example.tradeblock.updateprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.tradeblock.R;
import com.firebase.ui.auth.util.ui.fieldvalidators.EmailFieldValidator;
import com.firebase.ui.auth.util.ui.fieldvalidators.PasswordFieldValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

import static com.example.tradeblock.RegisterActivity.MIN_PASSWORD_LENGTH;


public class UpdateProfileActivity extends AppCompatActivity {
    private static final String TAG = "UpdateProfileActivity";
    public static final int PIC_UPDATE = 0;
    public static final int DISPLAY_UPDATE = 1;
    public static final int EMAIL_UPDATE = 2;
    private boolean[] updated;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        user = FirebaseAuth.getInstance().getCurrentUser();
        UpdateModel model = new ViewModelProvider.NewInstanceFactory().create(UpdateModel.class);
        updated = new boolean[]{false, false, false};
    }

    private boolean validateProfilePicture(TextInputLayout layout, String url) {
      return false;
    }

    private void updateProfilePicture(String url) {

    }

    private boolean validateDisplayName(TextInputLayout layout, String displayName) {
        Log.d(TAG, String.format("Validating display name: %s", displayName));

        if (displayName.length() == 0) {
            layout.setError("Please enter a display name.");
            return false;
        }
        return true;
    }

    private void updateDisplayName(String displayName) {
        UserProfileChangeRequest.Builder updates = new UserProfileChangeRequest.Builder();
        updates.setDisplayName(displayName);
        user.updateProfile(updates.build())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Display name successfully updated.");
                        } else {
                            Log.d(TAG, "Failed to update profile with new display name.");
                            Log.d(TAG, Objects.requireNonNull(task.getException()).getMessage());
                        }
                    }
                });
    }

    @SuppressLint("RestrictedApi")
    private boolean validateEmail(TextInputLayout layout, String email) {
        EmailFieldValidator emailFieldValidator = new EmailFieldValidator(layout);
        Log.d(TAG, String.format("Validating email prior to update: %s", email));

        return emailFieldValidator.validate(email);
    }

    private boolean updateEmail(final String email, final String password) {
        Log.d(TAG, "Updating email.");
        final boolean[] success = {false};

        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email successfully updated.");
                            success[0] = true;
                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthRecentLoginRequiredException e) {
                                Log.d(TAG, "User must be re-authenticated due to stale credentials.");
                                // TODO: Get old password
                                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), password);

                                user.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task2) {
                                                if (task2.isSuccessful()) {
                                                    Log.d(TAG, "User successfully re-authenticated.");
                                                    updateEmail(email, password);
                                                } else {
                                                    Log.d(TAG, Objects.requireNonNull(task2.getException()).getMessage());
                                                }
                                            }
                                        });
                            } catch (Exception e) {
                                Log.d(TAG, e.getMessage());
                            }
                        }
                    }
                });
        return success[0];
    }

    @SuppressLint("RestrictedApi")
    private boolean validatePassword(TextInputLayout passwordLayout, TextInputLayout confirmLayout, String password, String confirm) {
        PasswordFieldValidator passwordFieldValidator = new PasswordFieldValidator(passwordLayout, MIN_PASSWORD_LENGTH);
        Log.d(TAG, String.format("Validating password prior to update: %s", password));

        if (!password.equals(confirm)) {
            Log.d(TAG, "Passwords don't match.");
            confirmLayout.setError("Passwords must match.");
            return false;
        }

        return passwordFieldValidator.validate(password);
    }

    private void updatePassword(String password) {
        Log.d(TAG, "Updating password.");
        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"Password successfully updated.");
                        }
                    }
                });
    }

    public void updateProfileImageClick(View view) {

    }

    public void updateDisplayNameClick(View view) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialog = inflater.inflate(R.layout.dialog_update_with_input_text, null);
        final TextView titleView = dialog.findViewById(R.id.update_dialog_title);
        final TextInputLayout displayNameLayout = dialog.findViewById(R.id.update_dialog_text_input_layout);

        titleView.setText(R.string.update_dialog_title_display);
        displayNameLayout.setHint("Display Name");
        Objects.requireNonNull(displayNameLayout.getEditText()).setText(user.getDisplayName());

        new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Dialog)
                .setView(dialog)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG,"Cancelling display name update.");
                    }
                })
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String displayName = Objects.requireNonNull(displayNameLayout.getEditText()).getText().toString();
                        Log.d(TAG,"Attempting to update display name to " + displayName);

                        if (validateDisplayName(displayNameLayout, displayName)) {
                            updateDisplayName(displayName);
                            updated[DISPLAY_UPDATE] = true;
                        }
                    }
                })
                .create()
                .show();
    }

    public void updateEmailClick(View view) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialog = inflater.inflate(R.layout.dialog_update_email, null);
        final TextView titleView = dialog.findViewById(R.id.update_dialog_title);
        final TextInputLayout passwordLayout = dialog.findViewById(R.id.update_dialog_password);
        final TextInputLayout emailLayout = dialog.findViewById(R.id.update_dialog_email);

        titleView.setText(R.string.update_dialog_title_email);
        Objects.requireNonNull(emailLayout.getEditText()).setText(user.getEmail());

        new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Dialog)
                .setView(dialog)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG,"Cancelling email update.");
                    }
                })
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    // TODO: Change so it doesn't close dialog, maybe do same for email
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = Objects.requireNonNull(emailLayout.getEditText()).getText().toString();
                        String password = Objects.requireNonNull(passwordLayout.getEditText()).getText().toString();
                        Log.d(TAG,"Attempting to update email to " + email);

                        if (validateEmail(emailLayout, email)) {
                            Log.d(TAG, "New email successfully validated.");

                            if (updateEmail(email, password)) {
                                Log.d(TAG, "Email successfully updated.");
                                updated[EMAIL_UPDATE] = true;
                            } else {
                                Log.d(TAG, "Incorrect password.");
                                passwordLayout.setError("Incorrect password.");
                            }

                        }
                    }
                })
                .create()
                .show();
    }

    public void updatePasswordClick(View view) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialog = inflater.inflate(R.layout.dialog_update_password, null);
        final TextView titleView = dialog.findViewById(R.id.update_dialog_title);
        final TextInputLayout passwordLayout = dialog.findViewById(R.id.update_dialog_password);
        final TextInputLayout confirmLayout = dialog.findViewById(R.id.update_dialog_confirm_password);

        titleView.setText(R.string.update_dialog_title_password);

        new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Dialog)
                .setView(dialog)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG,"Cancelling password update.");
                    }
                })
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = Objects.requireNonNull(passwordLayout.getEditText()).getText().toString();
                        String confirmPassword = Objects.requireNonNull(passwordLayout.getEditText()).getText().toString();
                        Log.d(TAG,"Attempting to update password.");

                        if (validatePassword(passwordLayout, confirmLayout, password, confirmPassword)) {
                            Log.d(TAG, "Password successfully updated.");
                            updatePassword(password);
                        }
                    }
                })
                .create()
                .show();
    }

    public void back(View view) {
        Intent intent = new Intent();
        intent.putExtra("updated", updated);
        setResult(RESULT_OK, intent);
        finish();
    }
}

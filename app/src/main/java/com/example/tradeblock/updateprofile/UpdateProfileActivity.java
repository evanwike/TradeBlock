package com.example.tradeblock.updateprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tradeblock.R;
import com.firebase.ui.auth.util.ui.fieldvalidators.EmailFieldValidator;
import com.firebase.ui.auth.util.ui.fieldvalidators.PasswordFieldValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
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
        Log.d(TAG, String.format("Validating image URL: %s", url));

        if (!URLUtil.isValidUrl(url)) {
            layout.setError("Please provide a valid URL.");
            return false;
        }
        return true;
    }

    private void updateProfilePicture(String url) {
        // TODO: Finish this method
        // TODO: Allow users to set profile image from device
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
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to update profile with new display name.");
                        Toast.makeText(UpdateProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("RestrictedApi")
    private boolean validateEmail(TextInputLayout layout, String email) {
        EmailFieldValidator emailFieldValidator = new EmailFieldValidator(layout);
        Log.d(TAG, String.format("Validating email prior to update: %s", email));

        return emailFieldValidator.validate(email);
    }

    private boolean updateEmail(final String email, final String password, final TextInputLayout emailLayout, final TextInputLayout passwordLayout) {
        Log.d(TAG, "Updating email.");
        final boolean[] success = {false};

        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email successfully updated.");
                            success[0] = true;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthRecentLoginRequiredException) {
                            Log.d(TAG, "User must be re-authenticated due to stale credentials.");

                            passwordLayout.setVisibility(View.VISIBLE);
                            AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), password);

                            // Attempt to re-authenticate user
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "User successfully re-authenticated.");
                                            updateEmail(email, password, emailLayout, passwordLayout);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                                Toast.makeText(UpdateProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
                                                success[0] = false;
                                            }
                                        }
                                    });
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

    private void updatePassword(final String password, final TextInputLayout currentPasswordLayout) {
        Log.d(TAG, "Updating password.");
        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"Password successfully updated.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthRecentLoginRequiredException) {
                            Log.d(TAG, "User must be re-authenticated due to stale credentials.");

                            currentPasswordLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(UpdateProfileActivity.this, "Please enter your current password to proceed.", Toast.LENGTH_SHORT).show();
                            // TODO: Wait to get new password before getting credential - will probably have to do this with all 3
                            AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), password);

                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "User successfully re-authenticated.");
                                            updatePassword(password, currentPasswordLayout);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, e.getLocalizedMessage());
                                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                                Toast.makeText(UpdateProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                currentPasswordLayout.setError(e.getLocalizedMessage());
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public void updateProfileImageClick(View view) {
        // TODO: Handle click event
    }

    public void updateDisplayNameClick(View view) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_update_with_input_text, null);
        final TextView titleView = dialogLayout.findViewById(R.id.update_dialog_title);
        final TextInputLayout displayNameLayout = dialogLayout.findViewById(R.id.update_dialog_text_input_layout);
        Button cancel = dialogLayout.findViewById(R.id.update_dialog_cancel);
        Button update = dialogLayout.findViewById(R.id.update_dialog_update);

        titleView.setText(R.string.update_dialog_title_display);
        displayNameLayout.setHint("Display Name");
        Objects.requireNonNull(displayNameLayout.getEditText()).setText(user.getDisplayName());

        final AlertDialog dialog =  new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Dialog)
                .setView(dialogLayout)
                .create();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Cancelling display name update.");
                dialog.dismiss();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayName = Objects.requireNonNull(displayNameLayout.getEditText()).getText().toString();
                Log.d(TAG,"Attempting to update display name to " + displayName);

                if (validateDisplayName(displayNameLayout, displayName)) {
                    updateDisplayName(displayName);
                    updated[DISPLAY_UPDATE] = true;
                }
            }
        });

        dialog.show();
    }

    public void updateEmailClick(View view) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_update_email, null);
        final TextView titleView = dialogLayout.findViewById(R.id.update_dialog_title);
        final TextInputLayout passwordLayout = dialogLayout.findViewById(R.id.update_dialog_password);
        final TextInputLayout emailLayout = dialogLayout.findViewById(R.id.update_dialog_email);
        Button cancel = dialogLayout.findViewById(R.id.update_dialog_cancel);
        Button update = dialogLayout.findViewById(R.id.update_dialog_update);

        titleView.setText(R.string.update_dialog_title_email);
        Objects.requireNonNull(emailLayout.getEditText()).setText(user.getEmail());

        final AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Dialog)
                .setView(dialogLayout)
                .create();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Cancelling email update.");
                dialog.dismiss();
                passwordLayout.setVisibility(View.INVISIBLE);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(emailLayout.getEditText()).getText().toString();
                String password = Objects.requireNonNull(passwordLayout.getEditText()).getText().toString();
                Log.d(TAG,"Attempting to update email to " + email);

                if (validateEmail(emailLayout, email)) {
                    Log.d(TAG, "New email successfully validated.");

                    if (updateEmail(email, password, emailLayout, passwordLayout)) {
                        Log.d(TAG, "Email successfully updated.");
                        updated[EMAIL_UPDATE] = true;
                    } else {
                        Log.d(TAG, "Incorrect password.");
                        passwordLayout.setError("Incorrect password.");
                    }
                }
            }
        });

        dialog.show();
    }

    public void updatePasswordClick(View view) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_update_password, null);
        final TextView titleView = dialogLayout.findViewById(R.id.update_dialog_title);
        final TextInputLayout passwordLayout = dialogLayout.findViewById(R.id.update_dialog_password);
        final TextInputLayout confirmLayout = dialogLayout.findViewById(R.id.update_dialog_confirm_password);
        final TextInputLayout currentPasswordLayout = dialogLayout.findViewById(R.id.update_dialog_current_password);
        Button cancel = dialogLayout.findViewById(R.id.update_dialog_cancel);
        Button update = dialogLayout.findViewById(R.id.update_dialog_update);

        titleView.setText(R.string.update_dialog_title_password);

        final AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Dialog)
                .setView(dialogLayout)
                .create();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Cancelling password update.");
                dialog.dismiss();
                currentPasswordLayout.setVisibility(View.INVISIBLE);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = Objects.requireNonNull(passwordLayout.getEditText()).getText().toString();
                String confirmPassword = Objects.requireNonNull(passwordLayout.getEditText()).getText().toString();
                Log.d(TAG,"Attempting to update password.");

                if (validatePassword(passwordLayout, confirmLayout, password, confirmPassword)) {
                    Log.d(TAG, "Password successfully updated.");
                    updatePassword(password, currentPasswordLayout);
                }
            }
        });

        dialog.show();
    }

    public void back(View view) {
        Intent intent = new Intent();
        intent.putExtra("updated", updated);
        setResult(RESULT_OK, intent);
        finish();
    }
}

package com.example.tradeblock.updateprofile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tradeblock.R;
import com.example.tradeblock.Utils;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import static com.example.tradeblock.MainActivity.IMAGE_SIZE_PX;
import static com.example.tradeblock.RegisterActivity.MIN_PASSWORD_LENGTH;


public class UpdateProfileActivity extends AppCompatActivity {
    private static final String TAG = "UpdateProfileActivity";
    public static final int AVATAR_UPDATE = 0;
    public static final int DISPLAY_UPDATE = 1;
    public static final int EMAIL_UPDATE = 2;
    private static final int RC_LOAD_IMAGE = 4;
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


    // Avatar
    private boolean validateAvatarUrl(TextInputLayout layout, String url) {
        Log.d(TAG, String.format("Validating image URL: %s", url));

        if (!URLUtil.isValidUrl(url)) {
            layout.setError("Please provide a valid URL.");
            return false;
        }
        return true;
    }

    private void updateAvatar(String imageUrl, final AlertDialog dialog) {
        UserProfileChangeRequest updates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(imageUrl))
                .build();

        user.updateProfile(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Avatar URL successfully updated.");
                            Toast.makeText(UpdateProfileActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                            updated[AVATAR_UPDATE] = true;
                            dialog.dismiss();
                        } else {
                            Log.d(TAG, "Avatar URL update complete, but unsuccessful.");
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Avatar updated failed.");
                        e.printStackTrace();
                    }
                });
    }

    public void updateAvatarClick(View view) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_update_avatar, null);
        final TextView titleView = dialogLayout.findViewById(R.id.update_dialog_title);
        final ImageView imageView = dialogLayout.findViewById(R.id.update_dialog_imageView);
        final TextInputLayout imageUrlLayout = dialogLayout.findViewById(R.id.update_dialog_profile_picture_url);
        Button cancel = dialogLayout.findViewById(R.id.update_dialog_cancel);
        Button select = dialogLayout.findViewById(R.id.update_dialog_select_image);
        Button update = dialogLayout.findViewById(R.id.update_dialog_update);
        Uri userUrl = user.getPhotoUrl();

        titleView.setText(R.string.update_dialog_title_avatar);

        // Set current user image
        if (userUrl != null)
            Utils.getImageAndPlaceInto(userUrl.toString(), imageView, IMAGE_SIZE_PX, this);

        final AlertDialog dialog =  new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Dialog)
                .setView(dialogLayout)
                .create();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Cancelling profile picture update.");
                dialog.dismiss();
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Attempt to set image from URL.");
                String imageUrl = Objects.requireNonNull(imageUrlLayout.getEditText()).getText().toString();

                if (validateAvatarUrl(imageUrlLayout, imageUrl)) {
                    Log.d(TAG, "Image URL successfully validated.");
                    Utils.getImageAndPlaceInto(imageUrl, imageView, IMAGE_SIZE_PX, getBaseContext());
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Attempting to update Avatar URL.");
                String imageUrl = Objects.requireNonNull(imageUrlLayout.getEditText()).getText().toString();

                if (validateAvatarUrl(imageUrlLayout, imageUrl)) {
                    updateAvatar(imageUrl, dialog);
                }
            }
        });

        dialog.show();
    }


    // Display Name
    private boolean validateDisplayName(TextInputLayout layout, String displayName) {
        Log.d(TAG, String.format("Validating display name: %s", displayName));
        clearError(layout);

        if (displayName.length() == 0) {
            layout.setError("Please enter a display name.");
            return false;
        }
        return true;
    }

    private void updateDisplayName(String displayName, final AlertDialog dialog) {
        UserProfileChangeRequest.Builder updates = new UserProfileChangeRequest.Builder();
        updates.setDisplayName(displayName);
        user.updateProfile(updates.build())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Display name successfully updated.");
                            Toast.makeText(UpdateProfileActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                            updated[DISPLAY_UPDATE] = true;
                            dialog.dismiss();
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
                    updateDisplayName(displayName, dialog);
                }
            }
        });

        dialog.show();
    }


    // Email
    @SuppressLint("RestrictedApi")
    private boolean validateEmail(TextInputLayout layout, String email) {
        clearError(layout);
        EmailFieldValidator emailFieldValidator = new EmailFieldValidator(layout);
        Log.d(TAG, String.format("Validating email prior to update: %s", email));

        return emailFieldValidator.validate(email);
    }

    private void updateEmail(final String email, final AlertDialog dialog, final TextInputLayout passwordLayout) {
        Log.d(TAG, "Updating email.");

        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email successfully updated.");
                            Toast.makeText(UpdateProfileActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            updated[EMAIL_UPDATE] = true;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Get password from user, attempt to re-authenticate user and update email
                        if (e instanceof FirebaseAuthRecentLoginRequiredException) {
                            Log.d(TAG, "User must be re-authenticated due to stale credentials.");
                            Toast.makeText(UpdateProfileActivity.this, "Please enter your password to proceed.", Toast.LENGTH_SHORT).show();
                            passwordLayout.setVisibility(View.VISIBLE);
                            final String password = Objects.requireNonNull(passwordLayout.getEditText()).getText().toString();

                            if (validatePassword(passwordLayout, password)) {
                                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), password);

                                user.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User successfully re-authenticated.");
                                                    updateEmail(email, dialog,passwordLayout);
                                                } else {
                                                    Log.d(TAG, "Re-authentication complete, but unsuccessful.");
                                                    Log.d(TAG, Objects.requireNonNull(task.getException()).getMessage());
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                                    passwordLayout.setError("Incorrect password.");
                                                } else {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    public void updateEmailClick(View view) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_update_email, null);
        final TextView titleView = dialogLayout.findViewById(R.id.update_dialog_title);
        final TextInputLayout passwordLayout = dialogLayout.findViewById(R.id.update_dialog_password);
        final TextInputLayout emailLayout = dialogLayout.findViewById(R.id.update_dialog_email);
        Button cancel = dialogLayout.findViewById(R.id.update_dialog_cancel);
        final Button update = dialogLayout.findViewById(R.id.update_dialog_update);

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
                Log.d(TAG,"Attempting to update email to " + email);

                if (validateEmail(emailLayout, email)) {
                    Log.d(TAG, "New email successfully validated.");
                    updateEmail(email, dialog, passwordLayout);
                }
            }
        });

        dialog.show();
    }


    // Password
    // Send password reset email
    @SuppressLint("RestrictedApi")
    public void forgotPassword(View view) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = user.getEmail();

        assert email != null;
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Reset email successfully sent...
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password reset email sent.");
                            Toast.makeText(UpdateProfileActivity.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "sendPasswordResetEmail complete, but unsuccessful.");
                            Exception e = task.getException();
                            assert e != null;
                            Log.d(TAG, e.getMessage());
                            Toast.makeText(UpdateProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Reset email failure...
                        Log.d(TAG, "sendPasswordResetEmail failed.");
                        e.printStackTrace();
                    }
                });
    }
    @SuppressLint("RestrictedApi")
    private boolean validatePassword(TextInputLayout passwordLayout, String password) {
        clearError(passwordLayout);

        if (password.length() == 0) {
            passwordLayout.setError("Please enter your password to continue.");
            return false;
        }

        PasswordFieldValidator passwordFieldValidator = new PasswordFieldValidator(passwordLayout, 0);
        Log.d(TAG, String.format("Validating password prior to update: %s", password));

        return passwordFieldValidator.validate(password);
    }

    @SuppressLint("RestrictedApi")
    private boolean validatePassword(TextInputLayout passwordLayout, TextInputLayout confirmLayout, String password, String confirm) {
        clearError(passwordLayout);
        clearError(confirmLayout);
        PasswordFieldValidator passwordFieldValidator = new PasswordFieldValidator(passwordLayout, MIN_PASSWORD_LENGTH);
        Log.d(TAG, String.format("Validating password prior to update: %s", password));

        if (!password.equals(confirm)) {
            Log.d(TAG, "Passwords don't match.");
            confirmLayout.setError("Passwords must match.");
            return false;
        }

        return passwordFieldValidator.validate(password);
    }

    private void updatePassword(final String password, final TextInputLayout currentPasswordLayout, final AlertDialog dialog, final View dialogLayout) {
        Log.d(TAG, "Updating password.");
        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"Password successfully updated.");
                            dialog.dismiss();
                            Toast.makeText(UpdateProfileActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthRecentLoginRequiredException) {
                            Log.d(TAG, "User must be re-authenticated due to stale credentials.");
                            Toast.makeText(UpdateProfileActivity.this, "Please enter your current password to proceed.", Toast.LENGTH_SHORT).show();
                            currentPasswordLayout.setVisibility(View.VISIBLE);
                            final String currentPassword = Objects.requireNonNull(currentPasswordLayout.getEditText()).getText().toString();

                            // Wait until user enters password
                            if (currentPassword.length() > 0) {
                                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), password);

                                user.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User successfully re-authenticated.");
                                                    updatePassword(password, currentPasswordLayout, dialog, dialogLayout);
                                                } else {
                                                    Log.d(TAG, "Re-authentication complete, but unsuccessful");
                                                    Log.d(TAG, Objects.requireNonNull(task.getException()).getMessage());
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, e.getLocalizedMessage());
                                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                                    currentPasswordLayout.setError("Incorrect password.");
                                                    Button forgot = dialogLayout.findViewById(R.id.update_dialog_forgot_password);
                                                    forgot.setVisibility(View.VISIBLE);
                                                } else {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    public void updatePasswordClick(View view) {
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogLayout = inflater.inflate(R.layout.dialog_update_password, null);
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
                String confirmPassword = Objects.requireNonNull(confirmLayout.getEditText()).getText().toString();
                Log.d(TAG,"Attempting to update password.");

                if (validatePassword(passwordLayout, confirmLayout, password, confirmPassword)) {
                    Log.d(TAG, "Password successfully updated.");
                    updatePassword(password, currentPasswordLayout, dialog, dialogLayout);
                }
            }
        });

        dialog.show();
    }

    private void clearError(TextInputLayout layout) {
        layout.setErrorEnabled(false);
        layout.setError("");
        layout.setErrorEnabled(true);
    }

    public void back(View view) {
        Intent intent = new Intent();
        intent.putExtra("updated", updated);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_LOAD_IMAGE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Get image from device complete.");
                assert data != null;
                ImageView imageView = data.getParcelableExtra("imageView");

                try {
                    final Uri imageUri = data.getData();
                    assert imageUri != null;
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }
}

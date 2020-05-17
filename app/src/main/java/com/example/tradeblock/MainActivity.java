package com.example.tradeblock;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tradeblock.updateprofile.UpdateProfileActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import static com.example.tradeblock.updateprofile.UpdateProfileActivity.AVATAR_UPDATE;
import static com.example.tradeblock.updateprofile.UpdateProfileActivity.DISPLAY_UPDATE;
import static com.example.tradeblock.updateprofile.UpdateProfileActivity.EMAIL_UPDATE;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int RC_UPDATE = 3;
    public static final int IMAGE_SIZE_PX = 200;
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseUser user;
    private ImageView avatarView;
    private TextView displayNameView, emailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Set Header text
        View headerView = navigationView.getHeaderView(0);
        avatarView = headerView.findViewById(R.id.drawerImage);
        displayNameView = headerView.findViewById(R.id.drawerUsername);
        emailView = headerView.findViewById(R.id.drawerEmail);

        assert user != null;
        displayNameView.setText(user.getDisplayName());
        emailView.setText(user.getEmail());
        String url = Objects.requireNonNull(user.getPhotoUrl()).toString();
        Utils.getImageAndPlaceInto(url, avatarView, IMAGE_SIZE_PX, this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        MenuItem account = navigationView.getMenu().getItem(0);
        MenuItem logout = navigationView.getMenu().getItem(4);

        account.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivityForResult(new Intent(MainActivity.this, UpdateProfileActivity.class), RC_UPDATE);
                return false;
            }
        });

        // Add listener to logout button in navigation drawer
        logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "User signed out from nav.");

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void updateNavDrawerText(boolean[] updated) {
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (updated[AVATAR_UPDATE]) {
            Log.d(TAG, "Nav Drawer updated with new profile picture.");
            String url = Objects.requireNonNull(user.getPhotoUrl()).toString();
            Utils.getImageAndPlaceInto(url, avatarView, IMAGE_SIZE_PX, this);
        }
        if (updated[DISPLAY_UPDATE]) {
            Log.d(TAG, "Nav Drawer updated with new display name.");
            displayNameView.setText(user.getDisplayName());
        }
        if (updated[EMAIL_UPDATE]) {
            Log.d(TAG, "Nav Drawer updated with new email.");
            emailView.setText(user.getEmail());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_UPDATE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Profile update finished.");
                assert data != null;
                boolean[] updated = data.getBooleanArrayExtra("updated");
                updateNavDrawerText(updated);
            }
        }
    }
}

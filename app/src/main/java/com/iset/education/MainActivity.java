package com.iset.education;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iset.education.data.models.User;
import com.iset.education.data.models.UserRole;
import com.iset.education.data.repositories.UserRepository;
import com.iset.education.ui.auth.LoginActivity;
import com.iset.education.ui.dashboard.AdminDashboardActivity;
import com.iset.education.ui.dashboard.DashboardActivity;
import com.iset.education.utils.FileUtils;
import com.iset.education.utils.SessionManager;
import android.Manifest;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if user is logged in
        sessionManager = new SessionManager(this);
        boolean isLoggedIn = sessionManager.isLoggedIn();

        if (isLoggedIn) {
            // User is logged in, redirect to DashboardActivity
            Log.d("MainActivity", "onCreate: user is logged in");
            Intent intent;
            if(sessionManager.getUser().getRole().equals(UserRole.ADMIN)){
                intent = new Intent(this, AdminDashboardActivity.class);
            }
            else
                intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        } else {
            Executors.newSingleThreadExecutor().execute(() -> {
                UserRepository userRepository = new UserRepository(getApplication());
                User user = userRepository.getUserByUsername("admin");

                if (user == null) {
                    user = new User();
                    user.setUsername("admin");
                    user.setEmail("admin@isetr.tn");
                    user.setPhoneNumber("55555555");
                    user.setPassword("admin");
                    user.setRole(UserRole.ADMIN);

                    // Load default image from resources
                    Bitmap bitmap = BitmapFactory.decodeResource(getApplication().getResources(), R.mipmap.ic_launcher_foreground);
                        // Convert Bitmap to byte[]
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Set the imageBytes to the user object
                        user.setImage(imageBytes);


                    userRepository.insert(user);
                }
                else
                    Log.d("MainActivity", "onCreate: user already exists");
            });
            // User is not logged in, redirect to AuthActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        // Finish MainActivity
        finish();
    }
}
package com.iset.education;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iset.education.data.models.User;
import com.iset.education.data.models.UserRole;
import com.iset.education.data.repositories.UserRepository;
import com.iset.education.ui.auth.LoginActivity;
import com.iset.education.ui.dashboard.DashboardActivity;
import com.iset.education.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in
        SessionManager sessionManager = new SessionManager(this);
        boolean isLoggedIn = sessionManager.isLoggedIn();

        if (isLoggedIn) {
            // User is logged in, redirect to DashboardActivity
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        } else {
            // User is not logged in, redirect to AuthActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        // Finish MainActivity
        finish();
    }
}
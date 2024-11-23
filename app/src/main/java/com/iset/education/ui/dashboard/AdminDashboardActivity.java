package com.iset.education.ui.dashboard;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.iset.education.R;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new TaskFragment())
                    .commit();
        }

        // Handle navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;


            if (item.getItemId() == R.id.nav_tasks) {
                selectedFragment = new TaskFragment();
            }
            if (item.getItemId() == R.id.nav_users) {
                selectedFragment = new TaskFragment();
            }
            else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }
            else if (item.getItemId() == R.id.nav_courses) {
                selectedFragment = new CourFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });

        // default fragment
        bottomNavigationView.setSelectedItemId(R.id.nav_tasks);
    }
}
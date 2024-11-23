package com.iset.education.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iset.education.R;
import com.iset.education.data.models.User;
import com.iset.education.data.models.UserRole;
import com.iset.education.data.repositories.UserRepository;
import com.iset.education.databinding.ActivityLoginBinding;
import com.iset.education.ui.dashboard.AdminDashboardActivity;
import com.iset.education.ui.dashboard.DashboardActivity;
import com.iset.education.utils.BCryptPass;
import com.iset.education.utils.SessionManager;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button loginButton;
    private ActivityLoginBinding loginBinding;
    private TextView registerRedirectText;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());
        userRepository = new UserRepository(getApplication());

        usernameInput = loginBinding.usernameInput;
        passwordInput = loginBinding.passwordInput;
        loginButton = loginBinding.loginButton;
        registerRedirectText = loginBinding.registerRedirectText;

        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String username = usernameInput.getText().toString();
                        String password = passwordInput.getText().toString();

                        if(username.isEmpty() || password.isEmpty()){
                            return;
                        }

                        Executors.newSingleThreadExecutor().execute(() -> {
                                    User user = userRepository.getUserByUsername(username);
                                    if (user == null) {
                                        runOnUiThread(() -> {
                                            Toast.makeText(LoginActivity.this, "Username or password incorrect", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                    else if (!BCryptPass.checkPassword(password, user.getPassword())) {
                                        runOnUiThread(() -> {
                                            Toast.makeText(LoginActivity.this, "Username or password incorrect", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                    else {
                                        runOnUiThread(() -> {
                                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                            SessionManager sessionManager = new SessionManager(LoginActivity.this);
                                            sessionManager.createSession(user);
                                            Intent intent;
                                            if (user.getRole().equals(UserRole.ADMIN)) {
                                                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                                            }
                                            else
                                                intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                            startActivity(intent);
                                            finish();
                                        });
                                    }
                                });





//                        if (username.equals("admin") && password.equals("admin")) {
//                            // Save login state
//                            SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
//                            SharedPreferences.Editor editor = preferences.edit();
//                            editor.putBoolean("isLoggedIn", true);
//                            editor.apply();
//
//                            // Redirect to DashboardActivity
//                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                        else {
//                            // Show error message
//                            Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
//                        }
                    }
                }
        );

        registerRedirectText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }
}
package com.iset.education.ui.auth;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iset.education.R;
import com.iset.education.data.models.User;
import com.iset.education.data.models.UserRole;
import com.iset.education.data.repositories.UserRepository;
import com.iset.education.databinding.ActivityRegisterBinding;
import com.iset.education.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    private static final int IMAGE_PICK_CODE = 1000;
    private ImageView profileImage;
    private FloatingActionButton selectImageButton;
    private Button registerButton;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Spinner roleSpinner = binding.roleSpinner;
        profileImage = binding.profileImage;
        selectImageButton = binding.selectImageButton;
        registerButton = binding.registerButton;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{UserRole.ETUDIANT.name(), UserRole.ENSEIGNANT.name()}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE_PICK_CODE);
        });
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE_PICK_CODE);
        });

        registerButton.setOnClickListener(v -> onRegister());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri);
            selectImageButton.hide();
            // Convert image to byte[] if needed
        }
    }

    public void onRegister(){
        String username, email, password, cPassword, number, role;
        username = binding.usernameInput.getText().toString();
        email = binding.emailInput.getText().toString();
        password = binding.passwordInput.getText().toString();
        cPassword = binding.confirmPasswordInput.getText().toString();
        number = binding.phoneInput.getText().toString();
        role = binding.roleSpinner.getSelectedItem().toString();

        if(username.isEmpty()){
            binding.usernameInput.setError("Please enter a username");
            return;
        }
        if(username.length() < 4){
            binding.usernameInput.setError("Username must be at least 4 characters");
            return;
        }
        if(username.length() > 20){
            binding.usernameInput.setError("Username must be less than 20 characters");
            return;
        }
        if(username.contains(" ")){
            binding.usernameInput.setError("Username cannot contain spaces");
            return;
        }
        if(username.contains("@")){
            binding.usernameInput.setError("Username cannot contain @");
            return;
        }


        if(email.isEmpty()){
            binding.emailInput.setError("Please enter an email");
            return;
        }
        if(!email.contains("@")){
            binding.emailInput.setError("Please enter a valid email");
            return;
        }
        if(!email.contains(".")){
            binding.emailInput.setError("Please enter a valid email");
            return;
        }
        if(email.length() > 50){
            binding.emailInput.setError("Email must be less than 50 characters");
            return;
        }
        if(email.length() < 5){
            binding.emailInput.setError("Email must be at least 5 characters");
            return;
        }
        if(email.contains(" ")){
            binding.emailInput.setError("Email cannot contain spaces");
            return;
        }

        if(password.isEmpty()){
            binding.passwordInput.setError("Please enter a password");
            return;
        }
        if(password.length() < 8){
            binding.passwordInput.setError("Password must be at least 8 characters");
            return;
        }
        if(password.contains(" ")){
            binding.passwordInput.setError("Password cannot contain spaces");
            return;
        }
        if(cPassword.isEmpty()){
            binding.confirmPasswordInput.setError("Please confirm your password");
            return;
        }
        if(number.isEmpty()){
            binding.phoneInput.setError("Please enter a phone number");
            return;
        }

        if(!password.equals(cPassword)){
            binding.confirmPasswordInput.setError("Passwords do not match");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNumber(number);
        user.setRole(UserRole.valueOf(role));
        user.setImage(null);

        if (selectedImageUri != null) {
            try {
                String filePath = getFilePathFromUri(selectedImageUri);
                if (filePath != null) {
                    user.setImage(FileUtils.fileToBytes(new File(filePath)));
                } else {
                    Toast.makeText(this, "Unable to get file path", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "image problem", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        UserRepository userRepository = new UserRepository(getApplication());
//        if (userRepository.insert(user))
//        {
//            Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//        else
//            Toast.makeText(this, "email or username already exists", Toast.LENGTH_SHORT).show();
        Executors.newSingleThreadExecutor().execute(() -> {
            boolean success = userRepository.insert(user);
            if (success) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
                } else {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Email or username already exists", Toast.LENGTH_SHORT).show();
                });
            }
        });

    }

    private String getFilePathFromUri(Uri uri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}
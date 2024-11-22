package com.iset.education.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iset.education.R;
import com.iset.education.data.models.User;
import com.iset.education.data.repositories.UserRepository;
import com.iset.education.utils.FileUtils;
import com.iset.education.utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    private static final int IMAGE_PICK_CODE = 1000;
    private ImageView imageViewProfile;
    private EditText editTextEmail, editTextPhone, editTextNewPassword, editTextConfirmPassword;
    private TextView editTextName;
    private Button buttonSave, buttonCancel;
    private Uri imageUri;

    private User user;
    private UserRepository userRepository;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddEditUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            user = (User) getArguments().getSerializable("user");
            userRepository = new UserRepository(getActivity().getApplication());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Initialize UI elements
        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        editTextName = view.findViewById(R.id.textViewUsername);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonCancel = view.findViewById(R.id.buttonCancel);

        // Set up listeners
        buttonSave.setOnClickListener(v -> saveUser());
        buttonCancel.setOnClickListener(v -> cancelEdit());
        imageViewProfile.setOnClickListener(v -> openImageChooser());

        if (user != null) {
            editTextName.setText(user.getUsername());
            editTextEmail.setText(user.getEmail());
            editTextPhone.setText(user.getPhoneNumber());

            if (user.getImage() != null) {
                Bitmap profileBitmap = FileUtils.getImage(user.getImage());
                imageViewProfile.setImageBitmap(profileBitmap);
            }

        }

        return view;
    }

    private void saveUser() {
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            // Update user object with new values
            user.setEmail(email);
            user.setPhoneNumber(phone);
            if (imageUri != null){
                try {
                    String filePath = getFilePathFromUri(imageUri);
                    if (filePath != null) {
                        user.setImage(FileUtils.fileToBytes(new File(filePath)));
                    } else {
                        Toast.makeText(getContext(), "Unable to get file path", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "image problem", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (!editTextNewPassword.getText().toString().isEmpty()) {
                if (editTextNewPassword.getText().toString().length() < 8) {
                    Toast.makeText(getContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editTextNewPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())) {
                    user.setPassword(editTextNewPassword.getText().toString());
                } else {
                    Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Executors.newSingleThreadExecutor().execute(() -> {
                userRepository.update(user);
                getActivity().runOnUiThread(() -> {
                    SessionManager sessionManager = new SessionManager(getContext());
                    sessionManager.createSession(user);

                    Toast.makeText(getContext(), "User saved successfully", Toast.LENGTH_SHORT).show();
                    cancelEdit();
                });
            });
        }
    }

    private void cancelEdit() {
        // You can implement any logic needed for cancellation, like closing the fragment or resetting the fields
        getActivity().onBackPressed();
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewProfile.setImageURI(imageUri);
        }
    }

    private String getFilePathFromUri(Uri uri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
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
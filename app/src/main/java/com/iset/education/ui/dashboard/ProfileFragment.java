package com.iset.education.ui.dashboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iset.education.R;
import com.iset.education.data.models.User;
import com.iset.education.data.models.UserRole;
import com.iset.education.data.repositories.UserRepository;
import com.iset.education.ui.auth.LoginActivity;
import com.iset.education.ui.profile.EditProfileFragment;
import com.iset.education.utils.FileUtils;
import com.iset.education.utils.SessionManager;

import java.io.File;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView username, email, phone, role;
    private FloatingActionButton logoutButton;
    private FloatingActionButton editFab;
    private SessionManager sessionManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        }
        sessionManager = new SessionManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Bind views
        profileImage = root.findViewById(R.id.profile_image);
        username = root.findViewById(R.id.profile_username);
        email = root.findViewById(R.id.profile_email);
        phone = root.findViewById(R.id.profile_phone);
        role = root.findViewById(R.id.profile_role);
        logoutButton = root.findViewById(R.id.logout_button);
        editFab = root.findViewById(R.id.fab_edit);

        // Set user data (example - replace with actual user data)
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            username.setText(currentUser.getUsername());
            email.setText("Email: " + currentUser.getEmail());
            phone.setText("Phone: " + currentUser.getPhoneNumber());
            role.setText("Role: " + currentUser.getRole().toString());

            // Decode byte array to Bitmap for profile image
            if (currentUser.getImage() != null) {
                Bitmap profileBitmap = FileUtils.getImage(currentUser.getImage());
                profileImage.setImageBitmap(profileBitmap);
            }
        }

        // Set button listeners
        logoutButton.setOnClickListener(v -> logout());
        editFab.setOnClickListener(v -> navigateToEditProfile());

        return root;
    }

    private User getCurrentUser() {
        // Retrieve current user (implement your logic here)
        return sessionManager.getUser();

    }

    private void logout() {
        // Implement logout logic
//        UserRepository userRepository = new UserRepository(getActivity().getApplication());
//        User user = new User();
//        user.setUsername("admin");
//        user.setEmail("admin@iset.tn");
//        user.setPassword("admin");
//        user.setRole(UserRole.ADMIN);
//        user.setPhoneNumber("93585345");
//
//        Executors.newSingleThreadExecutor().execute(() -> userRepository.insert(user));
        sessionManager.logout();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void navigateToEditProfile() {
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", getCurrentUser());
        editProfileFragment.setArguments(args);
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, editProfileFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
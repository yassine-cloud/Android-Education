package com.iset.education.ui.admin;

import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iset.education.R;
import com.iset.education.data.models.User;
import com.iset.education.data.models.UserRole;
import com.iset.education.data.repositories.UserRepository;

import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDetailFragment extends Fragment {

    private ImageView profileImage;
    private TextView username, email, phone, role;
    private FloatingActionButton delButton, adminBut;
    private Button switchRole;
    private User user;
    private UserRepository userRepository;
    private int userId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserDetailFragment newInstance(String param1, String param2) {
        UserDetailFragment fragment = new UserDetailFragment();
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
            userId = getArguments().getInt("userId");
        }
        userRepository = new UserRepository(requireActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_detail, container, false);

        profileImage = root.findViewById(R.id.profile_image);

        username = root.findViewById(R.id.profile_username);
        email = root.findViewById(R.id.profile_email);
        phone = root.findViewById(R.id.profile_phone);
        role = root.findViewById(R.id.profile_role);
        delButton = root.findViewById(R.id.del_button);
        switchRole = root.findViewById(R.id.switch_role);
        adminBut = root.findViewById(R.id.admin_but);

        reloadUserInformation();

        delButton.setOnClickListener(v -> showDeleteConfirmationDialog());
        switchRole.setOnClickListener(v -> switchUserRole(null));
        adminBut.setOnClickListener(v -> showSwitchAdminConfirmationDialog());


        return root;
    }

    private void reloadUserInformation(){
        Executors.newSingleThreadExecutor().execute(() -> {
            user = userRepository.getUserById(userId);
            if (user != null) {

                // use getActivity() to get the current activity and runOnUiThread to update the UI
                getActivity().runOnUiThread(() -> {
                    // Update UI with user data
                    username.setText(user.getUsername());
                    email.setText("Email: " + user.getEmail());
                    phone.setText("Phone: " + user.getPhoneNumber());
                    role.setText(user.getRole().toString());

                    int roleColor = role.getContext().getResources().getColor(R.color.white);
                    if (user.getRole() == UserRole.ENSEIGNANT) {
                        roleColor = role.getContext().getResources().getColor(R.color.enseignant_color);
                    } else if (user.getRole() == UserRole.ETUDIANT) {
                        roleColor = role.getContext().getResources().getColor(R.color.etudiant_color);
                    } else if (user.getRole() == UserRole.ADMIN) {
                        roleColor = role.getContext().getResources().getColor(R.color.admin_color);
                    }
                    role.setBackgroundTintList(ColorStateList.valueOf(roleColor));


                    // Set profile image if available
                    if (user.getImage() != null) {
                        profileImage.setImageBitmap(BitmapFactory.decodeByteArray(user.getImage(), 0, user.getImage().length));
                    }

                    if (user.getRole() == UserRole.ADMIN) {
                        adminBut.setVisibility(View.GONE);
                        delButton.setVisibility(View.GONE);
                        switchRole.setVisibility(View.VISIBLE);
                        switchRole.setText("Switch to " + (UserRole.ENSEIGNANT).toString());
                        switchRole.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.enseignant_color)));
                    } else {
                        adminBut.setVisibility(View.VISIBLE);
                        switchRole.setVisibility(View.VISIBLE);
                        delButton.setVisibility(View.VISIBLE);
                        if (user.getRole() == UserRole.ENSEIGNANT) {
                            switchRole.setText("Switch to " + (UserRole.ETUDIANT).toString());
                            switchRole.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.etudiant_color)));
                        } else {
                            switchRole.setText("Switch to " + (UserRole.ENSEIGNANT).toString());
                            switchRole.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.enseignant_color)));
                        }

                    }
                });
            } else {
                Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }

    private void deleteUser(){
        Executors.newSingleThreadExecutor().execute(() -> {
            userRepository.delete(user);
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "User deleted", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        });


    }

    private void switchUserRole(UserRole newRole ){

        // Check if user and repository are valid
        if (user == null || userRepository == null) {
            Toast.makeText(getContext(), "User data unavailable", Toast.LENGTH_SHORT).show();
            return;
        }


        Executors.newSingleThreadExecutor().execute(() -> {
            UserRole roleToSet = newRole;

            if (roleToSet == null){
                roleToSet = (user.getRole() == UserRole.ENSEIGNANT) ? UserRole.ETUDIANT : UserRole.ENSEIGNANT;
            }
            user.setRole(roleToSet);
            userRepository.update(user);

            getActivity().runOnUiThread(() -> {
                UserRole role = user.getRole();
                Toast.makeText(getContext(), "User role switched to "+ role.toString(), Toast.LENGTH_SHORT).show();
                reloadUserInformation();
            });
        });

    }

    private void showDeleteConfirmationDialog(){
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteUser();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showSwitchAdminConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Switch to Admin")
                .setMessage("Are you sure you want to switch this user to admin?")
                .setPositiveButton("Switch", (dialog, which) -> {
                    switchUserRole(UserRole.ADMIN);
                })
                .setNegativeButton("Cancel", null)
                .show();

    }
}
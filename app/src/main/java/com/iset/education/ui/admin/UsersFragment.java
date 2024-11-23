package com.iset.education.ui.admin;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iset.education.R;
import com.iset.education.adapter.UserAdapter;
import com.iset.education.data.models.User;
import com.iset.education.data.models.UserRole;
import com.iset.education.data.repositories.UserRepository;
import com.iset.education.ui.dashboard.AdminDashboardActivity;
import com.iset.education.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment implements UserAdapter.OnCourClickListener {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private LiveData<List<User>> userList;
    private UserRepository userRepository;
    private FloatingActionButton fab_switch, fab_dashboard;
    private TextView title;
    private UserRole role;
    private SessionManager sessionManager;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        role = null;
        userRepository = new UserRepository(getActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_users, container, false);
        sessionManager = new SessionManager(requireContext());

        // Set up RecyclerView
        recyclerView = root.findViewById(R.id.recycler_view_users);
        fab_dashboard = root.findViewById(R.id.fab_dashboard);
        fab_switch = root.findViewById(R.id.fab_switch);
        title = root.findViewById(R.id.title);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(getContext(), requireActivity());
        recyclerView.setAdapter(userAdapter);
        userAdapter.setOnCourClickListener(this);

        observeUsers();


        fab_dashboard.setOnClickListener(v -> navigateToDashboard());
        fab_switch.setOnClickListener(v -> switchView());

        return root;
    }

    private void observeUsers() {
        if (role == null){
            title.setText("USERS");
            userList = userRepository.getAllUsers();
            userList.observe(getViewLifecycleOwner(), users -> {
                userAdapter.submitList(users);
            });
        }
        else {
            userList = userRepository.getUserByRole(role.toString());
            title.setText(role.toString());
            if (userList != null)
                userList.observe(getViewLifecycleOwner(), users -> {
                    userAdapter.submitList(users);
                });
            else userAdapter.submitList(new ArrayList<>());
        }
        switchFavIcon(role);

    }

    private void navigateToDashboard(){
        UserDashboardFragment userDashboardFragment = new UserDashboardFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, userDashboardFragment)
                .addToBackStack(null)
                .commit();
    }

    private void switchView(){
        if (role == null){
            role = UserRole.ENSEIGNANT;
        }
        else if (role == UserRole.ENSEIGNANT) {
            role = UserRole.ETUDIANT;
        } else if (role == UserRole.ETUDIANT) {
            role = UserRole.ADMIN;
        }
        else {
            role = null;
        }
        observeUsers();
    }

    private void switchFavIcon(UserRole role){
        if (role == null){
            fab_switch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
            fab_switch.setImageResource(R.drawable.ic_users);
        }
        else if (role == UserRole.ENSEIGNANT){
            fab_switch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.enseignant_color)));
            fab_switch.setImageResource(R.drawable.ic_switch_enseignant);
        }

        else if (role == UserRole.ETUDIANT){
            fab_switch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.etudiant_color)));
            fab_switch.setImageResource(R.drawable.ic_switch_etudiant);
        }

        else if (role == UserRole.ADMIN){
            fab_switch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.admin_color)));
            fab_switch.setImageResource(R.drawable.ic_switch_admin);
        }
    }

    @Override
    public void onUserClick(User user) {
        UserDetailFragment userDetailFragment = new UserDetailFragment();
        Bundle args = new Bundle();
        args.putInt("userId", user.getId());
        userDetailFragment.setArguments(args);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, userDetailFragment)
                .addToBackStack(null)
                .commit();
    }
}
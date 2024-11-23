package com.iset.education.ui.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iset.education.R;
import com.iset.education.data.models.User;
import com.iset.education.data.models.UserRole;
import com.iset.education.data.repositories.CourRepository;
import com.iset.education.data.repositories.UserRepository;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDashboardFragment extends Fragment {

    private TextView textEtudiantCount, textEnseignantCount, textAdminCount, textCourCount;
    private int etudiantCount, enseignantCount, adminCount, courCount;
    private UserRepository userRepository;
    private CourRepository courRepository;
    private FloatingActionButton fab_dashboard;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserDashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserDashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserDashboardFragment newInstance(String param1, String param2) {
        UserDashboardFragment fragment = new UserDashboardFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_dashboard, container, false);

//        initialize views
        textEtudiantCount = root.findViewById(R.id.text_etudiant_count);
        textEnseignantCount = root.findViewById(R.id.text_enseignant_count);
        textAdminCount = root.findViewById(R.id.text_admin_count);
        textCourCount = root.findViewById(R.id.text_courses_count);
        fab_dashboard = root.findViewById(R.id.fab_dashboard);

        userRepository = new UserRepository(requireActivity().getApplication());
        courRepository = new CourRepository(requireActivity().getApplication());

        userRepository.getAllUsersPlus().observe(getViewLifecycleOwner(), users -> {
            etudiantCount = 0;
            enseignantCount = 0;
            adminCount = 0;
            for(User user : users){
                if (user.getRole() == UserRole.ETUDIANT) {
                    etudiantCount++;
                } else if (user.getRole() == UserRole.ENSEIGNANT) {
                    enseignantCount++;
                } else if (user.getRole() == UserRole.ADMIN) {
                    adminCount++;
                }
            }
            textEtudiantCount.setText(String.valueOf(etudiantCount));
            textEnseignantCount.setText(String.valueOf(enseignantCount));
            textAdminCount.setText(String.valueOf(adminCount));
        });

        courRepository.getAllCourses().observe(getViewLifecycleOwner(), courses -> {
            courCount = courses.size();
            textCourCount.setText(String.valueOf(courCount));
        });

        fab_dashboard.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return root;
    }
}
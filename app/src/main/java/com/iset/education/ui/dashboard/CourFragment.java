package com.iset.education.ui.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iset.education.R;
import com.iset.education.adapter.CourAdapter;
import com.iset.education.data.models.Cour;
import com.iset.education.data.models.User;
import com.iset.education.data.models.UserRole;
import com.iset.education.data.repositories.CourRepository;
import com.iset.education.data.repositories.UserRepository;
import com.iset.education.ui.cours.AddEditCourFragment;
import com.iset.education.ui.cours.DetailsCourFragment;
import com.iset.education.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CourFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourFragment extends Fragment implements CourAdapter.OnCourClickListener{

    private RecyclerView recyclerView;
    private CourAdapter coursAdapter;
    private LiveData<List<Cour>> coursList, shownCourses;
    private Spinner instructorSpinner;
    private List<String> instructors ;
    private CourRepository courRepository;
    private UserRepository userRepository;
    private FloatingActionButton fabAdd, fabInstructorCourses;
    private SessionManager sessionManager;
    private boolean isInstructor = false, hisCours = false;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CourFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CourFragment newInstance(String param1, String param2) {
        CourFragment fragment = new CourFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courRepository = new CourRepository(getActivity().getApplication());
        userRepository = new UserRepository(getActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cour, container, false);

        sessionManager = new SessionManager(requireContext());
        isInstructor = sessionManager.getUser().getRole() == UserRole.ENSEIGNANT || sessionManager.getUser().getRole() == UserRole.ADMIN;

        // Set up RecyclerView
        recyclerView = root.findViewById(R.id.recycler_view_cours);
        instructorSpinner = root.findViewById(R.id.spinner_instructors);
        fabAdd = root.findViewById(R.id.fab_add_cours);
        fabInstructorCourses = root.findViewById(R.id.fab_instructor_courses);

        // Hide FABs if the user is not an instructor
        if (!isInstructor) {
            fabAdd.setVisibility(View.GONE);
            fabInstructorCourses.setVisibility(View.GONE);
        }

        // RecyclerView setup
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        coursAdapter = new CourAdapter(getContext(), courRepository, requireActivity());
        coursAdapter.setOnCourClickListener(this);
        recyclerView.setAdapter(coursAdapter);

        instructors = new ArrayList<>();
        instructors.add("All Instructors");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, instructors);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instructorSpinner.setAdapter(spinnerAdapter);

        // Load instructors and observe courses
        loadInstructors();
        observeCourses();

        // Spinner selection listener
        instructorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshCourseList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // FloatingActionButton to add a course
        fabAdd.setOnClickListener(v -> navigateToAddEditCourFragment());

        // FAB for showing current instructor's courses
        fabInstructorCourses.setOnClickListener(v -> {
            hisCours = !hisCours;
            instructorSpinner.setVisibility(hisCours ? View.GONE : View.VISIBLE);
            refreshCourseList();
        });

        return root;
    }

    private void loadInstructors() {
        courRepository.getInstructors().observe(getViewLifecycleOwner(), instructorList -> {
            if (instructorList != null) {
                instructors.clear();
                instructors.add("All Instructors");
                instructors.addAll(instructorList);
                ((ArrayAdapter) instructorSpinner.getAdapter()).notifyDataSetChanged();
            }
        });
    }

    private void observeCourses() {
        coursList = courRepository.getAllCourses();
        coursList.observe(getViewLifecycleOwner(), courses -> {
            if (!hisCours && instructorSpinner.getSelectedItemPosition() == 0) {
                coursAdapter.submitList(courses);
            }
        });
    }

    private void refreshCourseList() {
        if (hisCours) {
            // Show only the current instructor's courses
            String currentInstructor = sessionManager.getUser().getUsername();
            courRepository.getCoursesByInstructor(currentInstructor).observe(getViewLifecycleOwner(), instructorCourses -> {
                coursAdapter.submitList(instructorCourses);
            });
        } else if (instructorSpinner.getSelectedItemPosition() > 0) {
            // Show courses for the selected instructor
            String selectedInstructor = instructors.get(instructorSpinner.getSelectedItemPosition());
            courRepository.getCoursesByInstructor(selectedInstructor).observe(getViewLifecycleOwner(), filteredCourses -> {
                coursAdapter.submitList(filteredCourses);
            });
        } else {
            // Show all courses
            coursList = courRepository.getAllCourses();
            coursList.observe(getViewLifecycleOwner(), courses ->
                    coursAdapter.submitList(courses));
        }
    }

    private void navigateToAddEditCourFragment() {
        AddEditCourFragment addEditCourFragment = new AddEditCourFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, addEditCourFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCourClick(Cour cour) {
        Executors.newSingleThreadExecutor().execute(() -> {
            DetailsCourFragment detailsCourFragment = new DetailsCourFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("courId", cour.getId());
            User enseignant = userRepository.getUserByUsername(cour.getInstructor());
            bundle.putInt("enseignantId", enseignant.getId());
            detailsCourFragment.setArguments(bundle);

            getActivity().runOnUiThread(() -> {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, detailsCourFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            });
        });
    }

}
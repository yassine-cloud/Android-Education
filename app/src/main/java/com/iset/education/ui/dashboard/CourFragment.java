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
import com.iset.education.data.repositories.CourRepository;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cour, container, false);
        sessionManager = new SessionManager(requireContext());

        // Set up RecyclerView
        recyclerView = root.findViewById(R.id.recycler_view_cours);
        instructorSpinner = root.findViewById(R.id.spinner_instructors);
        fabAdd = root.findViewById(R.id.fab_add_cours);
        fabInstructorCourses = root.findViewById(R.id.fab_instructor_courses);

        // RecyclerView setup
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        coursAdapter = new CourAdapter(getContext(), courRepository, requireActivity());
        coursAdapter.setOnCourClickListener(this);
        recyclerView.setAdapter(coursAdapter);

        // Initialize instructors list
        instructors = new ArrayList<>();
        instructors.add("All Instructors");

        // Fetch courses and instructors
        courRepository.getAllCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null) {
                coursAdapter.submitList(courses);
            } else {
                coursAdapter.submitList(new ArrayList<>());
            }
        });

        courRepository.getInstructors().observe(getViewLifecycleOwner(), instructorList -> {
            if (instructorList != null) {
                instructors.addAll(instructorList);
                // Update spinner adapter when instructors are fetched
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_item,
                        instructors
                );
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                instructorSpinner.setAdapter(spinnerAdapter);
            }
        });

        // Spinner selection listener
        instructorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedInstructor = instructors.get(position);
                if (selectedInstructor.equals("All Instructors")) {
                    courRepository.getAllCourses().observe(getViewLifecycleOwner(), courses -> {
                        coursAdapter.submitList(courses);
                    });
                } else {
                    courRepository.getCoursesByInstructor(selectedInstructor).observe(getViewLifecycleOwner(), filteredCourses -> {
                        coursAdapter.submitList(filteredCourses);
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // FloatingActionButton to add a course
        fabAdd.setOnClickListener(v -> {
            AddEditCourFragment addEditCourFragment = new AddEditCourFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, addEditCourFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // FloatingActionButton to show the current instructor's courses
        fabInstructorCourses.setOnClickListener(v -> {
            if(!hisCours){
                String currentInstructor = sessionManager.getUser().getUsername();
                instructorSpinner.setVisibility(View.GONE);
                courRepository.getCoursesByInstructor(currentInstructor).observe(getViewLifecycleOwner(), instructorCourses -> {
                    coursAdapter.submitList(instructorCourses);
                });
            }
            else{
                instructorSpinner.setVisibility(View.VISIBLE);
                instructorSpinner.setSelection(0);
                courRepository.getAllCourses().observe(getViewLifecycleOwner(), courses -> {
                    coursAdapter.submitList(courses);
                });
            }
            hisCours = !hisCours;
        });

        return root;
    }

    @Override
    public void onCourClick(Cour cour) {
        // Navigate to DetailsCourFragment
        DetailsCourFragment detailsCourFragment = new DetailsCourFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("cour", cour);
        detailsCourFragment.setArguments(bundle);

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, detailsCourFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
package com.iset.education.ui.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iset.education.R;
import com.iset.education.adapter.TaskAdapter;
import com.iset.education.data.database.TaskDao;
import com.iset.education.data.models.Task;
import com.iset.education.data.repositories.TaskRepository;
import com.iset.education.ui.tasks.AddEditTaskFragment;
import com.iset.education.ui.tasks.DetailsTaskFragment;
import com.iset.education.utils.SessionManager;
import com.iset.education.viewmodels.TaskViewModel;
import com.iset.education.viewmodels.TaskViewModelFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment implements TaskAdapter.OnTaskClickListener {
    private TaskAdapter adapter;
    private TaskRepository taskRepository;
    private LiveData<List<Task>> allTasks, shownTasks;
    private FloatingActionButton fabAddTask, fabDashboard, fabToggleStatus;
    private TextView tasksLabel;
    private Boolean statusSelected = false;
    private SessionManager sessionManager;
    int userId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskFragment newInstance(String param1, String param2) {
        TaskFragment fragment = new TaskFragment();
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
        taskRepository = new TaskRepository(getActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_task, container, false);
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUser().getId();

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tasksLabel = root.findViewById(R.id.tasks_label);
        fabAddTask = root.findViewById(R.id.fab_add_task);
        fabDashboard = root.findViewById(R.id.fab_dashboard);
        fabToggleStatus = root.findViewById(R.id.fab_toggle_status);

        adapter = new TaskAdapter(getContext(), taskRepository, requireActivity());
        recyclerView.setAdapter(adapter);

        allTasks = taskRepository.getAllTasks();
        updateShownTasks(); // Update shownTasks initially based on statusSelected

//        adapter.setOnTaskClickListener(task -> {
//            AddEditTaskFragment addEditTaskFragment = new AddEditTaskFragment();
//            Bundle args = new Bundle();
//            args.putSerializable("task", task);
//            addEditTaskFragment.setArguments(args);
//
//        });
        adapter.setOnTaskClickListener(this);  // Set the listener

//        if (allTasks != null)
//                allTasks.observe(getViewLifecycleOwner(), tasks -> adapter.submitList(tasks));
//        else
//            adapter.submitList(new ArrayList<>());

        fabAddTask.setOnClickListener(v -> {
            AddEditTaskFragment addEditTaskFragment = new AddEditTaskFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, addEditTaskFragment)
                    .addToBackStack(null)
                    .commit();
        });

        fabDashboard.setOnClickListener(v -> {
            TaskDashboardFragment taskDashboardFragment = new TaskDashboardFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, taskDashboardFragment)
                    .addToBackStack(null)
                    .commit();
        });

        fabToggleStatus.setOnClickListener(v -> {
            statusSelected = !statusSelected; // Toggle the statusSelected flag
            updateShownTasks(); // Update the tasks based on the new statusSelected value
        });

        return root;
    }

    private void updateShownTasks() {
        // Filter tasks based on completion status (completed vs incomplete)
        if (statusSelected == true) {
            // Show completed tasks
            tasksLabel.setText(R.string.completed_tasks);
            shownTasks = taskRepository.getCompletedTasks(userId);
            fabToggleStatus.setImageResource(R.drawable.ic_check_circle);
        } else {
            // Show incomplete tasks
            tasksLabel.setText(R.string.pending_tasks);
            shownTasks = taskRepository.getUncompletedTasks(userId);
            fabToggleStatus.setImageResource(R.drawable.ic_uncheck);
        }
        // Observe changes in shownTasks LiveData
        shownTasks.observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null) {
                adapter.submitList(tasks);
            } else {
                adapter.submitList(new ArrayList<>());
            }
        });
    }

    @Override
    public void onTaskClick(Task task) {
        // Create a new instance of DetailsTaskFragment
        DetailsTaskFragment detailsTaskFragment = new DetailsTaskFragment();

        // Pass task data to the fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("task", task);  // Pass the task object to the fragment
        detailsTaskFragment.setArguments(bundle);

        // Replace the current fragment with AddEditTaskFragment
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, detailsTaskFragment);  // Replace with your container's ID
        transaction.addToBackStack(null);  // Allows the user to press back to return to the previous fragment
        transaction.commit();
    }


}
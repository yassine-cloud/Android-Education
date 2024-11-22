package com.iset.education.ui.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iset.education.R;
import com.iset.education.adapter.TaskAdapter;
import com.iset.education.data.repositories.TaskRepository;
import com.iset.education.utils.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskDashboardFragment extends Fragment {
    private RecyclerView recyclerCompletedTasks, recyclerPendingTasks;
    private TaskAdapter completedTasksAdapter, pendingTasksAdapter;
    private TaskRepository taskRepository;
    private TextView textCompletedCount, textPendingCount;
    private SessionManager sessionManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TaskDashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskDashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskDashboardFragment newInstance(String param1, String param2) {
        TaskDashboardFragment fragment = new TaskDashboardFragment();
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
        // Inflate the layout
        View root = inflater.inflate(R.layout.fragment_task_dashboard, container, false);
        sessionManager = new SessionManager(requireContext());
        int userId = sessionManager.getUser().getId();

        // Initialize views
        recyclerCompletedTasks = root.findViewById(R.id.recycler_completed_tasks);
        recyclerPendingTasks = root.findViewById(R.id.recycler_pending_tasks);
        textCompletedCount = root.findViewById(R.id.text_completed_count);
        textPendingCount = root.findViewById(R.id.text_pending_count);

        // Initialize adapters
        completedTasksAdapter = new TaskAdapter(null, taskRepository, requireActivity());
        pendingTasksAdapter = new TaskAdapter(null, taskRepository, requireActivity());

        // Set up RecyclerViews
        recyclerCompletedTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerCompletedTasks.setAdapter(completedTasksAdapter);

        recyclerPendingTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPendingTasks.setAdapter(pendingTasksAdapter);

        // Initialize repository
        taskRepository = new TaskRepository(requireActivity().getApplication());

        // Observe completed tasks
        taskRepository.getCompletedTasks(userId).observe(getViewLifecycleOwner(), tasks -> {
            completedTasksAdapter.submitList(tasks);
            textCompletedCount.setText(String.valueOf(tasks.size()));
        });

        // Observe pending tasks
        taskRepository.getUncompletedTasks(userId).observe(getViewLifecycleOwner(), tasks -> {
            pendingTasksAdapter.submitList(tasks);
            textPendingCount.setText(String.valueOf(tasks.size()));
        });

        return root;
    }
}
package com.iset.education.ui.tasks;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iset.education.R;
import com.iset.education.data.models.Task;
import com.iset.education.data.repositories.TaskRepository;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsTaskFragment extends Fragment {

    private Task task;
    private TaskRepository taskRepository;

    private TextView taskTitle, taskDescription, taskDueDate, taskStatus;
    private Button btnDeleteTask, btnMarkCompleted, btnUpdateTask;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DetailsTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailsTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsTaskFragment newInstance(String param1, String param2) {
        DetailsTaskFragment fragment = new DetailsTaskFragment();
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
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
            task = (Task) getArguments().getSerializable("task");
        }
        taskRepository = new TaskRepository(requireActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_details_task, container, false);

        taskTitle = root.findViewById(R.id.task_title);
        taskDescription = root.findViewById(R.id.task_description);
        taskDueDate = root.findViewById(R.id.task_due_date);
        taskStatus = root.findViewById(R.id.task_status);
        btnDeleteTask = root.findViewById(R.id.btn_delete_task);
        btnMarkCompleted = root.findViewById(R.id.btn_mark_completed);
        btnUpdateTask = root.findViewById(R.id.btn_update_task);

        // Display task details
        if (task != null) {
            taskTitle.setText(task.getTitle());
            taskDescription.setText(task.getDescription());
            taskDueDate.setText("Due Date: " + task.getDueDate());
            taskStatus.setText("Status: " + (task.isCompleted() ? "Completed" : "Pending"));
        }

        // Delete Task
        btnDeleteTask.setOnClickListener(v -> {
            // Delete the task from the database
            new Thread(() -> {
                taskRepository.delete(task);
                requireActivity().runOnUiThread(() -> {
                    // Show a message or navigate back
                    Toast.makeText(getContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                });
            }).start();
        });

        // Mark Task as Completed
        btnMarkCompleted.setOnClickListener(v -> {
            // Toggle completion status
            task.setCompleted(!task.isCompleted());
            new Thread(() -> {
                taskRepository.update(task); // Update task in the database
                requireActivity().runOnUiThread(() -> {
                    taskStatus.setText("Status: " + (task.isCompleted() ? "Completed" : "Pending"));
                    Toast.makeText(getContext(), "Task status updated", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });

        // Update Task
        btnUpdateTask.setOnClickListener(v -> {
            // Navigate to AddEditTaskFragment to update the task
            AddEditTaskFragment addEditTaskFragment = new AddEditTaskFragment();
            Bundle args = new Bundle();
            args.putSerializable("task", task);
            addEditTaskFragment.setArguments(args);
            getParentFragmentManager().popBackStack();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, addEditTaskFragment)
                    .addToBackStack(null)
                    .commit();


        });

        return root;
    }
}
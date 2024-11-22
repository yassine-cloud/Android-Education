package com.iset.education.ui.tasks;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.iset.education.R;
import com.iset.education.data.models.Task;
import com.iset.education.data.repositories.TaskRepository;
import com.iset.education.databinding.FragmentAddEditTaskBinding;
import com.iset.education.utils.SessionManager;
import com.iset.education.viewmodels.TaskViewModel;
import com.iset.education.viewmodels.TaskViewModelFactory;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddEditTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEditTaskFragment extends Fragment {

    private EditText taskTitleInput, taskDescriptionInput, taskDueDateInput;
    private CheckBox taskCompletedCheckbox;
    private Button saveTaskButton;
    private SessionManager sessionManager;
    private int userId;
//    private TaskViewModel taskViewModel;

    private TaskRepository taskRepository;

    private Task task;

    private int taskId = -1; // Default value for new task

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddEditTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddEditTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddEditTaskFragment newInstance() {
        AddEditTaskFragment fragment = new AddEditTaskFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            task = (Task) getArguments().getSerializable("task");
            if (task != null) taskId = task.getId();
        }
        taskRepository = new TaskRepository(getActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_task, container, false);
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUser().getId();

        taskTitleInput = view.findViewById(R.id.taskTitleInput);
        taskDescriptionInput = view.findViewById(R.id.taskDescriptionInput);
        taskDueDateInput = view.findViewById(R.id.taskDueDateInput);
        taskCompletedCheckbox = view.findViewById(R.id.taskCompletedCheckbox);
        saveTaskButton = view.findViewById(R.id.saveTaskButton);

        // Date picker for due date
        taskDueDateInput.setOnClickListener(v -> showDatePickerDialog());

        if (task != null) {
            taskId = task.getId();
            taskTitleInput.setText(task.getTitle());
            taskDescriptionInput.setText(task.getDescription());
            taskDueDateInput.setText(task.getDueDate());
            taskCompletedCheckbox.setChecked(task.isCompleted());
        }

//        TaskViewModelFactory factory = new TaskViewModelFactory(new TaskRepository(getActivity().getApplication()));
//        taskViewModel = new ViewModelProvider(this, factory).get(TaskViewModel.class);



        saveTaskButton.setOnClickListener(v -> {
            String taskTitle = taskTitleInput.getText().toString().trim();
            String taskDescription = taskDescriptionInput.getText().toString().trim();
            String dueDate = taskDueDateInput.getText().toString().trim();
            boolean isCompleted = taskCompletedCheckbox.isChecked();

            if (TextUtils.isEmpty(taskTitle)) {
                Toast.makeText(getContext(), "Task title cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(dueDate)) {
                Toast.makeText(getContext(), "Due date cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(taskDescription)) {
                Toast.makeText(getContext(), "Task description cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (taskId == -1) {
                addTask(taskTitle, taskDescription, dueDate, isCompleted);
            } else {
                updateTask(taskId, taskTitle, taskDescription, dueDate, isCompleted);
            }
        });

        return view;
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> taskDueDateInput.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void addTask(String title, String description, String dueDate, boolean isCompleted) {
        // Add task logic
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setCompleted(isCompleted);
        task.setUserId(userId);
        taskRepository.insert(task);
        Toast.makeText(getContext(), "Task added successfully", Toast.LENGTH_SHORT).show();
        getParentFragmentManager().popBackStack();
    }

    private void updateTask(int id, String title, String description, String dueDate, boolean isCompleted) {
        // Update task logic
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setCompleted(isCompleted);
        task.setUserId(userId);
        taskRepository.update(task);
        Toast.makeText(getContext(), "Task updated successfully", Toast.LENGTH_SHORT).show();
        getParentFragmentManager().popBackStack();
    }


}
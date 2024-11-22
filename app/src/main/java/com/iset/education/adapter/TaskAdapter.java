package com.iset.education.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.iset.education.R;
import com.iset.education.data.models.Task;
import com.iset.education.data.repositories.TaskRepository;
import com.iset.education.ui.tasks.AddEditTaskFragment;
import com.iset.education.ui.tasks.DetailsTaskFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskHolder> {
    private OnTaskClickListener taskClickListener;
    private Context context;
    private TaskRepository taskRepository;
    private FragmentActivity requireActivity;

    public TaskAdapter(Context context, TaskRepository taskRepository, FragmentActivity requireActivity) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.taskRepository = taskRepository;
        this.requireActivity = requireActivity;
    }

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Task>() {
                @Override
                public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        Task currentTask = getItem(position);
        holder.textViewTitle.setText(currentTask.getTitle());
//        holder.textViewDescription.setText(currentTask.getDescription());
        holder.taskDueDate.setText(currentTask.getDueDate());
        holder.taskStatus.setText(currentTask.isCompleted() ? "Completed" : "Pending");
        if (currentTask.isCompleted()) {
            holder.taskStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.green));
        } else {
            holder.taskStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
        }

        if (!currentTask.isCompleted()) {

            // Get the current date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date dueDate = null;
            try {
                dueDate = sdf.parse(currentTask.getDueDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
            currentCalendar.set(Calendar.MINUTE, 0);
            currentCalendar.set(Calendar.SECOND, 0);
            currentCalendar.set(Calendar.MILLISECOND, 0);

            Calendar dueCalendar = Calendar.getInstance();
            dueCalendar.setTime(dueDate);
            dueCalendar.set(Calendar.HOUR_OF_DAY, 0);
            dueCalendar.set(Calendar.MINUTE, 0);
            dueCalendar.set(Calendar.SECOND, 0);
            dueCalendar.set(Calendar.MILLISECOND, 0);

            if (dueCalendar.before(currentCalendar)) {
                holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.overdue));
            } else if (dueCalendar.equals(currentCalendar)) {
                holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.due_today));
            } else {
                holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.upcoming));
            }
        }



        holder.itemView.setOnClickListener(v -> {
            if (taskClickListener != null) {
                taskClickListener.onTaskClick(currentTask);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            showTaskOptionsMenu(v, currentTask);
            return true;
        });
    }

    private void showTaskOptionsMenu(View view, Task task) {
        if (context == null) return ;
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.task_options_menu);

        // Handling menu item clicks
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_details) {
                // Navigate to DetailsTaskFragment
//                taskClickListener.onTaskClick(task);
                navigateToTaskDetails(task);
                return true;
            }
            else if (item.getItemId() == R.id.menu_edit) {
                navigateToEditTask(task);
                    return true;
            } else if (item.getItemId() == R.id.menu_complete_uncomplete) {
                task.setCompleted(!task.isCompleted());
                taskRepository.update(task);
                    notifyItemChanged(getCurrentList().indexOf(task));
                    Toast.makeText(context, task.isCompleted() ? "Task Completed" : "Task Uncompleted", Toast.LENGTH_SHORT).show();
                    return true;
            }
            else if (item.getItemId() == R.id.menu_delete) {
                deleteTask(task);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void deleteTask(Task task) {
        // Implement your delete logic here (e.g., remove from database, etc.)
        taskRepository.delete(task);
        notifyItemRemoved(getCurrentList().indexOf(task));
        Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show();
    }

    private void navigateToTaskDetails(Task task) {
        // Create a new instance of DetailsTaskFragment
        DetailsTaskFragment detailsTaskFragment = new DetailsTaskFragment();

        // Pass task data to the fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("task", task);  // Pass the task object to the fragment
        detailsTaskFragment.setArguments(bundle);

        // Replace the current fragment with AddEditTaskFragment
        FragmentTransaction transaction = requireActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, detailsTaskFragment);  // Replace with your container's ID
        transaction.addToBackStack(null);  // Allows the user to press back to return to the previous fragment
        transaction.commit();
    }

    private void navigateToEditTask(Task task) {
        // Navigate to AddEditTaskFragment to update the task
        AddEditTaskFragment addEditTaskFragment = new AddEditTaskFragment();
        Bundle args = new Bundle();
        args.putSerializable("task", task);
        addEditTaskFragment.setArguments(args);
        requireActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, addEditTaskFragment)
                .addToBackStack(null)
                .commit();
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.taskClickListener = listener;
    }

    class TaskHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView taskDueDate;
        private TextView taskStatus;

        public TaskHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.taskTitle);
//            textViewDescription = itemView.findViewById(R.id.taskDescription);
            taskDueDate = itemView.findViewById(R.id.taskDueDate);
            taskStatus = itemView.findViewById(R.id.taskStatus);
        }
    }

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }
}
package com.iset.education.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.iset.education.data.models.Task;
import com.iset.education.data.repositories.TaskRepository;

import java.util.List;

public class TaskViewModel extends ViewModel {
    private TaskRepository repository;
    private LiveData<List<Task>> allTasks;

    public TaskViewModel(TaskRepository taskRepository) {
        this.repository = taskRepository;
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void insert(Task task) {
        repository.insert(task);
    }
}
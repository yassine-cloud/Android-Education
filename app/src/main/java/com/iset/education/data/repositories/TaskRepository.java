package com.iset.education.data.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.iset.education.data.database.TaskDao;
import com.iset.education.data.database.TaskDatabase;
import com.iset.education.data.models.Task;
import com.iset.education.utils.SessionManager;

import java.util.List;

public class TaskRepository {
    private TaskDao taskDao;
    private LiveData<List<Task>> allTasks;

    public TaskRepository(Application application) {
        TaskDatabase db = TaskDatabase.getInstance(application);
        taskDao = db.taskDao();
        SessionManager sessionManager = new SessionManager(application);
        int userId = sessionManager.getUser().getId();
        allTasks = taskDao.getAllTasks(userId);
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void insert(Task task) {
        new Thread(() -> taskDao.insert(task)).start();
    }

    public void update(Task task) {
        new Thread(() -> taskDao.update(task)).start();
    }

    public void delete(Task task) {
        new Thread(() -> taskDao.delete(task)).start();
    }

    public LiveData<List<Task>> getCompletedTasks(int userId) {
        return taskDao.getCompletedTasks(userId);
    }

    public LiveData<List<Task>> getUncompletedTasks(int userId) {
        return taskDao.getUncompletedTasks(userId);
    }

    public Task getTaskById(int taskId) {
        return taskDao.getTaskById(taskId);
    }

}
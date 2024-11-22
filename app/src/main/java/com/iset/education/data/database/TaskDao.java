package com.iset.education.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.iset.education.data.models.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY dueDate")
    LiveData<List<Task>> getAllTasks(int userId);

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 AND userId = :userId ORDER BY dueDate")
    LiveData<List<Task>> getCompletedTasks(int userId);

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND userId = :userId ORDER BY dueDate")
    LiveData<List<Task>> getUncompletedTasks(int userId);

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    Task getTaskById(int taskId);
}

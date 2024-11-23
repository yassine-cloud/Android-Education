package com.iset.education.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.iset.education.data.models.Cour;

import java.util.List;

@Dao
public interface CourDao {

    @Insert
    void insert(Cour course);

    @Update
    void update(Cour course);

    @Delete
    void delete(Cour course);

    @Query("SELECT * FROM cours ORDER BY schedule DESC, name ASC")
    LiveData<List<Cour>> getAllCourses();

    @Query("SELECT * FROM cours WHERE id = :courseId")
    Cour getCourseById(int courseId);

    @Query("SELECT * FROM cours WHERE name LIKE '%' || :searchQuery || '%' ORDER BY schedule DESC")
    LiveData<List<Cour>> searchCourses(String searchQuery);

    @Query("SELECT * FROM cours WHERE instructor LIKE '%' || :searchQuery || '%' ORDER BY schedule DESC")
    LiveData<List<Cour>> searchCoursesByInstructor(String searchQuery);

    @Query("SELECT DISTINCT instructor FROM cours ORDER BY schedule DESC, instructor ASC")
    LiveData<List<String>> getInstructorNames();
}
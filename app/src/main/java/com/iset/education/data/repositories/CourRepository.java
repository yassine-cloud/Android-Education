package com.iset.education.data.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.iset.education.data.database.CourDao;
import com.iset.education.data.database.CourDatabase;
import com.iset.education.data.models.Cour;

import java.util.List;

public class CourRepository {

    private CourDao courseDao;
    private LiveData<List<Cour>> allCourses;

    public CourRepository(Application application) {
        CourDatabase db = CourDatabase.getInstance(application);
        courseDao = db.courseDao();
        allCourses = courseDao.getAllCourses();
    }

    public LiveData<List<Cour>> getAllCourses() {
        return allCourses;
    }

    public void insert(Cour course) {
        new Thread(() -> courseDao.insert(course)).start();
    }

    public void update(Cour course) {
        new Thread(() -> courseDao.update(course)).start();
    }

    public void delete(Cour course) {
        new Thread(() -> courseDao.delete(course)).start();
    }

    public Cour getCourseById(int courseId) {
        return courseDao.getCourseById(courseId);
    }
    public LiveData<List<Cour>> getCoursesByInstructor(String instructor) {
        return courseDao.searchCoursesByInstructor(instructor);
    }

    public LiveData<List<Cour>> getCoursesByName(String name) {
        return courseDao.searchCourses(name);
    }

    public LiveData<List<String>> getInstructors() {
        return courseDao.getInstructorNames();
    }
}

package com.iset.education.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.iset.education.data.models.Cour;
import com.iset.education.data.repositories.CourRepository;

import java.util.List;

public class CourseViewModel extends AndroidViewModel {

    private CourRepository repository;
    private LiveData<List<Cour>> allCourses;

    public CourseViewModel(@NonNull Application application) {
        super(application);
        repository = new CourRepository(application);
        allCourses = repository.getAllCourses();
    }

    public LiveData<List<Cour>> getAllCourses() {
        return allCourses;
    }

    public void insert(Cour course) {
        repository.insert(course);
    }

    public void update(Cour course) {
        repository.update(course);
    }

    public void delete(Cour course) {
        repository.delete(course);
    }

}

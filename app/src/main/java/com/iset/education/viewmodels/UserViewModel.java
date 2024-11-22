package com.iset.education.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.iset.education.data.models.User;
import com.iset.education.data.repositories.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private UserRepository repository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    public void insert(User user) {
        repository.insert(user);
    }

    public User authenticate(String username, String password) {
        return repository.authenticate(username, password);
    }

    public void update(User user) {
        repository.update(user);

    }
}

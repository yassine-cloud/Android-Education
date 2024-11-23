package com.iset.education.data.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.iset.education.data.database.UserDao;
import com.iset.education.data.database.UserDatabase;
import com.iset.education.data.models.User;
import com.iset.education.utils.BCryptPass;
import com.iset.education.utils.SessionManager;

import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class UserRepository {

    private UserDao userDao;
    private LiveData<List<User>> allUsers;
    private SessionManager sessionManager;

    public UserRepository(Application application) {
        UserDatabase db = UserDatabase.getInstance(application);
        sessionManager = new SessionManager(application);
        userDao = db.userDao();
        allUsers = userDao.getAllUsers();
    }

    public User authenticate(String username, String password) {

        User user = getUserByUsername(username);
        if (user != null && BCryptPass.checkPassword(password, user.getPassword())) {
            return user;
        }
        return null;

    }

    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers(sessionManager.getUser().getId());
    }

    public LiveData<List<User>> getAllUsersPlus() {
        return userDao.getAllUsers();
    }

    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    public LiveData<List<User>> getUserByRole(String role) {
        return userDao.getUserByRole(role, sessionManager.getUser().getId());
    }

    public boolean insert(User user) {
        User existingUser = getUserByUsername(user.getUsername());
        if (existingUser != null) {
            return false;
        }
        existingUser = getUserByEmail(user.getEmail());
        if (existingUser != null) {
            return false;
        }
        user.setPassword(BCryptPass.hashPassword(user.getPassword()));
        new Thread(() -> userDao.insert(user)).start();
        return true;
    }

    public void update(User user) {
        User existingUser = getUserById(user.getId());
        if( user.getPassword() != null && !user.getPassword().equals(existingUser.getPassword()) ){
            user.setPassword(BCryptPass.hashPassword(user.getPassword()));
        }
        else {
            user.setPassword(existingUser.getPassword());
        }
        new Thread(() -> userDao.update(user)).start();
    }

    public void deleteUserById(int userId) {
        new Thread(() -> userDao.deleteUserById(userId)).start();
    }

    public void delete(User user) {
        new Thread(() -> userDao.delete(user)).start();
    }

}

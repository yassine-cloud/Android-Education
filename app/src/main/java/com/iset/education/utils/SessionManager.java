package com.iset.education.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Base64;

import androidx.core.app.NotificationCompat;

import com.iset.education.R;
import com.iset.education.data.models.User;
import com.iset.education.data.models.UserRole;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private static final String CHANNEL_ID = "user_session_notifications";
    private static final String CHANNEL_NAME = "User Session Notifications";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        createNotificationChannel();
    }

    public void createSession(User user) {
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_ROLE, user.getRole().toString());
        editor.putString(KEY_PHONE_NUMBER, user.getPhoneNumber());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        // Convert image byte array to Base64 string
        if (user.getImage() != null) {
            String imageBase64 = Base64.encodeToString(user.getImage(), Base64.DEFAULT);
            editor.putString(KEY_PROFILE_IMAGE, imageBase64);
        }

        editor.apply();

        // send login notification
        sendNotification("Welcome, " + user.getUsername() + "!", "You have successfully logged in.");
    }

    public User getUser() {
        User user = new User();
        user.setId(sharedPreferences.getInt(KEY_USER_ID, -1));
        user.setUsername(sharedPreferences.getString(KEY_USERNAME, null));
        user.setEmail(sharedPreferences.getString(KEY_EMAIL, null));
        user.setRole(UserRole.valueOf(sharedPreferences.getString(KEY_ROLE, "USER")));
        user.setPhoneNumber(sharedPreferences.getString(KEY_PHONE_NUMBER, null));

        // Convert Base64 string back to byte array for image
        String imageBase64 = sharedPreferences.getString(KEY_PROFILE_IMAGE, null);
        if (imageBase64 != null) {
            user.setImage(Base64.decode(imageBase64, Base64.DEFAULT));
        }

        return user;
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logout() {

        // Send logout notification before clearing session
        String username = sharedPreferences.getString(KEY_USERNAME, "User");
        sendNotification("Goodbye, " + username + "!", "You have successfully logged out.");


        editor.clear();
        editor.apply();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for user session events");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Replace with your notification icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

}

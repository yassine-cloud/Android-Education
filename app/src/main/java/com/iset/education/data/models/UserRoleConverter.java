package com.iset.education.data.models;

import androidx.room.TypeConverter;

public class UserRoleConverter {
    @TypeConverter
    public static String fromUserRole(UserRole role) {
        return role == null ? null : role.name(); // Convert enum to String
    }

    @TypeConverter
    public static UserRole toUserRole(String role) {
        return role == null ? null : UserRole.valueOf(role); // Convert String to enum
    }
}

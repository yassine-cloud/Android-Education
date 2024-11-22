package com.iset.education.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
    public static String formatDate(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return formatter.format(new Date(timestamp));
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
}

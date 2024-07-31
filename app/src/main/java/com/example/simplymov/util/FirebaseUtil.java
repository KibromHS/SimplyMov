package com.example.simplymov.util;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class FirebaseUtil {
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static String timestampToTimeString(Timestamp timestamp) {
        if (timestamp == null) timestamp = Timestamp.now();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        return dateFormat.format(timestamp.toDate());
    }

    public static String timestampToDateString(Timestamp timestamp) {
        if (timestamp == null) timestamp = Timestamp.now();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return dateFormat.format(timestamp.toDate());
    }
}

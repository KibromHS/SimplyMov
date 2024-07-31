package com.example.movdispatcher.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.movdispatcher.DispatcherCallback;
import com.example.movdispatcher.models.Dispatcher;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Util {
    public static String timestampToTimeString(Timestamp timestamp) {
        if (timestamp == null) {
            timestamp = Timestamp.now();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        return dateFormat.format(timestamp.toDate());
    }

    public static String timestampToDateString(Timestamp timestamp) {
        if (timestamp == null) {
            timestamp = Timestamp.now();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return dateFormat.format(timestamp.toDate());
    }

    public static void signIn(String email, String password, DispatcherCallback callback) {
        FirebaseFirestore.getInstance().collection("dispatchers").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() == 0) return;
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.getString("email").equals(email) && documentSnapshot.getString("password").equals(password)) {
                        Dispatcher dispatcher = documentSnapshot.toObject(Dispatcher.class);
                        callback.onDispatcherReceivedCallback(dispatcher);
                        return;
                    }
                }
                callback.onDispatcherNotFoundCallback("Dispatcher not found");
            }
        });
    }
}

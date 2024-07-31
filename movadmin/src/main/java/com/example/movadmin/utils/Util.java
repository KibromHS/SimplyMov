package com.example.movadmin.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Util {
    public static void showDispatcherConfirmationDialog(Context context, String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to remove this dispatcher?");

        builder.setPositiveButton("Yes", (dialog, which) -> FirebaseFirestore.getInstance().collection("dispatchers").document(id).delete().addOnSuccessListener(unused -> Toast.makeText(context, "Dispatcher removed", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(context, "Couldn't remove dispatcher", Toast.LENGTH_SHORT).show()));

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showDriverConfirmationDialog(Context context, String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to remove this driver?");

        builder.setPositiveButton("Yes", (dialog, which) -> FirebaseFirestore.getInstance().collection("drivers").document(id).delete().addOnSuccessListener(unused -> Toast.makeText(context, "Driver removed", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(context, "Couldn't remove driver", Toast.LENGTH_SHORT).show()));

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
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

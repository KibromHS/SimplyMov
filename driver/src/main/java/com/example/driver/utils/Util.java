package com.example.driver.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import com.example.driver.DriverCallback;
import com.example.driver.models.Driver;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Util {
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

    public static void signIn(String email, String password, DriverCallback callback) {
        FirebaseFirestore.getInstance().collection("drivers").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() == 0) return;
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.getString("driverEmail").equals(email) && documentSnapshot.getString("password").equals(password)) {
                        Driver driver = documentSnapshot.toObject(Driver.class);
                        callback.onDriverReceived(driver);
                        return;
                    }
                }
                callback.onDriverNotFound("Driver not found");
            }
        });
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        float distanceInMeters = results[0];

        return distanceInMeters / 1000;
    }

    public static LatLng geocodeAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        address += " Addis Ababa";
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address addressResult = addresses.get(0);
                return new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

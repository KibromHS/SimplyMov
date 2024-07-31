package com.example.simplymov.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapUtil {
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

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        float distanceInMeters = results[0];

        return distanceInMeters / 1000;
    }
}

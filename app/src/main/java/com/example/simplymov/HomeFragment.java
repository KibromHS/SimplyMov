package com.example.simplymov;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simplymov.models.UserModel;
import com.example.simplymov.util.UserPreferences;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int REQUEST_CODE = 444;
    private static final float DEFAULT_ZOOM = 16;
    private static final LatLng ADDIS_ABABA = new LatLng(9.0192, 38.7525);

    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private Location currentLocation;

    private LinearLayout toPackersBtn, toMovingBtn;
    private TextView toMoveNowBtn, walletBalance;
    private ImageView toAbout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        getLocationPermission();

        FirebaseFirestore.getInstance().collection("users").document(UserPreferences.getUser().getUserId()).get().addOnSuccessListener(documentSnapshot -> {
            UserModel userModel = documentSnapshot.toObject(UserModel.class);

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            assert userModel != null;
            String userBalance = "ETB " + decimalFormat.format(userModel.getBalance());
            walletBalance.setText(userBalance);
        });

        walletBalance.setOnClickListener(v -> {
            Intent i = new Intent(view.getContext(), PaymentOptionActivity.class);
            view.getContext().startActivity(i);
        });

        toPackersBtn.setOnClickListener(v -> {
            Intent i = new Intent(view.getContext(), PackersServiceActivity.class);
            view.getContext().startActivity(i);
        });

        toMovingBtn.setOnClickListener(v -> {
            Intent i = new Intent(view.getContext(), MovingServiceActivity.class);
            view.getContext().startActivity(i);
        });

        toMoveNowBtn.setOnClickListener(v -> {
            Intent i = new Intent(view.getContext(), MoveNowActivity.class);
            view.getContext().startActivity(i);
        });

        toAbout.setOnClickListener(v -> {
            Intent i = new Intent(view.getContext(), AboutActivity.class);
            view.getContext().startActivity(i);
        });

        return view;
    }

    private void initView(View view) {
        toPackersBtn = view.findViewById(R.id.toPackers);
        toMovingBtn = view.findViewById(R.id.toMoving);
        toMoveNowBtn = view.findViewById(R.id.toMoveNowBtn);
        walletBalance = view.findViewById(R.id.walletBalance);
        toAbout = view.findViewById(R.id.toAbout);
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private void getLocationPermission() {
        String[] permissions = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if ((ContextCompat.checkSelfPermission(getContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            mLocationPermissionGranted = true;
            initMap();
        } else {
            ActivityCompat.requestPermissions((Activity) getContext(), permissions, REQUEST_CODE);
        }
    }

    private void moveCamera(LatLng latLng, @NonNull String title) {
        if (title.equals("My Location")) {
//            mMap.addMarker(getMarkerOptions(latLng, title, true));
        } else {
            mMap.addMarker(new MarkerOptions().title(title).position(latLng));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }

    private void getDeviceLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        try {
            if (mLocationPermissionGranted) {
                Task<Location> location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            currentLocation = task.getResult();
                            if (currentLocation != null) {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), "My Location");
                            } else {
                                Toast.makeText(getContext(), "Null location", Toast.LENGTH_SHORT).show();
                                moveCamera(ADDIS_ABABA, "My Location");
                            }
                        } else {
                            Toast.makeText(getContext(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        mLocationPermissionGranted = false;

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                mLocationPermissionGranted = true;
                initMap();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(getContext(), FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }
}
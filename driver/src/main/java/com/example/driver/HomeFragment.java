package com.example.driver;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.driver.models.Order;
import com.example.driver.models.UserModel;
import com.example.driver.utils.UserPreferences;
import com.example.driver.utils.Util;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int REQUEST_CODE = 444;
    private static final float DEFAULT_ZOOM = 12;
    private static final LatLng ADDIS_ABABA = new LatLng(9.0192, 38.7525);

//    private ImageView userImage;
//    private TextView usernameAndText, userPhone;
//    private TextView orderDate, orderTime, orderPrice, orderPickup, orderDropOff, orderSize, orderKms;
//    private TextView declineBtn, acceptBtn;
//    private ConstraintLayout incomingOrder;
    private Button deliveredBtn;

    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private Location currentLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homes, container, false);

        deliveredBtn = view.findViewById(R.id.deliveredBtn);
        getLocationPermission(view);
        return view;
    }

//    private void initView(View view) {
//        userImage = view.findViewById(R.id.userImage);
//        usernameAndText = view.findViewById(R.id.usernameAndText);
//        userPhone = view.findViewById(R.id.userPhone);
//        orderDate = view.findViewById(R.id.orderDate);
//        orderTime = view.findViewById(R.id.orderTime);
//        orderPrice = view.findViewById(R.id.orderPrice);
//        orderPickup = view.findViewById(R.id.pickupLocation);
//        orderDropOff = view.findViewById(R.id.dropoffLocation);
//        orderSize = view.findViewById(R.id.orderSize);
//        orderKms = view.findViewById(R.id.orderKms);
//        declineBtn = view.findViewById(R.id.declineBtn);
//        acceptBtn = view.findViewById(R.id.acceptBtn);
//        incomingOrder = view.findViewById(R.id.incomingOrder);
//        deliveredBtn = view.findViewById(R.id.deliveredBtn);
//    }

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
                                LatLng myPos = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                moveCamera(myPos);
                            } else {
                                Toast.makeText(getContext(), "Please turn on your location", Toast.LENGTH_SHORT).show();
                                moveCamera(ADDIS_ABABA);
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

    private void getNewOrders() {
        FirebaseFirestore.getInstance().collection("orders").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) return;
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType().equals(DocumentChange.Type.ADDED)) {
                        Order order = documentChange.getDocument().toObject(Order.class);
                        if (!UserPreferences.getUser().getDriverStatus().equals("AVAILABLE")) {
                            if (order.getDriverId() != null && order.getDriverId().equals(UserPreferences.getUser().getDriverId())) {
                                if (!order.getStatus().equals("DELIVERED") && !order.getStatus().equals("CANCELLED")) {
                                    getUser(order.getUserId(), user -> showDialog(order, user));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void getUser(String userId, UserCallback callback) {
        FirebaseFirestore.getInstance().collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                callback.onUserReceived(userModel);
            }
        });
    }

//    private void showIncomingOrder(Order order, UserModel user) {
//        incomingOrder.setVisibility(View.VISIBLE);
//
//        Glide.with(requireContext()).load(user.getProfilePic()).into(userImage);
//        String builder = user.getUsername() + " wants to move with you";
//        usernameAndText.setText(builder);
//        userPhone.setText(user.getPhoneNo());
////        orderDate.setText(Util.timestampToDateString(order.getDate()));
////        orderTime.setText(Util.timestampToTimeString(order.getDate()));
//
//        DecimalFormat decimalFormat = new DecimalFormat("#.##");
//        String price = "ETB " + decimalFormat.format(order.getPrice());
//        orderPrice.setText(price);
//
//        orderPickup.setText(order.getFrom());
//        orderDropOff.setText(order.getTo());
//        orderSize.setText(order.getSize());
//
//        String kms = decimalFormat.format(order.getKms()) + " Kms";
//        orderKms.setText(kms);
//
//        declineBtn.setOnClickListener(v -> {
//            incomingOrder.setVisibility(View.VISIBLE);
//            FirebaseFirestore.getInstance().collection("orders").document(order.getOrderId()).update("status", "CANCELLED");
//            Toast.makeText(getContext(), "Order Declined", Toast.LENGTH_SHORT).show();
//        });
//
//        acceptBtn.setOnClickListener(v -> FirebaseFirestore.getInstance().collection("orders").document(order.getOrderId()).update("driverId", UserPreferences.getUser().getDriverId()).addOnSuccessListener(unused -> FirebaseFirestore.getInstance().collection("orders").document(order.getOrderId()).update("status", "OUT_FOR_DELIVERY").addOnSuccessListener(unused1 -> {
//            LatLng pickup = Util.geocodeAddress(getContext(), order.getFrom());
//            LatLng dropOff = Util.geocodeAddress(getContext(), order.getTo());
//            Marker pickupMarker = mMap.addMarker(getMarkerOptions(pickup, "Pickup Location"));
//            Marker dropOffMarker = mMap.addMarker(getMarkerOptions(dropOff, "Drop Off Location"));
//            incomingOrder.setVisibility(View.VISIBLE);
//            moveCamera(pickup);
//            deliveredBtn.setVisibility(View.VISIBLE);
//            deliveredBtn.setOnClickListener(view -> {
//                FirebaseFirestore.getInstance().collection("orders").document(order.getOrderId()).update("status", "DELIVERED");
//                FirebaseFirestore.getInstance().collection("drivers").document(UserPreferences.getUser().getDriverId()).update("driverStatus", "AVAILABLE");
//                FirebaseFirestore.getInstance().collection("drivers").document(UserPreferences.getUser().getDriverId()).update("numOfOrders", FieldValue.increment(1));
//                FirebaseFirestore.getInstance().collection("drivers").document(UserPreferences.getUser().getDriverId()).update("totalKms", FieldValue.increment(Util.calculateDistance(pickup.latitude, pickup.longitude, dropOff.latitude, dropOff.longitude)));
//                pickupMarker.remove();
//                dropOffMarker.remove();
//                deliveredBtn.setVisibility(View.GONE);
//                Toast.makeText(getContext(), "Order Delivered", Toast.LENGTH_SHORT).show();
//            });
//            Toast.makeText(getContext(), "Order Accepted", Toast.LENGTH_SHORT).show();
//        })));
//    }

    private void moveCamera(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }

    @NonNull
    private MarkerOptions getMarkerOptions(LatLng latLng, String title) {
        return new MarkerOptions().position(latLng).title(title);
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private void getLocationPermission(View view) {
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

    private void showDialog(Order order, UserModel user) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_new_order);

        ImageView userImage = dialog.findViewById(R.id.userImage);
        TextView usernameAndText = dialog.findViewById(R.id.usernameAndText);
        TextView userPhone = dialog.findViewById(R.id.userPhone);
        TextView orderDate = dialog.findViewById(R.id.orderDate);
        TextView orderTime = dialog.findViewById(R.id.orderTime);
        TextView orderPickup = dialog.findViewById(R.id.pickupLocation);
        TextView orderDropOff = dialog.findViewById(R.id.dropoffLocation);
        TextView orderPrice = dialog.findViewById(R.id.orderPrice);
        TextView orderSize = dialog.findViewById(R.id.orderSize);
        TextView orderKms = dialog.findViewById(R.id.orderKms);
        TextView acceptBtn = dialog.findViewById(R.id.acceptBtn);
        TextView declineBtn = dialog.findViewById(R.id.declineBtn);



        Glide.with(requireContext()).load(user.getProfilePic()).into(userImage);
        String builder = user.getUsername() + " wants to move with you";
        usernameAndText.setText(builder);
        userPhone.setText(user.getPhoneNo());
        orderDate.setText(Util.timestampToDateString(order.getDateTime()));
        orderTime.setText(Util.timestampToTimeString(order.getDateTime()));

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String price = "ETB " + decimalFormat.format(order.getPrice());
        orderPrice.setText(price);

        orderPickup.setText(order.getFrom());
        orderDropOff.setText(order.getTo());
        orderSize.setText(order.getSize());

        String kms = decimalFormat.format(order.getKms()) + " Kms";
        orderKms.setText(kms);

        declineBtn.setOnClickListener(v -> {
            dialog.dismiss();
            FirebaseFirestore.getInstance().collection("orders").document(order.getOrderId()).update("status", "CANCELLED");
            FirebaseFirestore.getInstance().collection("drivers").document(order.getDriverId()).update("driverStatus", "AVAILABLE");
            Toast.makeText(getContext(), "Order Declined", Toast.LENGTH_SHORT).show();
        });

        acceptBtn.setOnClickListener(v -> FirebaseFirestore.getInstance().collection("orders").document(order.getOrderId()).update("driverId", UserPreferences.getUser().getDriverId()).addOnSuccessListener(unused -> FirebaseFirestore.getInstance().collection("orders").document(order.getOrderId()).update("status", "OUT_FOR_DELIVERY").addOnSuccessListener(unused1 -> {
            LatLng pickup = Util.geocodeAddress(getContext(), order.getFrom());
            LatLng dropOff = Util.geocodeAddress(getContext(), order.getTo());
            Marker pickupMarker = mMap.addMarker(getMarkerOptions(pickup, "Pickup Location"));
            Marker dropOffMarker = mMap.addMarker(getMarkerOptions(dropOff, "Drop Off Location"));
            dialog.dismiss();
            moveCamera(pickup);
            Toast.makeText(getContext(), "Order Accepted", Toast.LENGTH_SHORT).show();
            deliveredBtn.setVisibility(View.VISIBLE);
            deliveredBtn.setOnClickListener(view1 -> {
                FirebaseFirestore.getInstance().collection("orders").document(order.getOrderId()).update("status", "DELIVERED");
                FirebaseFirestore.getInstance().collection("drivers").document(UserPreferences.getUser().getDriverId()).update("driverStatus", "AVAILABLE");
                FirebaseFirestore.getInstance().collection("drivers").document(UserPreferences.getUser().getDriverId()).update("numOfOrders", FieldValue.increment(1));
                FirebaseFirestore.getInstance().collection("drivers").document(UserPreferences.getUser().getDriverId()).update("totalKms", FieldValue.increment(Util.calculateDistance(pickup.latitude, pickup.longitude, dropOff.latitude, dropOff.longitude)));
                pickupMarker.remove();
                dropOffMarker.remove();
                deliveredBtn.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Order Delivered", Toast.LENGTH_SHORT).show();
            });

        })));

        dialog.show();
    }

    private void amIOnDuty() {
        FirebaseFirestore.getInstance().collection("orders").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) return;
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Order order = documentSnapshot.toObject(Order.class);
                assert order != null;
                if (order.getDriverId() != null && order.getDriverId().equals(UserPreferences.getUser().getDriverId()) && order.getStatus().equals("OUT_FOR_DELIVERY") && UserPreferences.getUser().getDriverStatus().equals("NOT AVAILABLE")) {
                    deliveredBtn.setVisibility(View.VISIBLE);
//                    getUser(order.getUserId(), user -> showDialog(order, user));

                    LatLng pickup = Util.geocodeAddress(getContext(), order.getFrom());
                    LatLng dropOff = Util.geocodeAddress(getContext(), order.getTo());



                    Marker pickupMarker = mMap.addMarker(getMarkerOptions(pickup, "Pickup Location"));
                    Marker dropOffMarker = mMap.addMarker(getMarkerOptions(dropOff, "Drop Off Location"));

                    moveCamera(pickup);

                    deliveredBtn.setOnClickListener(v -> {
                        FirebaseFirestore.getInstance().collection("orders").document(order.getOrderId()).update("status", "DELIVERED");
                        FirebaseFirestore.getInstance().collection("drivers").document(UserPreferences.getUser().getDriverId()).update("driverStatus", "AVAILABLE");
                        FirebaseFirestore.getInstance().collection("drivers").document(UserPreferences.getUser().getDriverId()).update("numOfOrders", FieldValue.increment(1));
                        FirebaseFirestore.getInstance().collection("drivers").document(UserPreferences.getUser().getDriverId()).update("totalKms", FieldValue.increment(Util.calculateDistance(pickup.latitude, pickup.longitude, dropOff.latitude, dropOff.longitude)));
                        pickupMarker.remove();
                        dropOffMarker.remove();
                        deliveredBtn.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Order Delivered", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }
            }
            getNewOrders();
        });
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
            amIOnDuty();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }
}
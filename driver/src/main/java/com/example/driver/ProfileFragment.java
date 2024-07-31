package com.example.driver;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.driver.models.Driver;
import com.example.driver.utils.UserPreferences;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;

public class ProfileFragment extends Fragment {
    private ImageView profilePic, toEditBtn;
    private TextView edUsername, edEmail, edPhone, totalOrder, totalKms, logoutBtn, rating;
    private View statusIndicator;
    private RatingBar ratingBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initView(view);
        Driver userModel = UserPreferences.getUser();
        assert userModel != null;
        FirebaseFirestore.getInstance().collection("drivers").document(userModel.getDriverId()).get().addOnSuccessListener(documentSnapshot -> {
            Driver driver = documentSnapshot.toObject(Driver.class);

            assert driver != null;
            Glide.with(view.getContext()).load(driver.getDriverPic()).into(profilePic);
            edUsername.setText(driver.getDriverName());
            edEmail.setText(driver.getDriverEmail());
            edPhone.setText(driver.getPhoneNumber());
            totalOrder.setText(String.valueOf(driver.getNumOfOrders()));
            ratingBar.setRating((float) driver.getRating());

            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            String rate = decimalFormat.format(driver.getRating());
            rating.setText(rate);

            statusIndicator.setBackgroundColor(driver.getDriverStatus().equals("AVAILABLE") ? 0xFF00FF00 : 0xFFFF0000);

            DecimalFormat df = new DecimalFormat("#.##");
            String formattedKms = df.format(driver.getTotalKms()) + " kms";
            totalKms.setText(formattedKms);
        });

        toEditBtn.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), EditProfileActivity.class);
            i.putExtra("driver", userModel);
            startActivity(i);
        });

        logoutBtn.setOnClickListener(v -> {
            UserPreferences.clearPreferences();
            Intent i = new Intent(getContext(), LoginActivity.class);
            startActivity(i);
        });

        return view;
    }

    private void initView(View view) {
        profilePic = view.findViewById(R.id.settingsProfilePic);
        edUsername = view.findViewById(R.id.edSettingsUsername);
        edEmail = view.findViewById(R.id.settingsEmail);
        edPhone = view.findViewById(R.id.edPhone);
        totalOrder = view.findViewById(R.id.totalOrder);
        totalKms = view.findViewById(R.id.kms);
        toEditBtn = view.findViewById(R.id.toEditBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        statusIndicator = view.findViewById(R.id.statusIndicator);
        ratingBar = view.findViewById(R.id.driverRating);
        rating = view.findViewById(R.id.rating);
    }
}
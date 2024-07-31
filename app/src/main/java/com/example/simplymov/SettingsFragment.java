package com.example.simplymov;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.simplymov.models.UserModel;
import com.example.simplymov.util.UserPreferences;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;

public class SettingsFragment extends Fragment {
    private ImageView profilePic, toEditBtn;
    private TextView edUsername, edEmail, totalOrder, totalKms, logoutBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initView(view);
        UserModel userModel = UserPreferences.getUser();
        assert userModel != null;
        Glide.with(view.getContext()).load(userModel.getProfilePic()).into(profilePic);
        edUsername.setText(userModel.getUsername());
        edEmail.setText(userModel.getPhoneNo());
        totalOrder.setText(String.valueOf(userModel.getNumOfOrders()));

        DecimalFormat df = new DecimalFormat("#.##");
        String formattedKms = df.format(userModel.getTotalKms()) + " KM";
        totalKms.setText(formattedKms);

        toEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EditSettingsActivity.class);
                i.putExtra("user", userModel);
                startActivity(i);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getContext(), IntroductionActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

    private void initView(View view) {
        profilePic = view.findViewById(R.id.settingsProfilePic);
        edUsername = view.findViewById(R.id.edSettingsUsername);
        edEmail = view.findViewById(R.id.settingsEmail);
        totalOrder = view.findViewById(R.id.totalOrder);
        totalKms = view.findViewById(R.id.kms);
        toEditBtn = view.findViewById(R.id.toEditBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);
    }
}
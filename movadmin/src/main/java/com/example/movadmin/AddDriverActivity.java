package com.example.movadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddDriverActivity extends AppCompatActivity {
    private String truckSize;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);

        EditText edName = findViewById(R.id.edName);
        EditText edEmail = findViewById(R.id.edEmail);
        EditText edPhone = findViewById(R.id.edPhone);
        EditText edPassword = findViewById(R.id.edPassword);
        EditText edConfirm = findViewById(R.id.edConfirm);
        TextView addBtn = findViewById(R.id.addBtn);
        ImageView dispatcherPic = findViewById(R.id.customerPic);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            switch (radioGroup1.getCheckedRadioButtonId()) {
                case R.id.rbSmall:
                    truckSize = "SMALL";
                    break;
                case R.id.rbMedium:
                    truckSize = "MEDIUM";
                    break;
                case R.id.rbLarge:
                    truckSize = "LARGE";
                    break;
            }
        });


        Glide.with(this).load("https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/no-profile-picture-icon.png").into(dispatcherPic);

        addBtn.setOnClickListener(view -> {
            String name = edName.getText().toString();
            String email = edEmail.getText().toString();
            String phone = edPhone.getText().toString();
            String password = edPassword.getText().toString();
            String confirm = edConfirm.getText().toString();
            String status = "AVAILABLE";
            int numOfOrders = 0;
            int totalKms = 0;

            if (!password.equals(confirm)) {
                Toast.makeText(this, "Confirm password correctly", Toast.LENGTH_SHORT).show();
                return;
            }

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (truckSize.isEmpty()) {
                Toast.makeText(this, "Check truck size", Toast.LENGTH_SHORT).show();
                return;
            }

            uploadDispatcherData(name, phone, email, password, status, numOfOrders, totalKms);
        });
    }

    private void uploadDispatcherData(String name, String phone, String email, String password, String status, int numOfOrders, int totalKms) {
        Map<String, Object> data = new HashMap<>();
        data.put("driverName", name);
        data.put("phoneNumber", phone);
        data.put("driverEmail", email);
        data.put("driverPic", "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/no-profile-picture-icon.png");
        data.put("password", password);
        data.put("numOfOrders", numOfOrders);
        data.put("totalKms", totalKms);
        data.put("driverStatus", status);
        data.put("truckSize", truckSize);
        data.put("rating", 0.0);
        data.put("numOfRates", 0);

        FirebaseFirestore.getInstance().collection("drivers").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String driverId = documentReference.getId();
                FirebaseFirestore.getInstance().collection("drivers").document(driverId).update("driverId", driverId).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddDriverActivity.this, "Driver added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddDriverActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
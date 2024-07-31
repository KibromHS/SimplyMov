package com.example.driver;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.driver.models.Driver;
import com.example.driver.utils.UserPreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView backBtn, doneBtn, profilePic;
    private EditText edUsername, edPhone, edPassword, edConfirm;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private Driver userModel;
    private RadioGroup radioGroup;
    private String status;

    private final ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        imageUri = data.getData();
                        profilePic.setImageURI(imageUri);
                    }
                }
            });

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initView();

        edUsername.setText(userModel.getDriverName());
        edPhone.setText(userModel.getPhoneNumber());
        edPassword.setText(userModel.getPassword());
        edConfirm.setText(userModel.getPassword());
        radioGroup.check(userModel.getDriverStatus().equals("AVAILABLE") ? R.id.available : R.id.notAvailable);
        Glide.with(this).load(userModel.getDriverPic()).into(profilePic);

        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.available:
                    status = "AVAILABLE";
                    break;
                case R.id.notAvailable:
                    status = "NOT AVAILABLE";
                    break;
            }
        });

        backBtn.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        profilePic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imageActivityResultLauncher.launch(intent);
        });

        doneBtn.setOnClickListener(v -> {
            String username = edUsername.getText().toString();
            String phone = edPhone.getText().toString();
            String password = edPassword.getText().toString();
            String confirm = edConfirm.getText().toString();

            if (username.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Fill in all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(EditProfileActivity.this, "Confirm password correctly", Toast.LENGTH_SHORT).show();
                return;
            }

            if (status == null || status.isEmpty()) {
                Toast.makeText(this, "Check your status", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog = new ProgressDialog(EditProfileActivity.this);
            progressDialog.setTitle("Updating Profile...");
            progressDialog.show();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_HH_mm_ss", Locale.ENGLISH);
            Date now = new Date();
            String fileName = formatter.format(now);

            if (imageUri == null) {
                uploadUpdatedData(username, phone, password, status);
            } else {
                uploadUpdatedDataWithImage(fileName, username, phone, password, status);
            }
        });
    }

    private void uploadUpdatedData(String username, String phone, String password, String status) {

        Map<String, Object> updates = new HashMap<>();
        updates.put("driverName", username);
        updates.put("phoneNumber", phone);
        updates.put("password", password);
        updates.put("driverStatus", status);

        FirebaseFirestore.getInstance().collection("drivers").document(userModel.getDriverId())
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Driver u = UserPreferences.getUser();
                        assert u != null;
                        u.setDriverName(username);
                        u.setPhoneNumber(phone);
                        u.setDriverStatus(status);
                        u.setPassword(password);
                        UserPreferences.setUser(u);
                        Toast.makeText(EditProfileActivity.this, "Successfully Updated Profile", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadUpdatedDataWithImage(String fileName, String username, String phone, String password, String status) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("DriverImages/" + fileName);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                StorageReference fileRef = FirebaseStorage.getInstance().getReference("DriverImages");
                StorageReference ref = fileRef.child(fileName);
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (username.isEmpty()) {
                            Toast.makeText(EditProfileActivity.this, "Please Fill in the empty fields", Toast.LENGTH_SHORT).show();
                        } else {
                            Driver d = new Driver(userModel.getDriverId(), username, phone, userModel.getDriverEmail(), password, uri.toString(), status, userModel.getTruckSize(), userModel.getNumOfOrders(), userModel.getTotalKms(), userModel.getRating(), userModel.getNumOfRates());
                            FirebaseFirestore.getInstance().collection("drivers").document(userModel.getDriverId()).set(d);
                            UserPreferences.setUser(d);
                            Toast.makeText(EditProfileActivity.this, "Successfully Updated Profile", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Can't get download url", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        backBtn = findViewById(R.id.editBackBtn);
        doneBtn = findViewById(R.id.doneEditBtn);
        profilePic = findViewById(R.id.settingsProfilePic);
        edUsername = findViewById(R.id.edSettingsUsername);
        edPhone = findViewById(R.id.edPhone);
        edPassword = findViewById(R.id.edPassword);
        edConfirm = findViewById(R.id.edConfirm);
        radioGroup = findViewById(R.id.radioGroup);
        userModel = (Driver) getIntent().getSerializableExtra("driver");
    }
}
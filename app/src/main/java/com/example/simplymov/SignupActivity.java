package com.example.simplymov;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.simplymov.models.UserModel;
import com.example.simplymov.util.FirebaseUtil;
import com.example.simplymov.util.UserPreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {
    private ImageView doneBtn, profilePic;
    private EditText edUsername;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private String phoneNumber;
    private TextView signupPhone;
    private static final String DEFAULT_IMAGE_URL = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBwgHBgkIBwgGCgkGBwoHBwYGBg8UFQYWIB0WIhURHxMYHSggGBolGx8fITEhJSkrLi4uFx8zODMsNygtLisBCgoKBQUFDgUFDisZExkrKysrKysrKysrKysrKysrKysrKysrKysrKysrKysrKysrKysrKysrKysrKysrKysrKysrK//AABEIAMgAyAMBIgACEQEDEQH/xAAaAAEAAwEBAQAAAAAAAAAAAAAAAwQFAgEH/8QANBABAQACAAMGAgYLAQAAAAAAAAIBAwQREiEiMTJSYhNyBTNTgqHBI0FCUWFxgZGSk7EU/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAH/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwD6oAqAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA8qpic1WcYmfNVM/fxV3nOIzmY/GwXNvEa9XZVc6+znxVb4+s57kTj5u1UAWP/bu/fH+t1PHbMeM6s/d5KoC/HHRnzxc/L2rWM4zjGceFMZJp33pz3c85+zrwBrDjVsnbGKn70+h2AAAAAAAAAAAAAAABnPLGc58J7wM/j9ma2/D59kT5f4qr26zV5rPjVdTxQAAAAABZ4C8zu6OfZctFkaKzO/XnHrlroAAAAAAAAAAAAAACPfnlo2fJSRHvxz0bMeygZICgAAAAADqM9NzXpqabDFbWPBAAAAAAAAAAAAAAARb9k69ec3z5V3e6lVuPxz0Yz6bkGcAoAAAAAA9xy545+DW1XOyMXPVyr1Mhp8Hjlw8e7qr8UE4AAAAAAAAAAAAACHip6+HvHpnq/smeVOKnM58KnpoGMJeI0fAqcdXVivL3USgAAAAAA19U9GqJ9MqHDcN8bHVnPKZr/JpIAAAAAAAAAAAAAAAAKv0hHVqm8fsV/wBZ7ZqcXOZrwqemmTu151bMxnt6fLXqwDgBQAABJw+v4u3E58vmr+QL/CR0aJxnxr9JX9U4IAAAAAAAAAAAAAAAADP+kfrp+T8167iJ53Uzj3MzitmNu3qnyzPTIIgFAABa+jvrq+T81VNwuzGrdiq8tT00DUHMXFzzisVj2ukAAAAAAAAAAAAAEezdr1+e5xn0z25BI52XGueq6xjClt47OeeNU8vdStV1ddVVnOfVQGy6u81Wc573d6nIKAAAAAAOtd1F4qc5x3u90tbXcbJ6orGcMd1N1FdU1nGfVKDYFDVx2ccsbZ5+6VvXu17PJc5z6a7MgkAAAAAABDxO74Mc8dt13YkHW3dGrHO6+WZ8aVL4+s/VxOPds7VWqq6zVZznNeaqeAkviNt+a88vTPYjBQAAAAAAAAAAAAABJHEbY8t55emu1PHH1j6yJz7tfYqANbVujbjnFfNNeMpGNNVFYqc5xmfLUtPht3xo557Lnu3KCYABm8dWa35x+qJmQBXAUAAAAAAAAAAAAAAAAAAFjgazO/GP1XNSCDSAB//Z";

    private final ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            imageUri = data.getData();
                            profilePic.setImageURI(imageUri);
                        } else {
                            Toast.makeText(SignupActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();

        profilePic.setImageURI(Uri.parse(DEFAULT_IMAGE_URL));
        signupPhone.setText(phoneNumber);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imageActivityResultLauncher.launch(intent);
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(SignupActivity.this);
                progressDialog.setTitle("Creating Profile...");
                progressDialog.show();

                String username = edUsername.getText().toString().trim();

                if (username.isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_HH_mm_ss", Locale.ENGLISH);
                String fileName = formatter.format(new Date());

                if (imageUri == null) {
                    uploadUpdatedData(username);
                } else {
                    uploadUpdatedDataWithImage(fileName, username);
                }
            }
        });
    }

    private void uploadUpdatedData(String username) {
        UserModel newUser = new UserModel(FirebaseUtil.currentUserId(), username, phoneNumber, DEFAULT_IMAGE_URL, 0, 0.0, 300);
        saveUserData(newUser);
    }

    private void uploadUpdatedDataWithImage(String fileName, String username) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Images/" + fileName);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                UserModel newUser = new UserModel(FirebaseUtil.currentUserId(), username, phoneNumber, uri.toString(), 0, 0.0, 300);
                                saveUserData(newUser);
                            })
                            .addOnFailureListener(e -> {
                                if (progressDialog.isShowing()) progressDialog.dismiss();
                                Toast.makeText(SignupActivity.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserData(UserModel user) {
        FirebaseFirestore.getInstance().collection("users").document(FirebaseUtil.currentUserId()).set(user)
                .addOnSuccessListener(aVoid -> {
                    UserPreferences.setUser(user);
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Successfully Created Profile", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                })
                .addOnFailureListener(e -> {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void initView() {
        doneBtn = findViewById(R.id.doneEditBtn);
        profilePic = findViewById(R.id.settingsProfilePic);
        edUsername = findViewById(R.id.edSettingsUsername);
        signupPhone = findViewById(R.id.signupPhone);
        phoneNumber = getIntent().getStringExtra("number");
    }
}

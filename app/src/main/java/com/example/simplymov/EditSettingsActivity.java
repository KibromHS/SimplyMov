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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditSettingsActivity extends AppCompatActivity {
    private ImageView backBtn, doneBtn, profilePic;
    private EditText edUsername;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private UserModel userModel;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_settings);
        initView();

        edUsername.setText(userModel.getUsername());
        Glide.with(this).load(userModel.getProfilePic()).into(profilePic);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

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
                progressDialog = new ProgressDialog(EditSettingsActivity.this);
                progressDialog.setTitle("Updating Profile...");
                progressDialog.show();

                String username = edUsername.getText().toString();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_HH_mm_ss", Locale.ENGLISH);
                Date now = new Date();
                String fileName = formatter.format(now);

                if (imageUri == null) {
                    uploadUpdatedData(username);
                } else {
                    uploadUpdatedDataWithImage(fileName, username);
                }
            }
        });
    }

    private void uploadUpdatedData(String username) {

        Map<String, Object> updates = new HashMap<>();
        updates.put("username", username);

        FirebaseFirestore.getInstance().collection("users").document(userModel.getUserId())
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        UserModel u = UserPreferences.getUser();
                        assert u != null;
                        u.setUsername(username);
                        UserPreferences.setUser(u);
                        Toast.makeText(EditSettingsActivity.this, "Successfully Updated Profile", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(EditSettingsActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditSettingsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadUpdatedDataWithImage(String fileName, String username) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Images/" + fileName);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                StorageReference fileRef = FirebaseStorage.getInstance().getReference("Images");
                StorageReference ref = fileRef.child(fileName);
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (username.isEmpty()) {
                            Toast.makeText(EditSettingsActivity.this, "Please Fill in the empty fields", Toast.LENGTH_SHORT).show();
                        } else {
                            UserModel newUser = new UserModel(userModel.getUserId(), username, userModel.getPhoneNo(), uri.toString(), userModel.getNumOfOrders(), userModel.getTotalKms(), userModel.getBalance());
                            FirebaseFirestore.getInstance().collection("users").document(userModel.getUserId()).set(newUser);
                            UserPreferences.setUser(newUser);
                            Toast.makeText(EditSettingsActivity.this, "Successfully Updated Profile", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(EditSettingsActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditSettingsActivity.this, "Can't get download url", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditSettingsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        backBtn = findViewById(R.id.editBackBtn);
        doneBtn = findViewById(R.id.doneEditBtn);
        profilePic = findViewById(R.id.settingsProfilePic);
        edUsername = findViewById(R.id.edSettingsUsername);
        userModel = (UserModel) getIntent().getSerializableExtra("user");
    }
}
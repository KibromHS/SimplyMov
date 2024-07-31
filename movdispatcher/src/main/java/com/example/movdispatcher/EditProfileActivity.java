package com.example.movdispatcher;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.movdispatcher.models.Dispatcher;
import com.example.movdispatcher.utils.UserPreferences;
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
    private Uri imageUri;
    private ImageView dispatcherPic;
    private TextView dispatcherPhone, dispatcherEmail;
    private EditText dispatcherName, edPassword, edConfirm;
    private Button doneBtn;
    private ProgressDialog progressDialog;
    private Dispatcher dispatcher;

    private final ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        imageUri = data.getData();
                        dispatcherPic.setImageURI(imageUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initView();

        Glide.with(this).load(dispatcher.getImgUrl()).into(dispatcherPic);
        dispatcherName.setText(dispatcher.getName());
        dispatcherEmail.setText(dispatcher.getEmail());
        dispatcherPhone.setText(dispatcher.getPhoneNumber());
        edPassword.setText(dispatcher.getPassword());
        edConfirm.setText(dispatcher.getPassword());

        dispatcherPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imageActivityResultLauncher.launch(intent);
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(EditProfileActivity.this);
                progressDialog.setTitle("Updating Profile...");
                progressDialog.show();

                String username = dispatcherName.getText().toString();
                String pass = edPassword.getText().toString();
                String confirm = edConfirm.getText().toString();

                if (!pass.equals(confirm)) {
                    Toast.makeText(EditProfileActivity.this, "Confirm password correctly", Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_HH_mm_ss", Locale.ENGLISH);
                Date now = new Date();
                String fileName = formatter.format(now);

                if (imageUri == null) {
                    uploadUpdatedData(username, pass);
                } else {
                    uploadUpdatedDataWithImage(fileName, username, pass);
                }
            }
        });
    }

    private void uploadUpdatedData(String username, String password) {

        Map<String, Object> updates = new HashMap<>();
        updates.put("dispatcherName", username);
        updates.put("password", password);

        FirebaseFirestore.getInstance().collection("dispatchers").document(dispatcher.getDispatcherId())
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Dispatcher d = UserPreferences.getUser();
                        assert d != null;
                        d.setName(username);
                        d.setPassword(password);
                        UserPreferences.setUser(d);
                        Toast.makeText(EditProfileActivity.this, "Successfully Updated Dispatcher Name", Toast.LENGTH_SHORT).show();
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

    private void uploadUpdatedDataWithImage(String fileName, String username, String password) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("DispatcherImages/" + fileName);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                StorageReference fileRef = FirebaseStorage.getInstance().getReference("DispatcherImages");
                StorageReference ref = fileRef.child(fileName);
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (username.isEmpty()) {
                            Toast.makeText(EditProfileActivity.this, "Please Fill in the empty fields", Toast.LENGTH_SHORT).show();
                        } else {
                            Dispatcher d = new Dispatcher(dispatcher.getDispatcherId(), username, dispatcher.getPhoneNumber(), dispatcher.getEmail(), password, uri.toString());
                            FirebaseFirestore.getInstance().collection("dispatchers").document(dispatcher.getDispatcherId()).set(d);
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
        dispatcherPic = findViewById(R.id.dispatcherPic);
        dispatcherPhone = findViewById(R.id.dispatcherPhone);
        dispatcherEmail = findViewById(R.id.dispatcherEmail);
        dispatcherName = findViewById(R.id.dispatcherName);
        doneBtn = findViewById(R.id.editDoneBtn);
        edPassword = findViewById(R.id.edPassword);
        edConfirm = findViewById(R.id.edConfirm);
        dispatcher = (Dispatcher) getIntent().getSerializableExtra("dispatcher");
    }
}
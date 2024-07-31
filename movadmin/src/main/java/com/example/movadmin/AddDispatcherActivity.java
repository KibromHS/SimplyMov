package com.example.movadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddDispatcherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dispatcher);

        EditText edName = findViewById(R.id.edName);
        EditText edEmail = findViewById(R.id.edEmail);
        EditText edPhone = findViewById(R.id.edPhone);
        EditText edPassword = findViewById(R.id.edPassword);
        EditText edConfirm = findViewById(R.id.edConfirm);
        TextView addBtn = findViewById(R.id.addBtn);
        ImageView dispatcherPic = findViewById(R.id.customerPic);
        Glide.with(this).load("https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/no-profile-picture-icon.png").into(dispatcherPic);

        addBtn.setOnClickListener(view -> {
            String name = edName.getText().toString();
            String email = edEmail.getText().toString();
            String phone = edPhone.getText().toString();
            String password = edPassword.getText().toString();
            String confirm = edConfirm.getText().toString();

            if (password.equals(confirm)) {
                uploadDispatcherData(name, phone, email, password);
            } else {
                Toast.makeText(this, "Confirm password correctly", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadDispatcherData(String name, String phone, String email, String password) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("phoneNumber", phone);
        data.put("email", email);
        data.put("imgUrl", "https://uxwing.com/wp-content/themes/uxwing/download/peoples-avatars/no-profile-picture-icon.png");
        data.put("password", password);

        FirebaseFirestore.getInstance().collection("dispatchers").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String dispatcherId = documentReference.getId();
                FirebaseFirestore.getInstance().collection("dispatchers").document(dispatcherId).update("dispatcherId", dispatcherId).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddDispatcherActivity.this, "Dispatcher added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddDispatcherActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
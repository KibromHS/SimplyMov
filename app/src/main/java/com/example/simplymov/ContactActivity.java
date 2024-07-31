package com.example.simplymov;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ImageView backBtn = findViewById(R.id.contactBackBtn);
        backBtn.setOnClickListener(view -> onBackPressed());
    }
}
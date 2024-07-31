package com.example.simplymov;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ImageView backBtn = findViewById(R.id.aboutBackBtn);
        TextView toContact = findViewById(R.id.toContactBtn);

        backBtn.setOnClickListener(view -> onBackPressed());
        toContact.setOnClickListener(view -> startActivity(new Intent(this, ContactActivity.class)));
    }
}
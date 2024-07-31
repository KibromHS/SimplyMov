package com.example.simplymov;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.simplymov.util.FirebaseUtil;
import com.example.simplymov.util.UserPreferences;
import com.google.firebase.FirebaseApp;

public class IntroductionActivity extends AppCompatActivity {
    Button getStartedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        getStartedBtn = findViewById(R.id.getStartedBtn);

        UserPreferences.init(this);
        if (FirebaseUtil.currentUserId() != null) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
        getStartedBtn.setOnClickListener(v -> {
            Intent intent = new Intent(IntroductionActivity.this, PhoneRegisterActivity.class);
            startActivity(intent);
        });
    }
}
package com.example.simplymov;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PhoneRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_register);

        EditText edPhoneNumber = findViewById(R.id.editTextPhone);
        Button sendOtpBtn = findViewById(R.id.buttonSendOtp);

        sendOtpBtn.setOnClickListener(view -> {
            String phoneNumber = "+251".concat(edPhoneNumber.getText().toString());

            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(PhoneRegisterActivity.this, "Fill in your phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = new Intent(PhoneRegisterActivity.this, OtpActivity.class);
            i.putExtra("number", phoneNumber);
            startActivity(i);
        });
    }
}
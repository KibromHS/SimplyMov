package com.example.movadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText txtUsername = findViewById(R.id.txtEmail);
        EditText txtPassword = findViewById(R.id.txtPassword);
        Button loginBtn = findViewById(R.id.loginBtn);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        loginBtn.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);

            String username = txtUsername.getText().toString();
            String password = txtPassword.getText().toString();

            if (username.equals("Admin") && password.equals("simplymov")) {
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Incorrect username and password match", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
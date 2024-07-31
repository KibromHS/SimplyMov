package com.example.movdispatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.movdispatcher.models.Dispatcher;
import com.example.movdispatcher.utils.UserPreferences;
import com.example.movdispatcher.utils.Util;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText txtEmail = findViewById(R.id.txtEmail);
        EditText txtPassword = findViewById(R.id.txtPassword);
        Button loginBtn = findViewById(R.id.loginBtn);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        UserPreferences.init(this);

        if (UserPreferences.getUser() != null) {
            UserPreferences.setUser(UserPreferences.getUser());
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                Util.signIn(email, password, new DispatcherCallback() {
                    @Override
                    public void onDispatcherReceivedCallback(Dispatcher dispatcher) {
                        progressBar.setVisibility(View.INVISIBLE);
                        UserPreferences.setUser(dispatcher);
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onDispatcherNotFoundCallback(String error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
package com.example.simplymov;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PaymentOptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_option);

        LinearLayout cbe = findViewById(R.id.cbe);
        LinearLayout telebirr = findViewById(R.id.telebirr);
        LinearLayout awash = findViewById(R.id.awash);

        cbe.setOnClickListener(view -> {
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("option", "CBE");
            intent.putExtra("number", "1000533730861");
            startActivity(intent);
        });

        telebirr.setOnClickListener(view -> {
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("option", "Telebirr");
            intent.putExtra("number", "0945525249");
            startActivity(intent);
        });

        awash.setOnClickListener(view -> {
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("option", "Awash");
            intent.putExtra("number", "1000533730861");
            startActivity(intent);
        });
    }
}
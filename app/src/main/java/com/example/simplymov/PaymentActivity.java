package com.example.simplymov;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simplymov.models.UserModel;
import com.example.simplymov.util.UserPreferences;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentActivity extends AppCompatActivity {
    private TextView paymentOption, accountNumber, yourNumber;
    private EditText edAmount, edAccNumber;
    private ImageView attachment;
    private Button confirm;
    private String option, number;
    private Uri imageUri;

    private final ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    imageUri = data.getData();
                    attachment.setImageURI(imageUri);
                }
            });

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        initView();

        paymentOption.setText(option + " account number:");
        yourNumber.setText("Your " + option + " account number:");
        accountNumber.setText(number);

        attachment.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imageActivityResultLauncher.launch(intent);
        });

        confirm.setOnClickListener(view -> {
            if (imageUri == null) {
                Toast.makeText(this, "Please attach your payment receipt", Toast.LENGTH_SHORT).show();
                return;
            }

            String amount = edAmount.getText().toString();
            String accNumber = edAccNumber.getText().toString();

            if (amount.isEmpty() || accNumber.isEmpty()) {
                Toast.makeText(this, "Please fill your account number and amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amountD;

            try {
                amountD = Double.parseDouble(amount);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter correct amount", Toast.LENGTH_SHORT).show();
                return;
            }

            UserModel userModel = UserPreferences.getUser();
            assert userModel != null;
            FirebaseFirestore.getInstance().collection("users").document(userModel.getUserId()).update("balance", FieldValue.increment(amountD)).addOnSuccessListener(unused -> {
                userModel.setBalance(userModel.getBalance() + amountD);
                UserPreferences.setUser(userModel);
                Toast.makeText(this, "Balance added to wallet", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(PaymentActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            });
        });
    }

    private void initView() {
        paymentOption = findViewById(R.id.paymentOption);
        accountNumber = findViewById(R.id.accountNumber);
        yourNumber = findViewById(R.id.yourNumber);
        edAmount = findViewById(R.id.edAmount);
        edAccNumber = findViewById(R.id.edAccNumber);
        attachment = findViewById(R.id.attachment);
        confirm = findViewById(R.id.confirmPaymentBtn);
        option = getIntent().getStringExtra("option");
        number = getIntent().getStringExtra("number");
    }
}
package com.example.simplymov;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.simplymov.models.UserModel;
import com.example.simplymov.util.FirebaseUtil;
import com.example.simplymov.util.UserPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private EditText[] otpDigits;
    private String verificationCode;
    private String phoneNumber;
    Long timeoutSeconds = 60L;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    PhoneAuthProvider.ForceResendingToken resendingToken;

    private TextView resendOtpTextView;
    private Button btnVerifyOtp;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpDigits = new EditText[] {
                findViewById(R.id.otp_digit_1),
                findViewById(R.id.otp_digit_2),
                findViewById(R.id.otp_digit_3),
                findViewById(R.id.otp_digit_4),
                findViewById(R.id.otp_digit_5),
                findViewById(R.id.otp_digit_6)
        };

        btnVerifyOtp = findViewById(R.id.btn_verify_otp);
        TextView tvInstruction = findViewById(R.id.instructionWithNumber);
        resendOtpTextView = findViewById(R.id.resend_otp_textview);
        progressBar = findViewById(R.id.progressBar);

        phoneNumber = getIntent().getStringExtra("number");
        String instruction = "Enter the 6 digit number sent to ";

        StringBuilder sb = new StringBuilder(phoneNumber);
        sb.replace(5, 10, "*****");
        String modifiedPhone = sb.toString();

        instruction = instruction.concat(modifiedPhone);

        tvInstruction.setText(instruction);

        sendOtp(phoneNumber, false);

        for (int i = 0; i < otpDigits.length; i++) {
            final int index = i;
            otpDigits[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpDigits.length - 1) {
                        otpDigits[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = getOtpFromEditTexts();
                if (otp.length() == 6) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                    signIn(credential);
                } else {
                    Toast.makeText(OtpActivity.this, "Please enter valid OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        resendOtpTextView.setOnClickListener(view -> sendOtp(phoneNumber, true));
    }

    private String getOtpFromEditTexts() {
        StringBuilder otp = new StringBuilder();
        for (EditText editText : otpDigits) {
            otp.append(editText.getText().toString());
        }
        return otp.toString();
    }

    private void sendOtp(String phoneNumber, boolean isResend) {
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signIn(phoneAuthCredential);
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OtpActivity.this, "Verification Failed", Toast.LENGTH_LONG).show();
                        Log.d("otp", e.getMessage());
                        setInProgress(false);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        resendingToken = forceResendingToken;
                        Toast.makeText(OtpActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
                        setInProgress(false);
                    }
                });
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    private void startResendTimer() {
        resendOtpTextView.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                String resendStr = "Resend OTP in " + timeoutSeconds + " seconds";
                resendOtpTextView.setText(resendStr);
                if (timeoutSeconds <= 0) {
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resendOtpTextView.setEnabled(true);
                        }
                    });
                }
            }
        }, 0, 1000);
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                    if (isNewUser) {
                        Intent i = new Intent(OtpActivity.this, SignupActivity.class);
                        i.putExtra("number", phoneNumber);
                        startActivity(i);
                    } else {

                        FirebaseFirestore.getInstance().collection("users").whereEqualTo("phoneNo", phoneNumber).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                        UserModel userModel = document.toObject(UserModel.class);
                                        assert userModel != null;
                                        UserPreferences.setUser(userModel);
                                        Intent i = new Intent(OtpActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Log.d("otp", "No matching documents found.");
                                        Intent i = new Intent(OtpActivity.this, SignupActivity.class);
                                        i.putExtra("number", phoneNumber);
                                        startActivity(i);
                                    }
                                } else {
                                    Log.w("otp", "Error getting documents.", task.getException());
                                }
                            }
                        });
                    }

                } else {
                    Toast.makeText(OtpActivity.this, "OTP verification failed to sign in", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            btnVerifyOtp.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnVerifyOtp.setVisibility(View.VISIBLE);
        }
    }

}

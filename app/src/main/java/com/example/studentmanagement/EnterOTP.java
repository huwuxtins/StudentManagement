package com.example.studentmanagement;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.studentmanagement.activities.MainActivity;
import com.example.studentmanagement.models.LoginHistory;
import com.example.studentmanagement.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class EnterOTP extends AppCompatActivity {
    PinView pinview ;
    Button btn_enter;

    String verificationid ;
    String phone;

    TextView txt_sendAgain;

    DatabaseReference mDatabase;

    FirebaseAuth mAuth;

    String dateTime ;
    String fphone ;

    DatabaseReference myRef;

    PhoneAuthProvider.ForceResendingToken mforceResendingToken;
    long maxId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterlogin);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        pinview = findViewById(R.id.pinview);
        btn_enter = findViewById(R.id.show_otp);
        txt_sendAgain = findViewById(R.id.txt_sendAgain);
        mAuth = FirebaseAuth.getInstance();


        myRef = FirebaseDatabase.getInstance().getReference("LoginHistory");
        getData();

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = pinview.getText().toString();
                Log.w("otppp",otp);
                sendVerifyOTP(otp);
            }
        });

        txt_sendAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    sendAgainOTP();
            }
        });
    }

    public String formatPhone(String s){
        String tmp = s.substring(3,12);
        return "0"+tmp;
    }

    public void getData(){
        phone = getIntent().getStringExtra("Phonenumber");
        fphone = formatPhone(phone);
        verificationid = getIntent().getStringExtra("Verificationid");
    }

    public void sendVerifyOTP(String otp){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            FirebaseUser user = task.getResult().getUser();
                            mDatabase.child("Users").child(formatPhone(user.getPhoneNumber())).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    User user = task.getResult().getValue(User.class);
                                    saveHistory();
                                    goToMain(user.getPhoneNumber(), user.getRole());
                                }
                            });
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(EnterOTP.this,"The verification code entered was invalid", Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                            }
                        }
                    }
                });
    }


    private void saveHistory() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/YYYY");
            DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("HHmmssddMMYYYY");
            LocalDateTime now = LocalDateTime.now();
            dateTime = dtf.format(now).toString();
            String id = dtf2.format(now).toString();
            LoginHistory newHis = new LoginHistory(fphone,dateTime);
            myRef.child(id).setValue(newHis);

        }
    }

    private void setMaxId(long mid){
        this.maxId = mid;
    }

    private void goToMain(String phoneNumber, String ROLE) {
        Intent intent = new Intent(EnterOTP.this, MainActivity.class);
        intent.putExtra("Phonenumber", phoneNumber);
        intent.putExtra("ROLE", ROLE);
        startActivity(intent);
    }

    private void sendAgainOTP(){
        PhoneAuthOptions options =
            PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setForceResendingToken(mforceResendingToken)
                .setActivity(this)                 // (optional) Activity for callback binding
                // If no activity is passed, reCAPTCHA verification can not be used.
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                            Toast.makeText(EnterOTP.this,"Verification Failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        Toast.makeText(EnterOTP.this, "OTP is sent again",Toast.LENGTH_SHORT).show();
                        verificationid = s;
                        mforceResendingToken = forceResendingToken;
                    }
                })          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
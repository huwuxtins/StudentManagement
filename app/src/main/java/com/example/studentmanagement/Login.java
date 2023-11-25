package com.example.studentmanagement;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.studentmanagement.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    EditText edt_phoneNumber;
    Button btn_otp;

    FirebaseDatabase database;
    DatabaseReference myRef;

    List<User> checkList = new ArrayList<>();

    FirebaseAuth mAuth;

    String phone;

    String formatPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        edt_phoneNumber = findViewById(R.id.edt_phonenumber);
        btn_otp = findViewById(R.id.btn_otp);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");


        mAuth =  FirebaseAuth.getInstance();
        btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = edt_phoneNumber.getText().toString();
                if(phone.length() != 10){
                    Toast.makeText(Login.this, "Invalid phone number", Toast.LENGTH_SHORT).show();

                }
                else {

                    String tmp  = phone.substring(1,10);
                    formatPhone = "+84"+tmp;

                    checkList.clear();

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dt: snapshot.getChildren()){
                                User us = dt.getValue(User.class);
                                Log.w("dddd",us.getPhoneNumber());
                                if(us.getPhoneNumber().equals(phone) && us.getLocked() == false){
                                    checkList.add(us);
                                }

                            }
                            if(checkList.size() == 0){
                                Toast.makeText(Login.this, "Account does not exist or it is blocked", Toast.LENGTH_SHORT).show();
                            }
                            else{
//                                User tmp = checkList.get(0);
//                                Toast.makeText(Login.this, tmp.getName(), Toast.LENGTH_SHORT).show();
                                  verifyOTP(formatPhone);

                            }


                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Login.this, "Check account fail", Toast.LENGTH_SHORT).show();
                        }
                    });

                }



            }

        });




    }


    public void verifyOTP(String phoneNumber){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                               // signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.w("excepSend",e.toString());
                                Toast.makeText(Login.this,"Verification Failed to " + formatPhone, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                goToEnterLogin(phoneNumber,s);
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            goToMain(user.getPhoneNumber());

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(Login.this,"The verification code entered was invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void goToMain(String phoneNumber) {
        Intent intent = new Intent(Login.this, MainScreen.class);
        intent.putExtra("Phonenumber", phoneNumber);
        startActivity(intent);
    }

    private void goToEnterLogin(String phoneNumber, String s) {
        Intent intent = new Intent(Login.this, enterlogin.class);
        intent.putExtra("Phonenumber", phoneNumber);
        intent.putExtra("Verificationid",s);
        startActivity(intent);
    }



}
package com.example.studentmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class intro extends AppCompatActivity {

    SharedPreferences sharedPref ;
    String shpPhone;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        sharedPref = getApplicationContext().getSharedPreferences("Account", 0);


        shpPhone  = sharedPref.getString("Phonenumber","null");

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkLogin();
            }
        },2000);

    }

    public void checkLogin(){
        if(shpPhone.equals("null")){
            Intent intent = new Intent(intro.this, Login.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(intro.this, MainScreen.class);
            startActivity(intent);
        }
    }
}
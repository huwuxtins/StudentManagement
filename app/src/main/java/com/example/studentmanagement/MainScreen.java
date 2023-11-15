package com.example.studentmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainScreen extends AppCompatActivity {

    String phone;
    String fphone;
    TextView txt_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        txt_phone = findViewById(R.id.txt_phone);

        setPhoneNumber();

    }

    public String formatPhone(String s){
        String tmp = s.substring(3,12);
        return "0"+tmp;
    }


    public  void setPhoneNumber(){
        phone = getIntent().getStringExtra("Phonenumber");
        fphone = formatPhone(phone);
        txt_phone.setText(fphone);
    }
}
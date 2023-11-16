package com.example.studentmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentmanagement.models.Role;
import com.example.studentmanagement.models.User;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainScreen extends AppCompatActivity {

    String phone;
    String fphone;
    TextView txt_phone;

    TextView txt_name;
    Button btn_clear;
    TextView txt_role;
    SharedPreferences sharedPref ;
    SharedPreferences.Editor editor ;
    FirebaseDatabase database;
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        txt_phone = findViewById(R.id.txt_phone);
        txt_name = findViewById(R.id.txt_name);
        txt_role = findViewById(R.id.txt_role);
        btn_clear = findViewById(R.id.btn_clear);

        sharedPref = getApplicationContext().getSharedPreferences("Account", 0);
        editor  = sharedPref.edit();
        database = FirebaseDatabase.getInstance();

        String shpPhone  = sharedPref.getString("Phonenumber","null");

        if(shpPhone.equals("null")){ // set sharepreferences tu intent - lan dang nhap dau
            Toast.makeText(MainScreen.this, "first", Toast.LENGTH_SHORT).show();
            setPhoneNumber();
            editor.putString("Phonenumber",phone);
            editor.apply();

        }
        else{ //da co thong tin dang nhap
            Toast.makeText(MainScreen.this, "phone", Toast.LENGTH_SHORT).show();
            phone = sharedPref.getString("Phonenumber","null");
            fphone = formatPhone(phone);
        }


        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.edit().clear().commit();
            }
        });


        showData();
        showRole();


    }



    public String formatPhone(String s){
        String tmp = s.substring(3,12);
        return "0"+tmp;
    }


    public  void setPhoneNumber(){
        phone = getIntent().getStringExtra("Phonenumber");
        fphone = formatPhone(phone);

    }



    private void showData() {  //luu curently account de su dung
         myRef = database.getReference("Users");
         myRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 for(DataSnapshot dt: snapshot.getChildren()){
                     User tmp = dt.getValue(User.class);
                     if(tmp.getPhoneNumber().equals(fphone)){

                         txt_phone.setText(tmp.getPhoneNumber());
                         txt_name.setText(tmp.getName());
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {
                 Toast.makeText(MainScreen.this, "Show data fail", Toast.LENGTH_SHORT).show();
             }
         });


    }

    public void showRole(){
        myRef = database.getReference("Role");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dt: snapshot.getChildren()){
                    Role tmp = dt.getValue(Role.class);
                    if(tmp.getPhonenumber().equals(fphone)){
                        txt_role.setText(tmp.getRolename());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainScreen.this, "Show role fail", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
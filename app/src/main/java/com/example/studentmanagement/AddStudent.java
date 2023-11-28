package com.example.studentmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.studentmanagement.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddStudent extends AppCompatActivity {
    Button btnAddStudent, btnViewListStudent;
    EditText name, email, phone;
    DatabaseReference databaseStudent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnViewListStudent = findViewById(R.id.btnViewListStudent);
        name = findViewById(R.id.edtName);
        email = findViewById(R.id.edtEmail);
        phone = findViewById(R.id.edtPhone);
        databaseStudent = FirebaseDatabase.getInstance().getReference();

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertData();
            }
        });
        btnViewListStudent.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddStudent.this,StudentList.class));
            }
        }));
    }
    private void InsertData() {
        String studentname = name.getText().toString();
        String studentemail = email.getText().toString();
        String studentphone = phone.getText().toString();
        String id = databaseStudent.push().getKey();

        Student student = new Student(studentname, studentemail, studentphone);
        databaseStudent.child("students").child(id).setValue(student)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AddStudent.this, "Student Detail Inserted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
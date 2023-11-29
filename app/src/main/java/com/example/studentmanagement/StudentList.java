package com.example.studentmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.health.connect.datatypes.units.Length;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.studentmanagement.adapter.StudentAdapter;
import com.example.studentmanagement.models.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class StudentList extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Student> list;

    ArrayList<Student> listTmp;

    ArrayList<Student> listSearch;
    DatabaseReference databaseReference;
    StudentAdapter studentAdapter;

    Spinner attribute, typeSort;

    Button btn_sort;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    EditText search;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(StudentList.this, AddStudent.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        recyclerView = findViewById(R.id.recyclerview);
        databaseReference = FirebaseDatabase.getInstance().getReference("students");
        list = new ArrayList<>();
        listTmp = new ArrayList<>();
        listSearch = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentAdapter = new StudentAdapter(this, list, getSupportFragmentManager());
        recyclerView.setAdapter(studentAdapter);

        attribute = findViewById(R.id.attribute);
        typeSort = findViewById(R.id.typeSort);
        btn_sort = findViewById(R.id.btn_sort);
        search = findViewById(R.id.editTextText);

        ImageButton addButton = findViewById(R.id.imageButtonAddStudent);
        ImageButton exportButton = findViewById(R.id.img_export);

        exportButton.setOnClickListener(v -> {
            File directory = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "Students");

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, "students.csv");

            try (FileWriter writer = new FileWriter(file)) {
                writer.append("ID,Name,Date of Birth,Class,Gender,Phone Number,Faculties,Picture Link\n");
                for (Student student : list) {
                    writer.append(String.format(Locale.getDefault(), "%s,%s,%s,%s,%s,%s,%s,%s\n",
                            student.id,
                            student.name,
                            sdf.format(student.bod),
                            student.className,
                            student.gender,
                            student.phoneNumber,
                            student.faculties,
                            student.pictureLink));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Student student = dataSnapshot.getValue(Student.class);
                    list.add(student);
                }
                studentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the AddStudent activity when the button is clicked
                Intent intent = new Intent(StudentList.this, AddStudent.class);
                startActivity(intent);
            }
        });

        btn_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type  = attribute.getSelectedItem().toString();
                String tSort = typeSort.getSelectedItem().toString();
                try{

                    if(type.equals("None") || tSort.equals("None")){
                        studentAdapter.setData(list);
                        studentAdapter.notifyDataSetChanged();
                    }
                    else{
                        if(type.equals("Class") ){
                            sortStudentClassByCollection(tSort);
                        }
                        else{
                            sortStudentYearOfBodByCollection(tSort);
                        }
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });


    }

    private void copyList (){
        for(int i=0; i<list.size();i++){
            listTmp.add(list.get(i));
        }
    }



    private void sortStudentClassByCollection(String tSort){
        if(listTmp.size() != 0){
            listTmp.clear();
        }
        copyList();

        if(tSort.equals("Ascending")){
            Collections.sort(listTmp,Student.StuClassComparatorASC);
        }
        else{
            Collections.sort(listTmp,Student.StuClassComparatorDESC);
        }

        studentAdapter.setData(listTmp);
        studentAdapter.notifyDataSetChanged();

    }


    private void sortStudentYearOfBodByCollection(String tSort){
        if(listTmp.size() != 0){
            listTmp.clear();
        }
        copyList();

        if(tSort.equals("Ascending")){
            Collections.sort(listTmp,Student.StuYearOfBodASC);
        }
        else{
            Collections.sort(listTmp,Student.StuYearOfBodDESC);
        }

        studentAdapter.setData(listTmp);
        studentAdapter.notifyDataSetChanged();
    }


}
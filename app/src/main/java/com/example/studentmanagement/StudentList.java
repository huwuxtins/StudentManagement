package com.example.studentmanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.health.connect.datatypes.units.Length;
import android.os.Bundle;
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
import com.example.studentmanagement.models.User;
import com.example.studentmanagement.models.UserSelect;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class StudentList extends AppCompatActivity {
    RecyclerView recyclerView;
    ActionBarDrawerToggle actionBarDrawerToggle;

    ArrayList<Student> list;

    ArrayList<Student> listTmp;

    ArrayList<Student> listSearch;
    DatabaseReference databaseReference;
    StudentAdapter studentAdapter;

    Spinner attribute, typeSort;

    Button btn_sort;

    EditText search;

    ImageButton btn_delete;

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(StudentList.this, AddStudent.class));
//        finish();
//    }

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


        getAllStudent();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the AddStudent activity when the button is clicked
                Intent intent = new Intent(StudentList.this, AddStudent.class);
                startActivity(intent);
            }
        });

        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Manage Student");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolbarManageUser)));

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

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    if(listSearch.size() != 0){
                        listSearch.clear();
                    }
                    String key = search.getText().toString();

                    if(key.equals("")){
                        studentAdapter.setData(list);
                        studentAdapter.notifyDataSetChanged();
                    }
                    else{
                        for(Student st : list){
                            if(st.getId().contains(key)){
                                listSearch.add(st);
                            }
                        }

                        studentAdapter.setData(listSearch);
                        studentAdapter.notifyDataSetChanged();
                    }
            }
        });


    }

    private void copyList (){
        for(int i=0; i<list.size();i++){
            listTmp.add(list.get(i));
        }
    }

    private void getAllStudent(){
        list.clear();
        databaseReference = FirebaseDatabase.getInstance().getReference("students");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Student tmp = snapshot.getValue(Student.class);
                if(tmp != null){
                      list.add(tmp);
                }
                studentAdapter.setData(list);
                studentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Student afterEdit = snapshot.getValue(Student.class);

                if(afterEdit == null || list == null || list.isEmpty()){
                    return;
                }

                for(int i=0; i< list.size(); i++){
                    Student tmp = list.get(i);
                    if(tmp.getId().equals(afterEdit.getId())){
                        list.set(i,afterEdit);
                    }
                }
                studentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Student st = snapshot.getValue(Student.class);

                if(st == null || list == null || list.isEmpty()){
                    return;
                }
                for(int i=0; i< list.size(); i++){
                    Student tmp = list.get(i);
                    if(tmp.getId().equals(st.getId())){
                        list.remove(list.get(i));
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
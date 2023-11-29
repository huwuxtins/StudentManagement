package com.example.studentmanagement;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.studentmanagement.controllers.StudentController;
import com.example.studentmanagement.dialog.DatePickerFragment;
import com.example.studentmanagement.models.Student;
import com.example.studentmanagement.utils.UriUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddStudent extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST_CODE = 100;
    Button btnAddStudent, btnViewListStudent;
    EditText edtId, edtName, edtPhone, edtBod, edtClass, edtFaculties;
    RadioButton rbnMale, rbnFemale;
    DatabaseReference databaseStudent;
    StudentController studentController = new StudentController();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnViewListStudent = findViewById(R.id.btn_import);
        edtName = findViewById(R.id.edt_name);
        edtPhone = findViewById(R.id.edt_phone);
        edtClass = findViewById(R.id.edt_class);
        edtBod = findViewById(R.id.edt_bod);
        edtFaculties = findViewById(R.id.edt_faculties);
        rbnMale = findViewById(R.id.rbn_male);
        rbnFemale = findViewById(R.id.rbn_female);
        databaseStudent = FirebaseDatabase.getInstance().getReference();

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String c = checkData();

                if(c.equals("OK")){
                     try {
                        InsertData();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                else{
                    Toast.makeText(AddStudent.this,c,Toast.LENGTH_LONG).show();
                }



            }
        });
        btnViewListStudent.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*"); // Set the MIME type to filter by file type
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Start the file picker activity
                startActivityForResult(Intent.createChooser(intent, "Select a File"), PICK_FILE_REQUEST_CODE);
            }
        }));

        edtBod.setOnClickListener(v -> {
            DatePickerFragment newFragment = new DatePickerFragment(edtBod);
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });
    }
    private void InsertData() throws ParseException {

        String name = edtName.getText().toString();
        String phone = edtPhone.getText().toString();
        String className = edtClass.getText().toString();
        String faculties = edtFaculties.getText().toString();
        Date bod = sdf.parse(edtBod.getText().toString());
        String gender = rbnMale.isChecked() ? "male" : "female";

            Student student = new Student(name, bod, className, gender, phone, faculties, "", new ArrayList<>());
            studentController.create(student, new StudentController.OnStudentCreatedListener() {
                @Override
                public void onStudentCreated(Student student) {
                    Toast.makeText(getApplicationContext(), "Add student successfully!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(AddStudent.this,StudentList.class));
                }
            });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Handle the selected file URI
            Uri selectedFileUri = data.getData();
            ArrayList<Student> students = new ArrayList<>();

            try {
                assert selectedFileUri != null;
                InputStream inputStream = UriUtils.getInputStreamFromUri(getApplicationContext(), selectedFileUri);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                CSVReader reader = new CSVReader(inputStreamReader);

                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    // Process each row (nextLine) of the CSV file
                    // For example, you can store it in a data structure or display it in your app
                    Student student = new Student(
                            nextLine[0], sdf.parse(nextLine[1]),
                            nextLine[2], nextLine[3], nextLine[4],
                            nextLine[5], nextLine[6], new ArrayList<>());
                    students.add(student);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (CsvValidationException | ParseException e) {
                throw new RuntimeException(e);
            }

            for(Student student: students){
                studentController.create(student, new StudentController.OnStudentCreatedListener() {
                    @Override
                    public void onStudentCreated(Student student) {
                        Toast.makeText(getApplicationContext(), "Add student successfully!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }


    private String checkData(){
        String name = edtName.getText().toString();
        String phone = edtPhone.getText().toString();
        String className = edtClass.getText().toString();
        String faculties = edtFaculties.getText().toString();

        if(name.equals("") || phone.equals("") || className.equals("") || faculties.equals("")){
            return "Please enter enough information";
        }

        if(phone.length()!=10){
            return "Invalid phone number";
        }

        return "OK";

    }
}
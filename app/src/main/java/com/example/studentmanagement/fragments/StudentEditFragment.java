package com.example.studentmanagement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.adapter.CertificateAdapter;
import com.example.studentmanagement.controllers.StudentController;
import com.example.studentmanagement.dialog.DatePickerFragment;
import com.example.studentmanagement.models.Student;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class StudentEditFragment extends Fragment {
    private Student student;

    RecyclerView rcvCer;
    TextView tvName, tvBod, tvClass, tvGender,tvFaculties, tvPhone;
    Button btnSave;
    ImageButton btnAdd;
    StudentController studentController = new StudentController();

    public StudentEditFragment(){
        super(R.layout.fragment_student_edit);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_edit, container, false);
        rcvCer = view.findViewById(R.id.rcv_certificates);
        tvName = view.findViewById(R.id.tv_name);
        tvBod = view.findViewById(R.id.tv_bod);
        tvClass = view.findViewById(R.id.tv_class);
        tvGender = view.findViewById(R.id.tv_gender);
        tvFaculties = view.findViewById(R.id.tv_faculties);
        tvPhone = view.findViewById(R.id.tv_phone_number);
        btnSave = view.findViewById(R.id.btn_save);
        btnAdd = view.findViewById(R.id.btn_add);

        Bundle args = getArguments();
        if (args != null) {
            Student student = (Student) args.getSerializable("student");
            if (student != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                tvName.setText(student.name);
                tvBod.setText(sdf.format(student.bod));
                tvClass.setText(student.className);
                tvGender.setText(student.gender);
                tvFaculties.setText(student.faculties);
                tvPhone.setText(student.phoneNumber);
                rcvCer.setAdapter(new CertificateAdapter(getContext(), student.certificates, student, getParentFragmentManager()));
                rcvCer.setLayoutManager(new GridLayoutManager(getContext(), 2));

                tvBod.setOnClickListener(v -> {
                    DatePickerFragment newFragment = new DatePickerFragment(tvBod);
                    newFragment.setTargetFragment(StudentEditFragment.this, 0); // Set the target fragment
                    newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");

                });

                btnSave.setOnClickListener(v -> {
                    student.name = tvName.getText().toString();
                    try {
                        student.bod = sdf.parse(tvBod.getText().toString());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    student.className = tvClass.getText().toString();
                    student.gender = tvGender.getText().toString();
                    student.faculties = tvFaculties.getText().toString();
                    student.phoneNumber = tvPhone.getText().toString();
                    student.certificates = ((CertificateAdapter)rcvCer.getAdapter()).certificates;

                    studentController.update(student, new StudentController.OnStudentLoadedListener() {
                        @Override
                        public void onStudentLoaded(Student student) {
                            Toast.makeText(getContext(), "Update successfully!", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("student", student);
                            getParentFragmentManager().beginTransaction().replace(R.id.csl_students, StudentDetailFragment.class, bundle).commit();
                            Toast.makeText(getContext(), "Edit student", Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                btnAdd.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("student", student);
                    bundle.putInt("position", student.certificates.size());
                    getParentFragmentManager().beginTransaction().replace(R.id.csl_students, CertificateEditFragment.class, bundle).commit();
                });
            }
        }
        return view;
    }
}

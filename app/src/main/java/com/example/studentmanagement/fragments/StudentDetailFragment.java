package com.example.studentmanagement.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.adapter.CertificateAdapter;
import com.example.studentmanagement.models.Student;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class StudentDetailFragment extends Fragment {
    SharedPreferences sharedPref;
    RecyclerView rcvCer;
    TextView tvId, tvName, tvBod, tvClass, tvGender,tvFaculties, tvPhone;
    Button btnEdit;

    public StudentDetailFragment(){
        super(R.layout.fragment_student_detail);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_detail, container, false);
        rcvCer = view.findViewById(R.id.rcv_certificates);
        tvId = view.findViewById(R.id.tv_id);
        tvName = view.findViewById(R.id.tv_name);
        tvBod = view.findViewById(R.id.tv_bod);
        tvClass = view.findViewById(R.id.tv_class);
        tvGender = view.findViewById(R.id.tv_gender);
        tvFaculties = view.findViewById(R.id.tv_faculties);
        tvPhone = view.findViewById(R.id.tv_phone_number);
        btnEdit = view.findViewById(R.id.btn_edit);

        Bundle args = getArguments();
        if (args != null) {
            Student student = (Student) args.getSerializable("student");
            if (student != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                sharedPref = getContext().getSharedPreferences("Account", 0);

                tvId.setText(student.id);
                tvName.setText(student.name);
                tvBod.setText(sdf.format(student.bod));
                tvClass.setText(student.className);
                tvGender.setText(student.gender);
                tvFaculties.setText(student.faculties);
                tvPhone.setText(student.phoneNumber);
                rcvCer.setAdapter(new CertificateAdapter(getContext(), student.certificates, student, getParentFragmentManager()));
                rcvCer.setLayoutManager(new GridLayoutManager(getContext(), 2));

                btnEdit.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("student", student);
                    getParentFragmentManager().beginTransaction().replace(R.id.csl_students, StudentEditFragment.class, bundle).commit();
                    Toast.makeText(getContext(), "Edit student", Toast.LENGTH_SHORT).show();
                });
            }
        }
        return view;
    }
}

package com.example.studentmanagement.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studentmanagement.R;
import com.example.studentmanagement.models.Student;

import java.util.ArrayList;

public class StudentFragmentDetail extends Fragment {
    private Student student;

    public StudentFragmentDetail(){
        super(R.layout.fragment_student_detail);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_detail, container, false);
        assert savedInstanceState != null;
        student = (Student) savedInstanceState.getSerializable("student");
        assert student != null;
        Log.e("MyApp", student.name);
        return view;
    }
}

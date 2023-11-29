package com.example.studentmanagement.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.fragments.StudentDetailFragment;
import com.example.studentmanagement.models.Student;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder> {

    Context context;
    ArrayList<Student> students;
    FragmentManager fragmentManager;

    public StudentAdapter(Context context, ArrayList<Student> students, FragmentManager fragmentManager) {
        this.context = context;
        this.students = students;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_student_view, parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Student student = students.get(position);
        holder.name.setText(student.getName());
        holder.txt_class.setText(student.getClassName());
        holder.itemView.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Bundle bundle = new Bundle();
            bundle.putSerializable("student", students.get(students.size()-1));

            StudentDetailFragment studentDetailFragment = new StudentDetailFragment();
            studentDetailFragment.setArguments(bundle);

            fragmentTransaction.replace(R.id.csl_students, studentDetailFragment);
            fragmentTransaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,txt_class;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textname);
            txt_class = itemView.findViewById(R.id.txt_class);
        }
    }

    public void setData(ArrayList<Student> students){
        this.students = students;
    }
}

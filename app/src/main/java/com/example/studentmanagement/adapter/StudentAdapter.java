package com.example.studentmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.models.Student;

import java.text.BreakIterator;
import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder> {

    Context context;

    ArrayList<Student> list;

    public StudentAdapter(Context context, ArrayList<Student> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public StudentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.MyViewHolder holder, int position) {
        Student student = list.get(position);
        holder.name.setText(student.getName());
        holder.id.setText(student.getId());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHoler extends RecyclerView.ViewHolder{

        TextView StudentName, StudentID;
        public MyViewHoler(@NonNull View itemView) {
            super(itemView);

            StudentName = itemView.findViewById(R.id.tvStudentName);
            StudentID = itemView.findViewById(R.id.tvStudentID);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Object name;
        public BreakIterator id;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

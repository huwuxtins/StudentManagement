package com.example.studentmanagement.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.fragments.StudentDetailFragment;
import com.example.studentmanagement.models.Student;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder> {

    Context context;
    ArrayList<Student> students;
    FragmentManager fragmentManager;
    FirebaseDatabase database;
    DatabaseReference myRef;



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

        holder.btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogdeleteSelected(student.getName(), student.getId(),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public void DialogdeleteSelected(String name, String id, int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete student ");
        builder.setMessage(name+" "+id);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("students");
                myRef.child(students.get(pos).getId()).removeValue();

                students.remove(pos);
                notifyItemRemoved(pos);


            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,txt_class;
        ImageButton btn_del;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textname);
            txt_class = itemView.findViewById(R.id.txt_class);
            btn_del = itemView.findViewById(R.id.btn_del);
        }
    }

    public void setData(ArrayList<Student> students){
        this.students = students;
    }
}

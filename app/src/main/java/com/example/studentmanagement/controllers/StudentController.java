    package com.example.studentmanagement.controllers;

    import android.util.Log;

    import androidx.annotation.NonNull;

    import com.example.studentmanagement.models.Student;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    public class StudentController {
        private final DatabaseReference mDatabase;

        public interface OnStudentLoadedListener {
            void onStudentLoaded(Student student);
        }

        public interface OnStudentCreatedListener {
            void onStudentCreated(Student student);
        }

        public StudentController() {
            mDatabase = FirebaseDatabase.getInstance().getReference();
        }

        public void findOne(String id, final OnStudentLoadedListener listener) {
            mDatabase.child("students").child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                        listener.onStudentLoaded(null);
                    } else {
                        Student student = task.getResult().getValue(Student.class);
                        listener.onStudentLoaded(student);
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    }
                }
            });
        }

        public void create(Student student, final OnStudentCreatedListener listener) {
            student.id = mDatabase.push().getKey();
            mDatabase.child("students").child(student.id).setValue(student)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            listener.onStudentCreated(student);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onStudentCreated(null);
                        }
                    });
        }

        public void update(Student student, final OnStudentLoadedListener listener){
            mDatabase.child("students").child(student.id).updateChildren(student.toMap())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            listener.onStudentLoaded(student);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onStudentLoaded(student);
                        }
                    });
        }
    }

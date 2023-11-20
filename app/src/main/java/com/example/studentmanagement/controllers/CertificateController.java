package com.example.studentmanagement.controllers;

import androidx.annotation.NonNull;

import com.example.studentmanagement.models.Certificate;
import com.example.studentmanagement.models.Student;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class CertificateController {
    private final DatabaseReference mDatabase;

    public interface OnCertificateLoadedListener {
        void onCertificateLoaded(Certificate certificate);
    }

    public CertificateController() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void addCertificate(String studentId, int position, Certificate certificate, final OnCertificateLoadedListener listener){
        mDatabase.child("students").child(studentId)
                .child("certificates").child(String.valueOf(position))
                .updateChildren(certificate.toMap())
                .addOnSuccessListener(aVoid -> listener.onCertificateLoaded(certificate))
                .addOnFailureListener(e -> listener.onCertificateLoaded(certificate));
    }

    public void deleteCertificate(String studentId, int position, Certificate certificate, final OnCertificateLoadedListener listener){
        mDatabase.child("students").child(studentId)
                .child("certificates").child(String.valueOf(position)).removeValue()
                .addOnSuccessListener(aVoid -> listener.onCertificateLoaded(certificate))
                .addOnFailureListener(e -> listener.onCertificateLoaded(certificate));
    }
}

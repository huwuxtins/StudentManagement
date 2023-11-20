package com.example.studentmanagement.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.controllers.CertificateController;
import com.example.studentmanagement.controllers.ImageController;
import com.example.studentmanagement.fragments.CertificateEditFragment;
import com.example.studentmanagement.models.Certificate;
import com.example.studentmanagement.models.Student;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder> {
    private final Context context;
    public ArrayList<Certificate> certificates;
    public Student student;
    private FragmentManager fragmentManager;

    private CertificateController certificateController = new CertificateController();
    private ImageController imageController = new ImageController();

    public CertificateAdapter(Context context, ArrayList<Certificate> certificates, Student student, FragmentManager fragmentManager){
        this.context = context;
        this.certificates = certificates;
        this.student = student;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public CertificateAdapter.CertificateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_certificate, parent, false);
        return new CertificateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CertificateAdapter.CertificateViewHolder holder, int position) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = sdf.format(certificates.get(position).createdAt) + " - " + sdf.format(certificates.get(position).expiredAt);
        try {
            imageController.setImage(certificates.get(position).pictureLink, "certificates", holder.imgCer, new ImageController.OnImageLoadListener() {
                @Override
                public void onImageLoaded() {
                    Log.e("MyApp", "Image is loading!");
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        holder.tvName.setText(certificates.get(position).name);
        holder.tvDate.setText(date);
        holder.tvScore.setText(String.valueOf(certificates.get(position).score));

        holder.btnEdit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("certificate", certificates.get(position));
            bundle.putSerializable("student", student);
            bundle.putInt("position", position);
            fragmentManager.beginTransaction().replace(R.id.ln_main, CertificateEditFragment.class, bundle).commit();
        });

        holder.btnRemove.setOnClickListener(v -> {
            Certificate certificate = certificates.get(position);
            certificates.remove(position);
            certificateController.deleteCertificate(student.id, position, certificate, new CertificateController.OnCertificateLoadedListener() {
                @Override
                public void onCertificateLoaded(Certificate certificate) {
                    Toast.makeText(v.getContext(), "Delete successfully!", Toast.LENGTH_SHORT).show();
                }
            });
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return certificates.size();
    }

    public static class CertificateViewHolder extends RecyclerView.ViewHolder{
        ImageView imgCer;
        TextView tvName, tvDate, tvScore;

        ImageButton btnRemove, btnEdit;

        public CertificateViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCer = itemView.findViewById(R.id.img_view_cer);
            tvName = itemView.findViewById(R.id.tv_certificate_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvScore = itemView.findViewById(R.id.tv_score);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            btnEdit = itemView.findViewById(R.id.btn_edit);
        }
    }
}

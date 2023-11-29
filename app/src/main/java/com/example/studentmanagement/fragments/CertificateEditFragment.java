package com.example.studentmanagement.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studentmanagement.R;
import com.example.studentmanagement.controllers.CertificateController;
import com.example.studentmanagement.controllers.ImageController;
import com.example.studentmanagement.controllers.StudentController;
import com.example.studentmanagement.dialog.DatePickerFragment;
import com.example.studentmanagement.models.Certificate;
import com.example.studentmanagement.models.Student;
import com.example.studentmanagement.utils.UriUtils;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class CertificateEditFragment extends Fragment {
    private final int PICK_PHOTO_FOR_AVATAR = 12345;
    ImageView img;
    EditText tvName, tvCreatedAt, tvExpiredAt, tvScore;
    Button btnSave;
    ImageButton btnUpload;
    CertificateController certificateController = new CertificateController();
    ImageController imageController = new ImageController();
    Certificate certificate = new Certificate();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Bitmap bitmapImg;
    Student student;

    public CertificateEditFragment(){
        super(R.layout.fragment_certificate);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_certificate, container, false);
        img = view.findViewById(R.id.img_view_cer);
        tvName = view.findViewById(R.id.edt_name);
        tvCreatedAt = view.findViewById(R.id.edt_created_at);
        tvExpiredAt = view.findViewById(R.id.edt_expired_at);
        tvScore = view.findViewById(R.id.edt_score);
        btnSave = view.findViewById(R.id.btn_save);
        btnUpload = view.findViewById(R.id.btn_upload);

        tvCreatedAt.setOnClickListener(v -> {
            DatePickerFragment newFragment = new DatePickerFragment(tvCreatedAt);
            newFragment.setTargetFragment(CertificateEditFragment.this, 0); // Set the target fragment
            newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        });

        tvExpiredAt.setOnClickListener(v -> {
            DatePickerFragment newFragment = new DatePickerFragment(tvExpiredAt);
            newFragment.setTargetFragment(CertificateEditFragment.this, 0); // Set the target fragment
            newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        });

        btnUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
        });

        Bundle args = getArguments();
        if (args != null) {
            certificate = (Certificate) args.getSerializable("certificate");
            if(certificate != null){
                tvName.setText(certificate.name);
                tvCreatedAt.setText(sdf.format(certificate.createdAt));
                tvExpiredAt.setText(sdf.format(certificate.expiredAt));
                tvScore.setText(String.valueOf(certificate.score));
                try {
                    Log.e("MyApp", "Edit fragment picture: " + certificate.pictureLink);
                    imageController.setImage(certificate.pictureLink, "certificates", img, new ImageController.OnImageLoadListener() {
                        @Override
                        public void onImageLoaded() {
                            Log.e("MyApp", "Image is loading!");
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            else{
                certificate = new Certificate();
            }

            student = (Student)args.getSerializable("student");
            int position = args.getInt("position");
            Certificate finalCertificate = certificate;
            btnSave.setOnClickListener(v -> {
                if(tvName.getText().toString().isEmpty() ||
                        tvCreatedAt.getText().toString().isEmpty() ||
                        tvExpiredAt.getText().toString().isEmpty() ||
                        tvScore.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Please write all fields!", Toast.LENGTH_LONG).show();
                    return;
                }

                if(img.getDrawable() == null){
                    Toast.makeText(getContext(), "Please choose a picture for Certificate", Toast.LENGTH_SHORT).show();
                    return;
                }
                Drawable drawable = img.getDrawable();

                if (drawable instanceof BitmapDrawable) {
                    // If the Drawable is a BitmapDrawable, directly cast and get the Bitmap
                    bitmapImg = ((BitmapDrawable) drawable).getBitmap();
                    // Now you can use the 'bitmap' as needed
                }

                finalCertificate.name = tvName.getText().toString();
                try {
                    finalCertificate.createdAt = sdf.parse(tvCreatedAt.getText().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                try {
                    finalCertificate.expiredAt = sdf.parse(tvExpiredAt.getText().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                finalCertificate.score = Double.parseDouble(tvScore.getText().toString());
                finalCertificate.pictureLink = finalCertificate.getName().replaceAll(" ", "_").toLowerCase() + UUID.randomUUID();

                assert student != null;
                boolean isContained = false;
                for(Certificate cer: student.certificates){
                    if (cer.name.equals(finalCertificate.name)) {
                        isContained = true;
                        break;
                    }
                }
                if(!isContained){
                    student.certificates.add(finalCertificate);
                }

                imageController.uploadImage(bitmapImg, finalCertificate.pictureLink, "certificates", new ImageController.OnImageLoadListener() {
                    @Override
                    public void onImageLoaded() {
                        certificateController.addCertificate(student.id, position, finalCertificate, new CertificateController.OnCertificateLoadedListener() {
                            @Override
                            public void onCertificateLoaded(Certificate certificate) {
                                Toast.makeText(getContext(), "Save certificate successfully!", Toast.LENGTH_SHORT).show();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("student", student);
                                getParentFragmentManager().beginTransaction().replace(R.id.csl_students, StudentDetailFragment.class, bundle).commitAllowingStateLoss();
                            }
                        });
                        Log.e("MyApp", "Image is uploading!");
                    }
                });

            });
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                img.setImageBitmap(BitmapFactory.decodeStream(inputStream));
                img.setDrawingCacheEnabled(true);
                img.buildDrawingCache();
                bitmapImg = ((BitmapDrawable) img.getDrawable()).getBitmap();

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }
}

package com.example.studentmanagement.fragments;

import static android.app.Activity.RESULT_OK;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.adapter.CertificateAdapter;
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

public class StudentEditFragment extends Fragment {
    private final int PICK_PHOTO_FOR_AVATAR = 12345;
    private static final int PICK_FILE_REQUEST_CODE = 100;
    private Student student;
    RecyclerView rcvCer;
    ImageView img;
    TextView tvName, tvBod, tvClass, tvGender,tvFaculties, tvPhone;
    Button btnSave, btnImport;
    ImageButton btnAdd, btnUpload;
    StudentController studentController = new StudentController();
    CertificateController certificateController = new CertificateController();
    ImageController imageController = new ImageController();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Bitmap bitmapImg;

    public StudentEditFragment(){
        super(R.layout.fragment_student_edit);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_edit, container, false);
        rcvCer = view.findViewById(R.id.rcv_certificates);
        img = view.findViewById(R.id.img_view_ava);
        tvName = view.findViewById(R.id.tv_name);
        tvBod = view.findViewById(R.id.tv_bod);
        tvClass = view.findViewById(R.id.tv_class);
        tvGender = view.findViewById(R.id.tv_gender);
        tvFaculties = view.findViewById(R.id.tv_faculties);
        tvPhone = view.findViewById(R.id.tv_phone_number);
        btnSave = view.findViewById(R.id.btn_save);
        btnAdd = view.findViewById(R.id.btn_add);
        btnImport = view.findViewById(R.id.btn_import);
        btnUpload = view.findViewById(R.id.btn_upload);

        Bundle args = getArguments();
        if (args != null) {
            student = (Student) args.getSerializable("student");
            if (student != null) {
                try {
                    imageController.setImage(student.pictureLink, "students", img, new ImageController.OnImageLoadListener() {
                        @Override
                        public void onImageLoaded() {
                            Log.e("MyApp", "Image is loading!");
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
                    if(img.getDrawable() == null){
                        Toast.makeText(getContext(), "Please choose a picture for Student", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Drawable drawable = img.getDrawable();

                    if (drawable instanceof BitmapDrawable) {
                        // If the Drawable is a BitmapDrawable, directly cast and get the Bitmap
                        bitmapImg = ((BitmapDrawable) drawable).getBitmap();
                        // Now you can use the 'bitmap' as needed
                    }

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
                    student.pictureLink = student.getName().replaceAll(" ", "_").toLowerCase() + UUID.randomUUID();

                    imageController.uploadImage(bitmapImg, student.pictureLink, "students", new ImageController.OnImageLoadListener() {
                        @Override
                        public void onImageLoaded() {
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
                        }
                    });
                });

                btnAdd.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("student", student);
                    if(student.certificates == null){
                        student.certificates = new ArrayList<>();
                    }
                    bundle.putInt("position", student.certificates.size());
                    getParentFragmentManager().beginTransaction().replace(R.id.csl_students, CertificateEditFragment.class, bundle).commit();
                });

                btnImport.setOnClickListener(v -> {

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*"); // Set the MIME type to filter by file type
                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                    // Start the file picker activity
                    startActivityForResult(Intent.createChooser(intent, "Select a File"), PICK_FILE_REQUEST_CODE);
                });

                btnUpload.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
                });
            }
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Handle the selected file URI
            Uri selectedFileUri = data.getData();
            ArrayList<Certificate> certificates = new ArrayList<>();

            try {
                assert selectedFileUri != null;
                InputStream inputStream = UriUtils.getInputStreamFromUri(getContext(), selectedFileUri);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                CSVReader reader = new CSVReader(inputStreamReader);

                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    // Process each row (nextLine) of the CSV file
                    // For example, you can store it in a data structure or display it in your app
                    Certificate certificate = new Certificate(
                            nextLine[0], sdf.parse(nextLine[1]),
                            sdf.parse(nextLine[1]), Double.parseDouble(nextLine[3]), nextLine[4]);
                    certificates.add(certificate);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (CsvValidationException | ParseException e) {
                throw new RuntimeException(e);
            }

            for(Certificate certificate: certificates){
                if(student.certificates == null){
                    student.certificates = new ArrayList<>();
                }
                certificateController.addCertificate(student.id, student.certificates.size(), certificate, new CertificateController.OnCertificateLoadedListener() {
                    @Override
                    public void onCertificateLoaded(Certificate certificate) {
                        Toast.makeText(getContext(), "Save certificate successfully!", Toast.LENGTH_SHORT).show();
                        student.certificates.add(certificate);
                    }
                });
            }
            // Now, you can use the selected file URI to perform further operations
            // For example, you might want to read the content of the file or perform some other action
        }
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

package com.example.studentmanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentmanagement.models.User;
import com.example.studentmanagement.models.UserSelect;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddUser extends AppCompatActivity {


    private Uri imgUri;
    CircleImageView uploadImage;
    TextView edt_typeName, edt_typeAge, edt_typePhone;

    Spinner chooseRole;
    Button btn_save, btn_chooseAvatar;

    String linkImage = "avatar.jpg";

    FirebaseDatabase database;
    DatabaseReference myRef;

    StorageReference storageReference;

    FirebaseStorage firebaseStorage;

    String action = "";

    User editUser;

    private ArrayList<UserSelect> data = new ArrayList<>();



    private final int storagePermission = 12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Add new user");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolbarManageUser)));

        uploadImage = findViewById(R.id.uploadImage);
        edt_typeAge = findViewById(R.id.edt_typeAge);
        edt_typeName = findViewById(R.id.edt_typeName);
        edt_typePhone = findViewById(R.id.edt_typePhone);
         chooseRole = findViewById(R.id.chooseRole);
         btn_save = findViewById(R.id.btn_save);
         btn_chooseAvatar = findViewById(R.id.btn_chooseAvatar);

         Intent getIntent = getIntent();
         action = getIntent.getStringExtra("action");


         if(action.equals("edit")){
             actionBar.setTitle("Edit user");
             Intent i = getIntent();
             editUser  = (User)i.getSerializableExtra("User");
             edt_typePhone.setText(editUser.getPhoneNumber());
             edt_typePhone.setEnabled(false);
             edt_typeName.setText(editUser.getName());
             edt_typeAge.setText(String.valueOf(editUser.getAge()));
             linkImage  = editUser.getPictureLink();
             chooseRole.setSelection(getIndex(chooseRole,editUser.getRole()));
             showImageEditUser(editUser.getPictureLink());
         }


         checkStoragePermission();

         btn_chooseAvatar.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent();
                 intent.setType("image/");
                 intent.setAction(Intent.ACTION_GET_CONTENT);
                 startActivityForResult(intent,1);
             }
         });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputName = edt_typeName.getText().toString();
                String inputPhone = edt_typePhone.getText().toString();

                String inputAge  = edt_typeAge.getText().toString();

                String role = chooseRole.getSelectedItem().toString();


                if(action.equals("add")){
                    String check  = validateInputData(inputName,inputPhone,inputAge);
                    if(check.equals("OK")){
                        int age = Integer.parseInt(inputAge);
                        addNewUser(inputName,inputPhone,age,linkImage,role);
                    }
                    else{
                        Toast.makeText(AddUser.this, check, Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    String check  = validateInputData(inputName,inputPhone,inputAge);
                    if(check.equals("OK")){
                        int age = Integer.parseInt(inputAge);
                        editUser.setName(inputName);
                        editUser.setAge(age);
                        editUser.setRole(role);
                        editUser.setPictureLink(linkImage);
                        editUser(editUser);
                    }
                    else{
                        Toast.makeText(AddUser.this, check, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });



    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }


    public void checkStoragePermission(){
        if(ActivityCompat.checkSelfPermission(AddUser.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddUser.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, storagePermission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == storagePermission){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(AddUser.this, "Accept", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(AddUser.this, "Deny", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String displayName(Uri uri) {

        Cursor mCursor =
                getApplicationContext().getContentResolver().query(uri, null, null, null, null);
        int indexedname = mCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        mCursor.moveToFirst();
        String filename = mCursor.getString(indexedname);
        mCursor.close();
        return filename;
    }


    private void showImageEditUser(String link){
        storageReference = FirebaseStorage.getInstance().getReference("images/"+link);
        try {
            File localFile = File.createTempFile("tmpfile",".jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            uploadImage.setImageBitmap(bitmap);
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imgUri = data.getData();
            uploadImage.setImageURI(imgUri);
            linkImage = displayName(imgUri);
            //Log.w("imgName",displayName(imgUri));
        }
    }


    public String validateInputData(String name, String phone, String age){

        if(name.equals("") || phone.equals("") || age.equals("")){
            return "Please enter enough information";
        }

        if(phone.length() != 10){
            return "Invalid phone number";
        }

        int inputAge = Integer.parseInt(age);

        if(inputAge < 18){
            return "Age must be greater than or equal to 18";
        }



        return "OK";
    }

    public void uploadImageToFirebase(Uri uri, String linkImage){
        final ProgressDialog pd  = new ProgressDialog(this);
        pd.setTitle("Uploading Image");
        pd.show();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        StorageReference st2 = storageReference.child("images/"+linkImage);
        st2.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
             //   Toast.makeText(AddUser.this,"Account added successfully",Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result","Successfully");
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }

         })
                .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(AddUser.this,"Failed",Toast.LENGTH_LONG).show();
                }
            })
        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progressPercent = (100.00*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    pd.setMessage("Progress: " + (int)progressPercent+"%");
            }
        }) ;


    }


//    public void addNewUser(String name, String phone, int age, String imageLink, String role){
//
//        database  = FirebaseDatabase.getInstance();
//        myRef = database.getReference("Users");
//
//        User user = new User(age,name,phone,false,imageLink,role);
//        myRef.child(phone).setValue(user, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                Log.d("addUser","Complete");
//                uploadImageToFirebase(imgUri,linkImage);
//            }
//
//        });
//
//    }

        public void addNewUser(String name, String phone, int age, String imageLink, String role){

        database  = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users/"+phone);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(AddUser.this,"Phone number already exists",Toast.LENGTH_LONG).show();
                } else {
                    User user = new User(age,name,phone,false,imageLink,role);
                    myRef = database.getReference("Users");
                    myRef.child(phone).setValue(user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Log.d("addUser","Complete");
                            if(imgUri!=null){
                                uploadImageToFirebase(imgUri,linkImage);
                            }
                            else{
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("result","Successfully");
                                setResult(Activity.RESULT_OK,returnIntent);
                                finish();
                            }

                        }
                    });

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void editUser(User us){
        database  = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users/"+us.getPhoneNumber());
        myRef.updateChildren(us.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if(imgUri != null){
                    uploadImageToFirebase(imgUri,linkImage);
                }
                else{
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result","Successfully");
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }
        });

    }




}
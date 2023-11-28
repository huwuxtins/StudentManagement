package com.example.studentmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.studentmanagement.dialog.DialogLoginHistory;
import com.example.studentmanagement.models.LoginHistory;
import com.example.studentmanagement.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetail extends AppCompatActivity {


    CircleImageView detailAvatar;
    TextView detailName, detailPhone, detailAge, detailRole, detailStatus;

    Button btn_showHistory;

    String linkAvatar;

    FirebaseDatabase database;
    DatabaseReference myRef;

    StorageReference storageReference;

    User detailuser;

    ArrayList<LoginHistory> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        detailAvatar = findViewById(R.id.detailAvatar);
        detailName = findViewById(R.id.detailName);
        detailPhone = findViewById(R.id.detailPhone);
        detailAge = findViewById(R.id.detailAge);
        detailRole = findViewById(R.id.detailRole);
        detailStatus = findViewById(R.id.detailStatus);
        btn_showHistory = findViewById(R.id.btn_showHistory);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolbarManageUser)));
        actionBar.setTitle("User detail");

        Intent i = getIntent();
        detailuser = (User)i.getSerializableExtra("User");

        linkAvatar = detailuser.getPictureLink();

        showData();
        showAvatar();


        btn_showHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryDialogShow();
            }
        });

    }


    private void HistoryDialogShow(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("LoginHistory");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                for(DataSnapshot dt: snapshot.getChildren()){
                    LoginHistory lg = dt.getValue(LoginHistory.class);
                    if(lg!=null && lg.getPhoneNumber().equals(detailuser.getPhoneNumber())){
                        data.add(lg);
                    }
                }
                DialogLoginHistory listDialog = new DialogLoginHistory(
                        UserDetail.this, data) {
                    @Override
                    public void onCreate(Bundle savedInstanceState)
                    {
                        super.onCreate(savedInstanceState);
                    }
                };
                listDialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("LoginHistory", "Get history failed");
            }
        });
    }


    private void showData(){
        detailName.setText(detailuser.getName());
        detailPhone.setText(detailuser.getPhoneNumber());
        detailAge.setText(String.valueOf(detailuser.getAge()));
        if(detailuser.getLocked() == false){
            detailStatus.setText("Normal");
        }
        else{
            detailStatus.setText("Blocked");
        }

        detailRole.setText(detailuser.getRole());

    }


    private void showAvatar(){

        storageReference = FirebaseStorage.getInstance().getReference("images/"+linkAvatar);
        try {
            File localFile = File.createTempFile("tmpfile",".jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                           detailAvatar.setImageBitmap(bitmap);
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
    package com.example.studentmanagement.activities;

    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.graphics.drawable.ColorDrawable;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.KeyEvent;
    import android.view.MenuItem;
    import android.widget.Button;
    import android.widget.FrameLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.ActionBar;
    import androidx.appcompat.app.ActionBarDrawerToggle;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.drawerlayout.widget.DrawerLayout;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentTransaction;

    import com.example.studentmanagement.Login;
    import com.example.studentmanagement.MainScreen;
    import com.example.studentmanagement.ManageUser;
    import com.example.studentmanagement.R;
    import com.example.studentmanagement.StudentList;
    import com.example.studentmanagement.controllers.StudentController;
    import com.example.studentmanagement.fragments.StudentDetailFragment;
    import com.example.studentmanagement.models.Certificate;
    import com.example.studentmanagement.models.Student;
    import com.example.studentmanagement.models.User;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.android.material.navigation.NavigationView;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;
    import com.google.firebase.storage.FileDownloadTask;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;

    import org.w3c.dom.Text;

    import java.io.File;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Date;

    import de.hdodenhof.circleimageview.CircleImageView;

    public class MainActivity extends AppCompatActivity {
        DrawerLayout dl;
        NavigationView nav;
        ActionBarDrawerToggle actionBarDrawerToggle;
        FrameLayout lnMain;
        Button btnHome, btnUsers, btnStudents;
        String phone;
        String role;
        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;

        TextView mname, mphone, mrole;

        CircleImageView avatar;

        FirebaseDatabase database;
        DatabaseReference myRef;

        StorageReference storageReference;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            dl = findViewById(R.id.dl);
            nav = findViewById(R.id.nav_main);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);
            lnMain = findViewById(R.id.ln_main);
            btnHome = findViewById(R.id.btn_home);
            btnUsers = findViewById(R.id.btn_users);
            btnStudents = findViewById(R.id.btn_students);

            mname = findViewById(R.id.name);
            mrole = findViewById(R.id.role);
            mphone = findViewById(R.id.phone);
            avatar = findViewById(R.id.detailAvatar);

            sharedPref = getApplicationContext().getSharedPreferences("Account", 0);
            editor = sharedPref.edit();
            String shpPhone = sharedPref.getString("Phonenumber", "null");

            if (shpPhone.equals("null")) { // set sharepreferences tu intent - lan dang nhap dau
                setPhoneNumber();
                editor.putString("Phonenumber", phone);
                editor.putString("ROLE", role);
                editor.apply();
            } else { //da co thong tin dang nhap
                phone = sharedPref.getString("Phonenumber", "null");
                role = sharedPref.getString("ROLE", null);
            }

            dl.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            nav.setNavigationItemSelectedListener(item -> {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                int id = item.getItemId();
                if (id == R.id.it_home) {
                    Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    finish();
                    // Start the current activity again to simulate returning to the home page
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);

                } else if (id == R.id.it_users) {
                    Toast.makeText(MainActivity.this, "Users", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, ManageUser.class);
                    startActivity(intent);

                } else if (id == R.id.it_stu) {
                    Toast.makeText(MainActivity.this, "Students", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, StudentList.class);
                    startActivity(intent);

                } else if (id == R.id.it_logout) {
                    Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                    sharedPref = getApplicationContext().getSharedPreferences("Account", 0);
                    sharedPref.edit().clear().commit();
                    Intent intent = new Intent(this, Login.class);
                    startActivity(intent);
                }
                return true;
            });

            btnHome.setOnClickListener(v -> {
                finish();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            });

            btnUsers.setOnClickListener(v -> {
                Intent intent = new Intent(this, ManageUser.class);
                startActivity(intent);
            });

            btnStudents.setOnClickListener(v -> {
                Intent intent = new Intent(this, StudentList.class);
                startActivity(intent);
            });

            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Student Management");
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolbarManageUser)));
            setMyProfile();

        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                this.finishAffinity();
                return true;

            }
            return super.onKeyDown(keyCode, event);
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {

            if (actionBarDrawerToggle.onOptionsItemSelected(item))
                return true;

            return super.onOptionsItemSelected(item);
        }

        public void setPhoneNumber() {
            phone = getIntent().getStringExtra("Phonenumber");
            role = getIntent().getStringExtra("ROLE");
            Log.e("MyApp", "MainActivity: " + role);
        }

        private void setMyProfile() {
            mphone.setText(phone);
            mrole.setText(role);
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("Users");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User us = dataSnapshot.getValue(User.class);
                        if (us.getPhoneNumber().equals(phone)) {
                            mname.setText(us.getName());
                            showAvatar(us.getPictureLink());
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void showAvatar(String linkAvatar) {

            storageReference = FirebaseStorage.getInstance().getReference("images/" + linkAvatar);
            try {
                File localFile = File.createTempFile("tmpfile", ".jpg");
                storageReference.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                avatar.setImageBitmap(bitmap);
                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
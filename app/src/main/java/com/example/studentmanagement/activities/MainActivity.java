    package com.example.studentmanagement.activities;

    import android.os.Bundle;
    import android.util.Log;
    import android.view.MenuItem;
    import android.widget.ImageButton;
    import android.widget.LinearLayout;
    import android.widget.Toast;

    import androidx.appcompat.app.ActionBarDrawerToggle;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.drawerlayout.widget.DrawerLayout;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentTransaction;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.studentmanagement.R;
    import com.example.studentmanagement.controllers.StudentController;
    import com.example.studentmanagement.fragments.StudentDetailFragment;
    import com.example.studentmanagement.models.Certificate;
    import com.example.studentmanagement.models.Student;
    import com.google.android.material.navigation.NavigationView;

    import java.time.LocalDate;
    import java.util.ArrayList;
    import java.util.Date;

    public class MainActivity extends AppCompatActivity {

        DrawerLayout dl;
        NavigationView nav;
        ActionBarDrawerToggle actionBarDrawerToggle;
        RecyclerView rcvMain;
        LinearLayout lnMain;
        ImageButton btnPlus;
        ArrayList<Student> students = new ArrayList<>();

        StudentController studentController = new StudentController();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            dl = findViewById(R.id.dl);
            nav = findViewById(R.id.nav_main);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);
            lnMain = findViewById(R.id.ln_main);
            rcvMain = findViewById(R.id.rcv_main);
            btnPlus = findViewById(R.id.btn_plus);

            dl.addDrawerListener(actionBarDrawerToggle);

            // Move syncState() here
            actionBarDrawerToggle.syncState();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            nav.setNavigationItemSelectedListener(item -> {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                int id = item.getItemId();
                if (id == R.id.it_home) {
                    Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.it_users) {
                    Toast.makeText(MainActivity.this, "Users", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.it_stu) {
                    Toast.makeText(MainActivity.this, "Students", Toast.LENGTH_SHORT).show();
                    ArrayList<Certificate> certificates = new ArrayList<>();

                    Certificate certificate = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        certificate = new Certificate("Ielts", new Date(2003 - 1900, 0, 1),
                                new Date(2003 - 1900, 0, 1), 6.5,
                                "ieltsf997171d-e338-4bc5-a0f0-d1ea80073f07");
                    }
                    certificates.add(certificate);

                    Student student = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        student = new Student(
                                "Nguyen Huu Tin", new Date(2003 - 1900, 0, 1), "21050201",
                                "Male", "0987654321", "IT", "ieltsf997171d-e338-4bc5-a0f0-d1ea80073f07",
                                certificates);
                    }
                    studentController.create(student, student1 -> {
                        students.add(student1);
                        Log.e("MyApp", "Length: " + students.size());
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("student", students.get(students.size()-1));

                        fragmentTransaction.replace(R.id.ln_main, StudentDetailFragment.class, bundle);
                        fragmentTransaction.commit();
                    });
                } else if (id == R.id.it_logout) {
                    Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            if(actionBarDrawerToggle.onOptionsItemSelected(item))
                return true;

            return super.onOptionsItemSelected(item);
        }
    }
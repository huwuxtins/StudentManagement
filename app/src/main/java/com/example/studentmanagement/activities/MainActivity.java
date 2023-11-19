    package com.example.studentmanagement.activities;

    import androidx.appcompat.app.ActionBarDrawerToggle;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.drawerlayout.widget.DrawerLayout;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentTransaction;
    import androidx.recyclerview.widget.RecyclerView;

    import android.os.Bundle;
    import android.view.MenuItem;
    import android.widget.Toast;

    import com.example.studentmanagement.R;
    import com.google.android.material.navigation.NavigationView;

    public class MainActivity extends AppCompatActivity {

        DrawerLayout dl;
        NavigationView nav;
        ActionBarDrawerToggle actionBarDrawerToggle;
        RecyclerView rcvMain;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            dl = findViewById(R.id.dl);
            nav = findViewById(R.id.nav_main);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);
            rcvMain = findViewById(R.id.rcv_main);

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
                } else if (id == R.id.it_logout) {
                    Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                }
                fragmentTransaction.commit();
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
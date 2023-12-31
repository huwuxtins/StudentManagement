package com.example.studentmanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentmanagement.adapter.UserAdapter;
import com.example.studentmanagement.models.User;
import com.example.studentmanagement.models.UserSelect;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ManageUser extends AppCompatActivity {
    DrawerLayout dl;
    NavigationView nav;
    ActionBarDrawerToggle actionBarDrawerToggle;
    TextView txt_total;
    RecyclerView listUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Spinner spinner;
    ImageButton btn_add, btn_del;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor ;

    private ArrayList<UserSelect> data = new ArrayList<>();
    private ArrayList<UserSelect> datatmp = new ArrayList<>();
    private UserAdapter userAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        sharedPref = getApplicationContext().getSharedPreferences("Account", 0);
        editor = sharedPref.edit();
        String role = sharedPref.getString("ROLE", "null");

        dl = findViewById(R.id.dl);
        nav = findViewById(R.id.nav_main);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);

        dl.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_total = findViewById(R.id.txt_total);
        listUser = findViewById(R.id.listUser);

        spinner = (Spinner)findViewById(R.id.role);

        btn_add = findViewById(R.id.btn_add);
        btn_del = findViewById(R.id.btn_del);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(role.equals("Admin")){
                        Intent newIntent = new Intent(ManageUser.this, AddUser.class);
                        newIntent.putExtra("action","add");
                        startActivityForResult(newIntent,112);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "You don't have permission to make this action!", Toast.LENGTH_LONG).show();
                    }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String role = parent.getSelectedItem().toString();
                if(role.equals("All")){
                    getListAllUser();
                }
                else{
                   filterRole(role);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Manage User");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolbarManageUser)));

        data = new ArrayList<>();

        userAdapter = new UserAdapter(data,ManageUser.this);
        listUser.setLayoutManager(new LinearLayoutManager(this));
        listUser.setAdapter(userAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        listUser.addItemDecoration(dividerItemDecoration);


        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(role.equals("Admin")){
                    if(userAdapter.getUserSelected() == 0){
                        Toast.makeText(ManageUser.this, "No user selected", Toast.LENGTH_LONG).show();
                    }
                    else{
                        DialogdeleteSelected();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "You don't have permission to make this action!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void DialogdeleteSelected(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete selected user");
        builder.setMessage("Do you want to delete selected user ?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                userAdapter.deleteSelected();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type phone number to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    userAdapter.setData(data);
                    txt_total.setText("Total: "+String.valueOf(data.size()) +" users");
                    userAdapter.notifyDataSetChanged();
                }
                else{
                    searchUser(newText);
                }
               return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    private void searchUser(String key){
        if(datatmp.size() !=0){
            datatmp.clear();
        }
        for(UserSelect us : data){
            User tmp = us.getUser();
            if(tmp.getPhoneNumber().equals(key)){
                datatmp.add(us);
            }
        }

        userAdapter.setData(datatmp);
        txt_total.setText("Total: "+String.valueOf(datatmp.size()) +" users");
        userAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 112 && resultCode == RESULT_OK){
            String message = data.getStringExtra("result");
            Toast.makeText(ManageUser.this, message,Toast.LENGTH_LONG).show();
        }
        else{
            if(requestCode == 222){
                String message = data.getStringExtra("result");
                Toast.makeText(ManageUser.this, message,Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setTotal(){
        txt_total.setText("Total: "+String.valueOf(data.size()) +" users");
    }

    public void getListAllUser(){

        data.clear();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                User tmp = snapshot.getValue(User.class);
                if(tmp != null){
                    UserSelect usl = new UserSelect(false, tmp);
                    data.add(usl);
                }
                userAdapter.setData(data);
                userAdapter.notifyDataSetChanged();
                setTotal();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { //Sau khi chinh sua User
                User afterEdit = snapshot.getValue(User.class);

                if(afterEdit == null || data == null || data.isEmpty()){
                    return;
                }

                for(int i=0; i< data.size(); i++){
                    User tmp = data.get(i).getUser();
                    if(tmp.getPhoneNumber().equals(afterEdit.getPhoneNumber())){
                        data.set(i,new UserSelect(false,afterEdit));
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {  //Sau khi xóa User
                User afterRemo = snapshot.getValue(User.class);

                if(afterRemo == null || data == null || data.isEmpty()){
                    return;
                }
                for(int i=0; i< data.size(); i++){
                    User tmp = data.get(i).getUser();
                    if(tmp.getPhoneNumber().equals(afterRemo.getPhoneNumber())){
                        data.remove(data.get(i));
                        break;
                    }
                }

                setTotal();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("GetListUser","Failed");
            }
        });
    }

    public void filterRole(String role){
        if(datatmp.size() !=0){
            datatmp.clear();
        }
        for(UserSelect us : data){
            User tmp = us.getUser();
            if(tmp.getRole().equals(role)){
                datatmp.add(us);
            }
        }

        userAdapter.setData(datatmp);
        txt_total.setText("Total: "+String.valueOf(datatmp.size()) +" users");
        userAdapter.notifyDataSetChanged();

    }




//    public void getListUserByRole(String role){
//
//        if(data != null){
//            data.clear();
//        }
//
//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference("Users");
//
//        myRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    User tmp = snapshot.getValue(User.class);
//                    if(tmp != null){
//                        if(tmp.getRole().equals(role)){
//                            UserSelect usl = new UserSelect(false, tmp);
//                            data.add(usl);
//                        }
//                    }
//                    userAdapter.notifyDataSetChanged();
//                    setTotal();
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }



}
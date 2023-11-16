package com.example.studentmanagement.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.models.User;
import com.example.studentmanagement.models.UserSelect;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

import de.hdodenhof.circleimageview.CircleImageView;
public class UserAdapter   extends RecyclerView.Adapter <UserAdapter.RecyclerViewHolder>{

    private ArrayList<UserSelect> data;
    private Context context;

    StorageReference storageReference;
    FirebaseDatabase database;
    DatabaseReference myRef;

    public UserAdapter (ArrayList<UserSelect> data, Context context){
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_view, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        UserSelect usl = data.get(position);
        User tmp = usl.getUser();
        holder.userName.setText(tmp.getName());
        holder.userPhone.setText(tmp.getPhoneNumber());
        holder.userRole.setText(tmp.getRole());

        Log.w("enable", String.valueOf(tmp.getLocked()));

        if(tmp.getLocked()){
            holder.sw_enable.setChecked(false);
        }
        else{
            holder.sw_enable.setChecked(true);
        }

        storageReference = FirebaseStorage.getInstance().getReference("images/"+tmp.getPictureLink());
        try {
            File localFile = File.createTempFile("tmpfile",".jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            holder.avatar.setImageBitmap(bitmap);
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }


        holder.sw_enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tmp.setLocked(false);
                }
                else{
                    tmp.setLocked(true);
                }

                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("Users");

                myRef.child(tmp.getPhoneNumber()).updateChildren(tmp.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        notifyItemChanged(position);
                    }
                });

            }
        });


    }

    public void setData(ArrayList<UserSelect> dt){
        this.data = dt;
    }

    public ArrayList<UserSelect> getData(ArrayList<UserSelect> dt){
        return this.data;
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        CircleImageView avatar;
        TextView userName;
        TextView userPhone;
        TextView userRole;
        Switch sw_enable;
        CheckBox cb_select;


        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            userName = itemView.findViewById(R.id.txt_UserName);
            userPhone = itemView.findViewById(R.id.txt_UserPhone);
            userRole = itemView.findViewById(R.id.txt_UserRole);
            sw_enable = itemView.findViewById(R.id.sw_enable);
            cb_select = itemView.findViewById(R.id.cb_select);
        }
    }

}

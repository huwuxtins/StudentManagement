package com.example.studentmanagement.controllers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageController {
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();

    public interface OnImageLoadListener {
        void onImageLoaded();
    }

    public void uploadImage(Bitmap bitmap, String pictureLink, String object, final OnImageLoadListener listener) {
        StorageReference objectRef = storageReference.child("images/" + object + "/" + pictureLink + ".jpg");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] data = bytes.toByteArray();

        UploadTask uploadTask = objectRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            Log.e("MyApp", "Upload failed!");
            listener.onImageLoaded();
        }).addOnSuccessListener(taskSnapshot -> {
            Log.e("MyApp", "Upload successfully!");
            listener.onImageLoaded();
        });
    }

    public void setImage(String pictureLink, String object, ImageView img, final OnImageLoadListener listener) throws IOException {
        StorageReference objectRef = storageReference.child("images/" + object + "/" + pictureLink + ".jpg");
        File localFile = File.createTempFile("images", "jpg");

        objectRef.getFile(localFile)
                .addOnSuccessListener(taskSnapshot -> {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    img.setImageBitmap(bitmap);
                    listener.onImageLoaded();
                    Log.e("MyApp", "Load file successfully!");
                }).addOnFailureListener(exception -> {
                    Log.e("MyApp", "Load file failed!");
                    listener.onImageLoaded();
                });

    }

}

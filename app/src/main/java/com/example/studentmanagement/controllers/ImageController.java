package com.example.studentmanagement.controllers;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageController {
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = storage.getReference();

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
        Log.e("MyApp", "images/" + object + "/" + pictureLink + ".jpg");
        objectRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(img);
            listener.onImageLoaded();
            Log.e("MyApp", "Load file successfully!");
        }).addOnFailureListener(e -> Log.e("MyApp", "Load file failed!"));
    }
}

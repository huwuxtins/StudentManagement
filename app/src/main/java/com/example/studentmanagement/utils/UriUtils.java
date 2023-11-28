package com.example.studentmanagement.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UriUtils {

    public static InputStream getInputStreamFromUri(Context context, Uri uri) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();

        // Check if the URI scheme is content
        if ("content".equals(uri.getScheme())) {
            return contentResolver.openInputStream(uri);
        } else if ("file".equals(uri.getScheme())) {
            // If the URI is a file URI, open a FileInputStream directly
            return new FileInputStream(uri.getPath());
        } else {
            // For other URI schemes, try to open a ParcelFileDescriptor
            ParcelFileDescriptor parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r");
            if (parcelFileDescriptor != null) {
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                return new FileInputStream(fileDescriptor);
            } else {
                throw new IOException("Unable to open ParcelFileDescriptor for URI: " + uri);
            }
        }
    }
}

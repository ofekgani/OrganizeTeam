package com.myapp.organizeteam.Resources;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;


import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class FileManage extends AppCompatActivity {

    public static final int IMAGE_PICK_CODE = 1000;
    public static final int FILE_PICK_CODE = 190;
    private static final String SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";

    /**
     * this function use to open the gallery to select image.
     * @param activity The activity which from you want to open the gallery.
     */
    public void pickImageFromGallery(Activity activity)
    {
        Intent intent = new Intent ( Intent.ACTION_PICK  );
        intent.setType ( "image/*" );
        activity.startActivityForResult (intent,IMAGE_PICK_CODE);
    }

    public void pickImageFromGallery(Fragment fragment)
    {
        Intent intent = new Intent ( Intent.ACTION_PICK  );
        intent.setType ( "image/*" );
        fragment.startActivityForResult (intent,IMAGE_PICK_CODE);
    }

    /**
     * This function use to set image url to imageView.
     * @param data The url that you want to set.
     * @param imageView The imageView resource that you want to set into.
     */
    public void setImageUri(String data, ImageView imageView)
    {
        if(data == null || data.equals ( "" )) return;
        Picasso.get ().load(data).into(imageView);
    }

    /**
     * This function use to open Crop-activity to the user, to crop part of the image to use it.
     * @param uri the image uri that you want to crop.
     * @param activity The activity which from you want to open the Crop-activity.
     */
    public void cropImage(@NonNull Uri uri, Activity activity)
    {
        if(uri == null) return;
        String fileName = SAMPLE_CROPPED_IMG_NAME;
        double token=Math.random();
        fileName += token;
        fileName += ".jpg";
        UCrop.of(uri, Uri.fromFile ( new File ( activity.getCacheDir (),fileName ) ))
                .withAspectRatio(1, 1)
                .withMaxResultSize(450, 450)
                .start(activity);
    }

    public void cropImage(@NonNull Uri uri, Context context, Fragment fragment)
    {
        if(uri == null) return;
        String fileName = SAMPLE_CROPPED_IMG_NAME;
        double token=Math.random();
        fileName += token;
        fileName += ".jpg";
        UCrop.of(uri, Uri.fromFile ( new File ( context.getCacheDir(),fileName ) ))
                .withAspectRatio(1, 1)
                .withMaxResultSize(450, 450)
                .start(context,fragment);
    }

    /**
     * This function use to check if the user selected image from gallery.
     * @param requestCode The request code from which Intent you came back.
     * @param resultCode
     */
    public boolean imageSelectedFromGallery(int requestCode, int resultCode) {
        return resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE;
    }

    /**
     * Check if the image cropped.
     * @param resultCode The request code from which Intent you came back.
     */
    public boolean isImageCropped(int requestCode, int resultCode) {
        return requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK;
    }

    public boolean isImageCropped(int requestCode) {
        return requestCode == UCrop.REQUEST_CROP;
    }

    public Uri getCropOutput(@Nullable Intent data) {
        return UCrop.getOutput ( data );
    }

    public Uri getImageUri(@Nullable Intent data) {
        if(data == null) return null;
        return data.getData ();
    }

    public void downloadFile(Context context, String fileName, String destinationDirectory, String url) {
        if(url == null) return;
        DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName);

        downloadmanager.enqueue(request);
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void pickFile(Activity activity)
    {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("*/*");
        activity.startActivityForResult(fileIntent,FILE_PICK_CODE);
    }
}

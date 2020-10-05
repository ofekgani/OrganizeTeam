package com.example.organizeteam.Resources;

import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;

public class Image {

    public Uri setImageFromGallery(@Nullable Intent data, ImageView imageView)
    {
        imageView.setImageURI ( data.getData () );
        return data.getData ();
    }

    public void setImageFromUri(String data, ImageView imageView)
    {
        Picasso.get ().load(data).into(imageView);
    }
}

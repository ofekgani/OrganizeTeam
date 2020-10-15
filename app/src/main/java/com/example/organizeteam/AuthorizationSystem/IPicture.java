package com.example.organizeteam.AuthorizationSystem;

/**
 * This interface use to check when image upload success and give the image uri that uploaded.
 */
public interface IPicture {
    void onUploadImage(String uri);
}

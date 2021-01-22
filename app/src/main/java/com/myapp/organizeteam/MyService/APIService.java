package com.myapp.organizeteam.MyService;

import com.myapp.organizeteam.Core.Meeting;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {
    @Headers (
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAApbtYzLc:APA91bEv4x-xqONlIJ71H24fHn6jVNyGHOWtTi0154MRpiR_Lmc0h_4nS5s-wTFN7jytRv9c6AeSnuBogr2q5oQ8ZwTxynPaVO_a4STO6SDBFoV3NFdKEglubtCeBHJBMzLUWZ5XILoE"
            }
    )

    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body Sender root);

}


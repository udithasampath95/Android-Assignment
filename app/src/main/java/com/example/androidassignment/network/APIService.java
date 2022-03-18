package com.example.androidassignment.network;

import com.example.androidassignment.model.Data;
import com.example.androidassignment.model.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {
    @GET(".")
    Call<User> getUserList(@Query("page") int val);

}

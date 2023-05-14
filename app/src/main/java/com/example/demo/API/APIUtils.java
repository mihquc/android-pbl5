package com.example.demo.API;


import com.example.demo.Tree;

import java.util.List;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIUtils {
    private static final String BASE_URL = "http://127.0.0.1:8080";

    private MyAPI api;

    public APIUtils() {
        api = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(MyAPI.class);
    }
    public Single<List<Tree>> getTree(){
        return api.getTree();
    }
}


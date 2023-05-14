package com.example.demo.API;

import com.example.demo.Tree;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;

public interface MyAPI {

    @GET("trees")
    Single<List<Tree>> getTree();

}

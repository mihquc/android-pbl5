package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.demo.databinding.ActivityImageViewerBinding;

public class ImageViewerActivity extends AppCompatActivity {
    private ActivityImageViewerBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent receivedIntent = getIntent();
        if(receivedIntent != null){
            String ima = receivedIntent.getStringExtra("image");
            Glide.with(this).load(ima).into(binding.fullScreenImageView);
        }
    }

    public void closeFullScreen(View view) {
        finish();
    }

}
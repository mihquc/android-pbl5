package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.demo.databinding.ActivityDetailsBinding;
import com.example.demo.databinding.ActivityMainBinding;

public class DetailsActivity extends AppCompatActivity {

    private ActivityDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent receivedIntent = getIntent();
        if(receivedIntent != null){
            String data = receivedIntent.getStringExtra("name");
            String data1 = receivedIntent.getStringExtra("state");
            int hum = receivedIntent.getIntExtra("hum", 0);
            String ima = receivedIntent.getStringExtra("image");

            binding.tvName.setText(data);
            binding.tvState.setText(data1);
            binding.tvPercentHumidity.setText(hum+"%");
            binding.pbHumidity.setProgress(hum);
            Glide.with(this).load(ima).into(binding.ivPhoto);
        }
    }
}
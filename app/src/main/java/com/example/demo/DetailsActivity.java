package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.demo.databinding.ActivityDetailsBinding;
import com.example.demo.databinding.ActivityMainBinding;

public class DetailsActivity extends AppCompatActivity {

    private ActivityDetailsBinding binding;

    private TextView tvName;
    private TextView tvState;

    private TextView tvTemperature;
    private TextView tvHumidity;

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
            int tem = receivedIntent.getIntExtra("tem", 0);
            int hum = receivedIntent.getIntExtra("hum", 0);
            int ima = receivedIntent.getIntExtra("image", 0);

            binding.tvName.setText(data);
            binding.tvState.setText(data1);
            binding.tvDegreesCelsius.setText(tem+" độ C");
            binding.tvPercentHumidity.setText(hum+"%");
            binding.ivPhoto.setImageResource(ima);
        }
    }
}
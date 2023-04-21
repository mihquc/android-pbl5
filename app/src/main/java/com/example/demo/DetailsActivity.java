package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvState;

    private TextView tvNhietDo;
    private TextView tvDoAm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tvName = findViewById(R.id.tv_name);
        tvState = findViewById(R.id.tv_state);
        tvNhietDo = findViewById(R.id.tv_nhietdo);

        Intent receivedIntent = getIntent();
        if(receivedIntent != null){
            String data = receivedIntent.getStringExtra("name");
            String data1 = receivedIntent.getStringExtra("state");
            tvName.setText(data);
            tvState.setText(data1);

        }
    }
}
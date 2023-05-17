package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.demo.databinding.ActivityDetailsBinding;
import com.example.demo.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class DetailsActivity extends AppCompatActivity {
    private ActivityDetailsBinding binding;
    private static final String ESP8266_IP_ADDRESS = "192.168.1.102";

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
            Glide.with(this).load(ima).into(binding.ivPhoto);
        }

        binding.btnWatering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest("/on");
            }
        });
        binding.btnStopWatering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest("/off");
            }
        });

        startSensorValueUpdates();
    }
    private void sendRequest(String endpoint) {
        String url = "http://" + ESP8266_IP_ADDRESS + endpoint;
        new HttpRequestTask().execute(url);
    }
    private void startSensorValueUpdates() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(1000);  // Update every 1 second
                        String url = "http://" + ESP8266_IP_ADDRESS + "/sensor";
                        new HttpRequestTask().execute(url);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    private class HttpRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 0)
                return null;

            String urlString = params[0];
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (result.startsWith("Moisture Level:")) {
                    String sensorValue = result.replace("Moisture Level:", "").trim();
                    int humidity = Integer.parseInt(sensorValue);
                    double humidity1 = 100 - (humidity*100/1024);
                    binding.tvPercentHumidity.setText(humidity1 + "%");
                    binding.pbHumidity.setProgress((int) Math.round(humidity1));
                }
            }
        }
    }
}
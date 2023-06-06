package com.example.demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.demo.databinding.ActivityTreeFragmentBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class TreeFragment extends Fragment {
    private static final String ESP8266_IP_ADDRESS = "192.168.1.11";
    private ActivityTreeFragmentBinding binding;
    private Boolean isWatering = false;

    @NonNull
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityTreeFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

//        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        int Count = sharedPreferences.getInt("Count", 0);
//        binding.tvWateringTimes.setText(String.valueOf(Count)+" lần");
//        long currentTime = System.currentTimeMillis();
//        long oneDayInMillis = 24 * 60 * 60 * 1000;
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 13); // Đặt giờ là 0h (0 giờ)
//        calendar.set(Calendar.MINUTE, 57); // Đặt phút là 0
//        calendar.set(Calendar.SECOND, 0); // Đặt giây là 0
//        calendar.set(Calendar.MILLISECOND, 0); // Đặt mili giây là 0
//        long nextMidnightTime = calendar.getTimeInMillis() + oneDayInMillis;
//
//        if (currentTime >= nextMidnightTime) {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putInt("Count", 0);
//            editor.apply();
//
//            binding.tvWateringTimes.setText("0 lần");
//        }


        DatabaseReference imagesRef = FirebaseDatabase.getInstance().getReference().child("images");
        Query query = imagesRef.orderByChild("timestamp").limitToLast(1); // Sắp xếp theo thời gian và giới hạn kết quả là 1 ảnh mới nhất

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String imageUrl = childSnapshot.child("url").getValue(String.class);

                    Glide.with(view).load(imageUrl).into(binding.ivPhoto);
                    binding.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {;
                            Intent intent = new Intent(v.getContext(), ImageViewerActivity.class);
                            intent.putExtra("image",imageUrl);
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });

        binding.btnWatering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWateringOptions(v);
//                if(!isWatering){
//                    sendRequest("/on");
//
//                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", v.getContext().MODE_PRIVATE);
//                    int Count = sharedPreferences.getInt("Count", 0);
//                    Count += 1;
//                    isWatering = true;
//                    binding.tvWateringTimes.setText(String.valueOf(Count)+" lần");
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putInt("Count", Count);
////                    editor.putLong("lastWateringTime", System.currentTimeMillis());
//                    editor.apply();
//                }
            }
        });
        binding.btnStopWatering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest("/off");
                isWatering = false;
            }
        });
        startSensorValueUpdates();

        return view;
    }

    private void sendRequest(String endpoint) {
        String url = "http://" + ESP8266_IP_ADDRESS + endpoint;
        new TreeFragment.HttpRequestTask().execute(url);
    }
    private void startSensorValueUpdates() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(1000);  // Update every 1 second
                        String url = "http://" + ESP8266_IP_ADDRESS + "/sensor";
                        new TreeFragment.HttpRequestTask().execute(url);
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
                    double humidity1 = 100 - (humidity * 100 / 1024);
                    binding.tvPercentHumidity.setText((int) Math.round(humidity1) + "%");
                    binding.pbHumidity.setProgress((int) Math.round(humidity1));
                }
            }
        }
    }

    public void showWateringOptions(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.options_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.option1:
                        if(!isWatering){
                            sendRequest("/on");

                            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
                            int Count = sharedPreferences.getInt("Count", 0);
                            Count += 1;
                            isWatering = true;
                            binding.tvWateringTimes.setText(String.valueOf(Count)+" lần");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("Count", Count);
//                    editor.putLong("lastWateringTime", System.currentTimeMillis());
                            editor.apply();
                        }
                        return true;
                    case R.id.option2:
                        if(!isWatering){
                            sendRequest("/2seconds");

                            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
                            int Count = sharedPreferences.getInt("Count", 0);
                            Count += 1;
                            isWatering = true;
                            binding.tvWateringTimes.setText(String.valueOf(Count)+" lần");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("Count", Count);
//                    editor.putLong("lastWateringTime", System.currentTimeMillis());
                            editor.apply();
                        }
                        return true;
                    case R.id.option3:
                        if(!isWatering){
                            sendRequest("/5seconds");

                            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
                            int Count = sharedPreferences.getInt("Count", 0);
                            Count += 1;
                            isWatering = true;
                            binding.tvWateringTimes.setText(String.valueOf(Count)+" lần");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("Count", Count);
//                    editor.putLong("lastWateringTime", System.currentTimeMillis());
                            editor.apply();
                        }
                        return true;
                    case R.id.option4:
                        if(!isWatering){
                            sendRequest("/10seconds");

                            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
                            int Count = sharedPreferences.getInt("Count", 0);
                            Count += 1;
                            isWatering = true;
                            binding.tvWateringTimes.setText(String.valueOf(Count)+" lần");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("Count", Count);
//                    editor.putLong("lastWateringTime", System.currentTimeMillis());
                            editor.apply();
                        }
                        return true;
                    case R.id.option5:
                        if(!isWatering){
                            sendRequest("/20seconds");

                            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
                            int Count = sharedPreferences.getInt("Count", 0);
                            Count += 1;
                            isWatering = true;
                            binding.tvWateringTimes.setText(String.valueOf(Count)+" lần");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("Count", Count);
//                    editor.putLong("lastWateringTime", System.currentTimeMillis());
                            editor.apply();
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        if (currentHour >= 6 && currentHour < 18) {
            // Ban ngày
            binding.tvDayNight.setText("Ban ngày");
            binding.ivDayNight.setImageResource(R.drawable.baseline_wb_sunny_24);
        } else {
            // Ban đêm
            binding.tvDayNight.setText("Ban đêm");
            binding.ivDayNight.setImageResource(R.drawable.baseline_dark_mode_24);
        }

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int Count = sharedPreferences.getInt("Count", 0);
        binding.tvWateringTimes.setText(String.valueOf(Count)+" lần");
        long currentTime = System.currentTimeMillis();
        long oneDayInMillis = 24 * 60 * 60 * 1000;

        calendar.set(Calendar.HOUR_OF_DAY, 16); // Đặt giờ là 0h (0 giờ)
        calendar.set(Calendar.MINUTE, 35); // Đặt phút là 0
        calendar.set(Calendar.SECOND, 0); // Đặt giây là 0
        calendar.set(Calendar.MILLISECOND, 0); // Đặt mili giây là 0
        long nextMidnightTime = calendar.getTimeInMillis() + oneDayInMillis;

        if (currentTime >= nextMidnightTime) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("Count", 0);
            editor.apply();

            binding.tvWateringTimes.setText("0 lần");
        }
    }
}
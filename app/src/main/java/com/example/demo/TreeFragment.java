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
    private static final String ESP8266_IP_ADDRESS = "172.20.10.3";
    private ActivityTreeFragmentBinding binding;
    private Boolean isWatering = false;
    private String lightSensor;
    private int lightValue;

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
                    System.out.println(imageUrl);
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
                        String urlTemperature = "http://" + ESP8266_IP_ADDRESS + "/tempe";
                        String urlHumidity = "http://" + ESP8266_IP_ADDRESS + "/humid";
                        String urlLight = "http://" + ESP8266_IP_ADDRESS + "/light";
                        new TreeFragment.HttpRequestTask().execute(url, urlTemperature, urlHumidity,urlLight);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (!Thread.currentThread().isInterrupted()) {
//                    try {
//                        Thread.sleep(1000);  // Update every 1 second
//                        String url = "http://" + ESP8266_IP_ADDRESS + "/light";
//                        new TreeFragment.HttpRequestTask().execute(url);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (!Thread.currentThread().isInterrupted()) {
//                    try {
//                        Thread.sleep(1000);  // Update every 1 second
//                        String url = "http://" + ESP8266_IP_ADDRESS + "/tempe";
//                        new TreeFragment.HttpRequestTask().execute(url);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (!Thread.currentThread().isInterrupted()) {
//                    try {
//                        Thread.sleep(1000);  // Update every 1 second
//                        String url = "http://" + ESP8266_IP_ADDRESS + "/humid";
//                        new TreeFragment.HttpRequestTask().execute(url);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
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
                } if (result.startsWith("Light:")) {
                    lightValue = Integer.parseInt(result.replace("Light:", "").trim());
                } if (result.startsWith("Tempe:")) {
                    String temSensor = result.replace("Tempe:", "").trim();
                    binding.tvTemperature.setText(temSensor+"°C");
                } if (result.startsWith("Humid:")) {
                    String humSensor = result.replace("Humid:", "").trim();
                    binding.tvHumidity.setText("Độ ẩm không khí "+humSensor+"%");
                }
            }
//            String[] lines = result.split("\n");
//
//            for (String line : lines) {
//                if (line.startsWith("Moisture Level:")) {
//                    String sensorValue = line.replace("Moisture Level:", "").trim();
//                    int humidity = Integer.parseInt(sensorValue);
//                    double humidity1 = 100 - (humidity * 100 / 1024);
//                    binding.tvPercentHumidity.setText((int) Math.round(humidity1) + "%");
//                    binding.pbHumidity.setProgress((int) Math.round(humidity1));
//                } if (line.startsWith("Light:")) {
//                    String sensorValue = line.replace("Light:", "").trim();
//                    lightValue = Integer.parseInt(sensorValue);
//                    // TODO: Xử lý giá trị cảm biến ánh sáng
//                } if (line.startsWith("Tempe:")) {
//                    String sensorValue = line.replace("Tempe:", "").trim();
//                    double temperature = Double.parseDouble(sensorValue);
//                    binding.tvTemperature.setText(temperature+"°C");
//                } if (line.startsWith("Humid:")) {
//                    String sensorValue = line.replace("Humid:", "").trim();
//                    double humidity = Double.parseDouble(sensorValue);
//                    // TODO: Xử lý giá trị độ ẩm
//                    binding.tvHumidity.setText("Độ ẩm không khí "+humidity+"%");
//                }
//                // TODO: Xử lý các cảm biến khác (nếu có)
//            }
        }
    }

    public void showWateringOptions(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.options_menu, popupMenu.getMenu());

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.option1:
                        if(!isWatering){
                            sendRequest("/on");

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
                            sendRequest("/4seconds");

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
//        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        if(lightValue == 0){
            // Ban ngày
            binding.tvDayNight.setText("Ban ngày");
            binding.ivDayNight.setImageResource(R.drawable.baseline_wb_sunny_24);
        } else if (lightValue == 1) {
            // Ban đêm
            binding.tvDayNight.setText("Ban đêm");
            binding.ivDayNight.setImageResource(R.drawable.baseline_dark_mode_24);
        }
//        if (currentHour >= 6 && currentHour < 18) {
//            // Ban ngày
//            binding.tvDayNight.setText("Ban ngày");
//            binding.ivDayNight.setImageResource(R.drawable.baseline_wb_sunny_24);
//        } else {
//            // Ban đêm
//            binding.tvDayNight.setText("Ban đêm");
//            binding.ivDayNight.setImageResource(R.drawable.baseline_dark_mode_24);
//        }

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int Count = sharedPreferences.getInt("Count", 0);
        binding.tvWateringTimes.setText(String.valueOf(Count)+" lần");
        long currentTime = System.currentTimeMillis();
        long oneDayInMillis = 24 * 60 * 60 * 1000;

        calendar.set(Calendar.HOUR_OF_DAY, 0); // Đặt giờ là 0h (0 giờ)
        calendar.set(Calendar.MINUTE, 0); // Đặt phút là 0
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
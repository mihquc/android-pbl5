package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.demo.databinding.ActivityResetPasswordBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class ResetPassword extends AppCompatActivity {
    private ActivityResetPasswordBinding binding;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    private String email = "";
    private String NewPassword="";
    private String ConfirmPassword="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }
    private void validateData(){
        NewPassword = binding.newPassEt.getText().toString().trim();
        ConfirmPassword = binding.ConfirmPassEt.getText().toString().trim();

        if(TextUtils.isEmpty(NewPassword)){
            Toast.makeText(this, "Enter new password...", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(ConfirmPassword)){
            Toast.makeText(this, "Enter confirm password...", Toast.LENGTH_SHORT).show();
        } else if(!NewPassword.equals(ConfirmPassword)){
            Toast.makeText(this, "Password doesn't match...", Toast.LENGTH_SHORT).show();
        }
        else {
            updateProfile();
        }
    }
    private void updateProfile(){
        Log.d("TAG", "updateProfile: ");
        progressDialog.setMessage("Updating user profile....");
        progressDialog.show();

//        ResetPasswordTask task1 = new ResetPasswordTask();
//        task1.execute();

        Intent receivedIntent = getIntent();
        if (receivedIntent != null){
            email = receivedIntent.getStringExtra("email");
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DataSnapshot dataSnapshot = task.getResult();
                for (DataSnapshot user : dataSnapshot.getChildren()){
                    String emailUser = user.child("email").getValue(String.class);
                    if (emailUser != null && emailUser.equals(email)) {
                        Query query = ref.orderByChild("email").equalTo(email);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    String userId = userSnapshot.getKey();
                                    ref.child(userId).child("password").setValue(NewPassword);
                                }

                                // Thông báo cập nhật mật khẩu thành công
                                Toast.makeText(ResetPassword.this, "Update password successfully...", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Xử lý khi có lỗi xảy ra
                                Toast.makeText(ResetPassword.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "No user account...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        progressDialog.dismiss();

        onBackPressed();

//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("password", NewPassword);
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getUid());
//        databaseReference.child("password").setValue(NewPassword);
//        databaseReference.child("uid")
//                .updateChildren(hashMap)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Log.d("TAG", "onFailure: ");
//                        progressDialog.dismiss();
//                        Toast.makeText(ResetPassword.this, "Profile updated...",Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("TAG", "onFailure: ");
//                        progressDialog.dismiss();
//                        Toast.makeText(ResetPassword.this, "Failed to update db due to "+e.getMessage(),Toast.LENGTH_SHORT).show();
//                    }
//                });
    }
    public class ResetPasswordTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String uid = firebaseAuth.getUid();
            try {
                URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:update?key=AIzaSyD0nnUltKen1VywmBd-tAHjF3r7i0J4G2Q");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setDoOutput(true);

                String requestBody = "{\"localId\": \"" + uid + "\", \"password\": \"" + NewPassword + "\"}";

                try (OutputStream outputStream = connection.getOutputStream()) {
                    byte[] requestBodyBytes = requestBody.getBytes("utf-8");
                    outputStream.write(requestBodyBytes, 0, requestBodyBytes.length);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Successfully: " + responseCode);
                } else {
                    System.out.println("Error: " + responseCode);
                }

                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Xử lý kết quả hoặc thông báo sau khi hoàn thành tác vụ mạng
            Toast.makeText(ResetPassword.this, "Update password successfully...", Toast.LENGTH_SHORT).show();
        }
    }
}
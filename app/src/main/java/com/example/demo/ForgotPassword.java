package com.example.demo;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.example.demo.databinding.ForgotDialogPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ForgotPassword extends Dialog {
    private ForgotDialogPasswordBinding binding;
    private FirebaseAuth firebaseAuth;

    public ForgotPassword(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ForgotDialogPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.btResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etEmail.getText().toString().trim();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.get().addOnCompleteListener(task -> {
                   if (task.isSuccessful()){
                       DataSnapshot dataSnapshot = task.getResult();
                       for (DataSnapshot user : dataSnapshot.getChildren()){
                           String emailUser = user.child("email").getValue(String.class);
                           if (emailUser != null && emailUser.equals(email)) {
                               Intent intent = new Intent(v.getContext(), ResetPassword.class);
                               intent.putExtra("email", email);
                               v.getContext().startActivity(intent);
                           } else {
                               Toast.makeText(v.getContext(), "No user account...", Toast.LENGTH_SHORT).show();
                           }
                       }
                   }
                });

//                if (firebaseAuth.getCurrentUser() != null) {
//                    List<UserInfo> userInfos = (List<UserInfo>) firebaseAuth.getCurrentUser().getProviderData();
//                    for (UserInfo userInfo : userInfos) {
//                        String emailUser = userInfo.getEmail();
//                        if (emailUser != null && emailUser.equals(email)) {
//                            Intent intent = new Intent(v.getContext(), ResetPassword.class);
//                            v.getContext().startActivity(intent);
//                        } else {
//                            Toast.makeText(v.getContext(), "No user account...", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }else {
//                    Toast.makeText(v.getContext(), "error...", Toast.LENGTH_SHORT).show();
//                }

            }
        });
    }
}
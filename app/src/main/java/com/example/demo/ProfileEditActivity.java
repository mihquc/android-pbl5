package com.example.demo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.demo.databinding.ActivityProfileEditBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ProfileEditActivity extends AppCompatActivity {

    private ActivityProfileEditBinding binding;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    private static final String TAG = "PROFILE_EDIT_TAG";

    private Uri imageUri = null;

    private String name = "";
    private String password="";
    private String CurrentPassword="";
    private String NewPassword="";
    private String ConfirmPassword="";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();



        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageAttachMenu();
            }
        });

        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }
    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: ");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email = ""+snapshot.child("email").getValue();
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        password = ""+snapshot.child("password").getValue();
//                        String userType = ""+snapshot.child("userType").getValue();
                        binding.nameEt.setText(name);

                        if(!isDestroyed()){
                            Glide.with(ProfileEditActivity.this)
                                    .load(profileImage)
                                    .placeholder(R.drawable.baseline_person_gray)
                                    .into(binding.profileIv);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void validateData(){
        name = binding.nameEt.getText().toString().trim();
        CurrentPassword = binding.passEt.getText().toString().trim();
        NewPassword = binding.newPassEt.getText().toString().trim();
        ConfirmPassword = binding.ConfirmPassEt.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Enter name...", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(CurrentPassword)){
            Toast.makeText(this, "Enter current password...", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(NewPassword)){
            Toast.makeText(this, "Enter new password...", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(ConfirmPassword)){
            Toast.makeText(this, "Enter confirm password...", Toast.LENGTH_SHORT).show();
        } else if(!NewPassword.equals(ConfirmPassword)){
            Toast.makeText(this, "Password doesn't match...", Toast.LENGTH_SHORT).show();
        }
        else {
            if(imageUri == null){
                updateProfile("");
            } else {
                uploadImage();
            }
        }
    }

    private void uploadImage(){
        progressDialog.setMessage("Updating profile image");
        progressDialog.show();

        String filePathAndName = "ProfileImages/"+firebaseAuth.getUid();
        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName);
        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: ");
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uploadedImageUrl = ""+uriTask.getResult();
                        Log.d(TAG, "onSuccess: blah blah ");
                        updateProfile(uploadedImageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: ");
                        progressDialog.dismiss();
                        Toast.makeText(ProfileEditActivity.this,"Failed to upload image due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateProfile(String imageUrl){
        if(password.equals(CurrentPassword)){
            Log.d(TAG, "updateProfile: ");
            progressDialog.setMessage("Updating user profile....");
            progressDialog.show();

            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("password", NewPassword);
            firebaseUser.updatePassword(NewPassword);
            hashMap.put("name",""+name);
            if (imageUri != null){
                hashMap.put("profileImage",""+imageUrl);
            }
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(firebaseAuth.getUid())
                    .updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "onFailure: ");
                            progressDialog.dismiss();
                            Toast.makeText(ProfileEditActivity.this, "Profile updated...",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: ");
                            progressDialog.dismiss();
                            Toast.makeText(ProfileEditActivity.this, "Failed to update db due to "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(this, "Current password is not valid... ", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImageAttachMenu(){
        PopupMenu popupMenu = new PopupMenu(this, binding.profileIv);
        popupMenu.getMenu().add(Menu.NONE,0,0,"Camera");
        popupMenu.getMenu().add(Menu.NONE,1,1,"Photos");

        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int which = item.getItemId();
                if (which==0){
                    pickImageCamera();
                }
                else if (which==1){
                    pickImagePhotos();
                }
                return false;
            }
        });
    }

    private void pickImageCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample Image Description");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        cameraActivityResultLauncher.launch(intent);
    }
    private void pickImagePhotos(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setPackage("com.google.android.apps.photos");
        photosActivityResultLauncher.launch(intent);
    }
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Log.d(TAG, "onActivityResult: "+imageUri);
                        Intent data = result.getData();
                        binding.profileIv.setImageURI(imageUri);
                    }
                    else {
                        Toast.makeText(ProfileEditActivity.this,"Cacnelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private ActivityResultLauncher<Intent> photosActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Log.d(TAG, "onActivityResult: "+imageUri);
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: ");
                        binding.profileIv.setImageURI(imageUri);
                    }
                }
            }
    );
}
package com.example.demo;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;

import com.example.demo.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ActivityMainBinding binding;
    private ArrayList<Tree> treeList;
    private TreeAdapter treeAdapter;
    private int i=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        treeList = new ArrayList<>();
        treeAdapter = new TreeAdapter(treeList);
        TreeFragment treeFragment = new TreeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, treeFragment)
                .commit();
        initBottomNav();

//        binding.rcvTree.setLayoutManager(new LinearLayoutManager(this));
//        binding.rcvTree.setAdapter(treeAdapter);

        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.ibLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

//        binding.edSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                try {
//                    treeAdapter.getFilter().filter(s);
//                } catch (Exception e){
//                Log.d("TAG", "onTextChanged: "+e.getMessage());
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("images");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String imageUrl = snapshot.child("url").getValue(String.class);
                treeList.add(new Tree(i, imageUrl, "Cây Ớt", "Có quả", "", 37));
                treeAdapter.notifyDataSetChanged();
                i++;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");
//        storageRef.listAll()
//                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
//                    @Override
//                    public void onSuccess(ListResult listResult) {
//                        for (StorageReference item : listResult.getItems()) {
//                            // Lấy URL của ảnh
//                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    treeList.add(new Tree(i, uri.toString(), "Cây Ớt", "Có quả", "", 37));
//                                    treeAdapter.notifyDataSetChanged();
//                                }
//                            });
//                            i += 1;
//                        }
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });

    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        else {
            String email = firebaseUser.getEmail();

            binding.tvSubtitle.setText(email);
        }
    }

    private void initBottomNav() {

        binding.mainLayout.content.bottomNavView.setOnItemSelectedListener(v ->
        {
            switch (v.getItemId()) {
                case R.id.bottom_my_plants:
                        openMyPlants();
                    break;
                case R.id.bottom_add_plant:
                    TreeFragment treeFragment = new TreeFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, treeFragment)
                            .commit();
                    break;
                default:
                    return false;
            }
            return true;
        });
    }

    private void openMyPlants() {
        // Tạo đối tượng TreeListFragment và truyền treeAdapter vào
        TreeListFragment treeListFragment = new TreeListFragment();
        treeListFragment.setAdapter(treeAdapter);

        // Thay thế container bằng fragment trong transaction
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, treeListFragment)
                .commit();
    }

}
package com.example.demo;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;

import com.example.demo.API.APIUtils;
import com.example.demo.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ArrayList<Tree> treeList;
    private TreeAdapter treeAdapter;
    private APIUtils apiUtils;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);

        treeList = new ArrayList<>();
        treeAdapter = new TreeAdapter(treeList);

        binding.rcvTree.setLayoutManager(new LinearLayoutManager(this));
        binding.rcvTree.setAdapter(treeAdapter);

        binding.toolbar.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    binding.toolbar.btSearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            treeAdapter.getFilter().filter(s);
                        }
                    });
                } catch (Exception e){
                Log.d("TAG", "onTextChanged: "+e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");
        storageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            // Lấy URL của ảnh
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    treeList.add(new Tree(1, uri.toString(), "Ớt", "Có quả", "", 50));
                                    treeAdapter.notifyDataSetChanged();
                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


//        treeList.add(new Tree(R.drawable.tree1,"Cây táo", "Chưa có quả", "",  50));
//        treeList.add(new Tree(R.drawable.tree2,"Cây quýt", "Chưa có hoa", "",  40));
//        treeList.add(new Tree(R.drawable.tree3,"Cây ớt", "Đã có quả", "",  60));
//        treeList.add(new Tree(R.drawable.tree1,"Cây mận", "Chưa có hoa", "", 27, 30));
//        treeList.add(new Tree(R.drawable.tree5,"Cây mận", "Chưa có hoa", "", 28, 50));
//        treeList.add(new Tree(R.drawable.tree1,"Cây ớt", "Đã có quả", "", 26, 70));
//        treeList.add(new Tree(R.drawable.tree3,"Cây cà chua", "Đã có quả", "", 26, 40));
//        treeList.add(new Tree(R.drawable.tree5,"Cây ổi", "Đã có quả", "", 25, 30));
//        treeAdapter.notifyDataSetChanged();

    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                treeAdapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//
//           return true;
//    }

}
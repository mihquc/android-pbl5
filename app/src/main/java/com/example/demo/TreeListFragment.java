package com.example.demo;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.demo.databinding.TreeListFragmentBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class TreeListFragment extends Fragment {
    private TreeAdapter treeAdapter;
    private int i = 1;
    private TreeListFragmentBinding binding;

    public void setAdapter(TreeAdapter treeAdapter){
        this.treeAdapter = treeAdapter;
    }

    @NonNull
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tree_list_fragment, container, false);

//        treeList = new ArrayList<>();
//        treeAdapter = new TreeAdapter(treeList);

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
//                                    treeList.add(new Tree(i, uri.toString(), "Cây Ớt", "Có quả", "", 0));
//                                    treeAdapter.notifyDataSetChanged();
//                                }
//                            });
//                            i += 1;
//                        }
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(Exception e) {
//
//                    }
//                });

        TextView textView = rootView.findViewById(R.id.ed_search);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    treeAdapter.getFilter().filter(s);
                } catch (Exception e){
                Log.d("TAG", "onTextChanged: "+e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        RecyclerView recyclerView = rootView.findViewById(R.id.rcv_tree);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(treeAdapter);

        return rootView;
    }

}
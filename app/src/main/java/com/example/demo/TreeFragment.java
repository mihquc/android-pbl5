package com.example.demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TreeFragment extends Fragment {

    @NonNull
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_tree_fragment, container, false);

        ImageView imageView = rootView.findViewById(R.id.iv_photo);
        CardView cardView = rootView.findViewById(R.id.cardView);

        DatabaseReference imagesRef = FirebaseDatabase.getInstance().getReference().child("images");
        Query query = imagesRef.orderByChild("timestamp").limitToLast(1); // Sắp xếp theo thời gian và giới hạn kết quả là 1 ảnh mới nhất

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String imageUrl = childSnapshot.child("url").getValue(String.class);

                    Glide.with(rootView).load(imageUrl).into(imageView);
                    cardView.setOnClickListener(new View.OnClickListener() {
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


        return rootView;
    }
}
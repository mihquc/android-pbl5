package com.example.demo;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TreeAdapter extends RecyclerView.Adapter<TreeAdapter.ViewHolder> implements Filterable{
    public ArrayList<Tree> treeList;
    public ArrayList<Tree> treeListOld;
    private FilterTree filter;
    private static final int REQ_CODE = 122;

    public TreeAdapter(ArrayList<Tree> treeList) {
        this.treeList = treeList;
        this.treeListOld = treeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tree_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tree tree = treeList.get(position);
        if(tree == null){
            return;
        }
        Glide.with(holder.context).load(tree.getImageTree()).into(holder.image);
        holder.tvName.setText(tree.getName());
        holder.tvState.setText(tree.getState());
        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LayoutInflater.from(v.getContext()).getContext(), DetailsActivity.class);
                intent.putExtra("name", tree.getName());
                intent.putExtra("state", tree.getState());
                intent.putExtra("image", tree.getImageTree());
                intent.putExtra("hum", tree.getHumidity());
                LayoutInflater.from(v.getContext()).getContext().startActivity(intent);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Delete")
                        .setMessage("Are you sure delete this Tree?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(v.getContext(), "Deleting...", Toast.LENGTH_SHORT).show();
                                treeList.remove(tree);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return treeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private Context context = itemView.getContext();
        private ImageView image;
        private TextView tvName;
        private TextView tvState;
        private ImageView btnDetails;
        private ImageView btnDelete;


        public ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image_tree);
            tvName = view.findViewById(R.id.tv_name);
            tvState = view.findViewById(R.id.tv_trangthai);
            btnDetails = view.findViewById(R.id.btn_details);
            btnDelete = view.findViewById(R.id.btn_delete);
        }
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterTree(treeListOld, this);
        }
        return filter;
    }
}

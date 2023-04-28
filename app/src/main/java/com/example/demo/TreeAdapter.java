package com.example.demo;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TreeAdapter extends RecyclerView.Adapter<TreeAdapter.ViewHolder> implements Filterable {
    private ArrayList<Tree> treeList;
    private ArrayList<Tree> treeListOld;
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
        holder.image.setImageResource(tree.getImageTree());
        holder.tvName.setText(tree.getName());
        holder.tvState.setText(tree.getState());
        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LayoutInflater.from(v.getContext()).getContext(), DetailsActivity.class);
                intent.putExtra("name", tree.getName());
                intent.putExtra("state", tree.getState());
                intent.putExtra("image", tree.getImageTree());
                intent.putExtra("tem", tree.getTemperature());
                intent.putExtra("hum", tree.getHumidity());
                LayoutInflater.from(v.getContext()).getContext().startActivity(intent);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                treeList.remove(tree);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return treeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
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
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if(strSearch.isEmpty()){
                    treeList = treeListOld;
                }else {
                    ArrayList<Tree> list = new ArrayList<>();
                    for(Tree tree : treeListOld){
                        if(tree.getName().toLowerCase().contains(strSearch.toLowerCase())){
                            list.add(tree);
                        }
                    }
                    treeList = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = treeList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                treeList = (ArrayList<Tree>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}

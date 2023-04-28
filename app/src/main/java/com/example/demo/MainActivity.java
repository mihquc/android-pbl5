package com.example.demo;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;

import com.example.demo.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ArrayList<Tree> treeList;
    private TreeAdapter treeAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);

        binding.rcvTree.setLayoutManager(new LinearLayoutManager(this));
        treeList = new ArrayList<>();
        treeAdapter = new TreeAdapter(treeList);
        binding.rcvTree.setAdapter(treeAdapter);

        treeList.add(new Tree(R.drawable.tree6,"Cây táo", "Chưa có quả", "", 25, 50));
        treeList.add(new Tree(R.drawable.tree2,"Cây quýt", "Chưa có hoa", "", 28, 40));
        treeList.add(new Tree(R.drawable.tree2,"Cây ớt", "Đã có quả", "", 27, 60));
        treeList.add(new Tree(R.drawable.tree1,"Cây mận", "Chưa có hoa", "", 27, 30));
        treeList.add(new Tree(R.drawable.tree5,"Cây mận", "Chưa có hoa", "", 28, 50));
        treeList.add(new Tree(R.drawable.tree1,"Cây ớt", "Đã có quả", "", 26, 70));
        treeList.add(new Tree(R.drawable.tree3,"Cây cà chua", "Đã có quả", "", 26, 40));
        treeList.add(new Tree(R.drawable.tree5,"Cây ổi", "Đã có quả", "", 25, 30));
        treeAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                treeAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }
}
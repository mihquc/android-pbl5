package com.example.demo;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterTree extends Filter {
    ArrayList<Tree> filterList;
    TreeAdapter treeAdapter;

    public FilterTree(ArrayList<Tree> filterList, TreeAdapter treeAdapter) {
        this.filterList = filterList;
        this.treeAdapter = treeAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if(constraint != null && constraint.length() > 0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<Tree> modelFilter = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){
                if(filterList.get(i).getState().toUpperCase().contains(constraint)){
                    modelFilter.add(filterList.get(i));
                }
            }

            results.count = modelFilter.size();
            results.values = modelFilter;
        }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        treeAdapter.treeList = (ArrayList<Tree>) results.values;
        treeAdapter.notifyDataSetChanged();
    }
}

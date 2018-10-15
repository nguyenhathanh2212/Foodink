package com.example.thanh.foodink.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.example.thanh.foodink.Adapter.RecyclerAdapter;
import com.example.thanh.foodink.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private View rootView;
    private RecyclerView categoryRecyclerView;
    private RecyclerView storeRecyclerView;
    private RecyclerAdapter categoryAdapter;
    private RecyclerAdapter storeAdapter;
    private ViewFlipper viewFlipper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_fragment, container, false);
        addControls();

        return rootView;
    }

    private void addControls() {
        viewFlipper = rootView.findViewById(R.id.view_flipper);
        viewFlipper.setFlipInterval(2000);
        viewFlipper.setAutoStart(true);

        categoryRecyclerView = rootView.findViewById(R.id.recycler_category);
        storeRecyclerView = rootView.findViewById(R.id.recycler_store);
        categoryRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(linearLayoutManager);
        storeRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        ArrayList<String> listData = new ArrayList<String>();
        listData.add("Aaaaaaa");
        listData.add("bbaaaaaav");
        listData.add("Aaaaaaa");
        listData.add("Aaaaaaa");
        listData.add("bbaaaaaav");
        listData.add("Aaaaaaa");
        listData.add("bbaaaaaav");
        listData.add("Aaaaaaa");
        listData.add("bbaaaaaav");
        storeAdapter = new RecyclerAdapter(listData);
        storeRecyclerView.setAdapter(storeAdapter);

        ArrayList<String> listData2 = new ArrayList<String>();
        listData2.add("Food");
        listData2.add("Drink");
        categoryAdapter = new RecyclerAdapter(listData2);
        categoryRecyclerView.setAdapter(categoryAdapter);
    }
}

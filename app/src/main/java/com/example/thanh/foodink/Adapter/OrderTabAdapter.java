package com.example.thanh.foodink.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.thanh.foodink.Fragment.CartFragment;
import com.example.thanh.foodink.Fragment.OrderHistoryFragment;

import java.util.ArrayList;

public class OrderTabAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment> fragments;
    private ArrayList<String> titles;

    public OrderTabAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        fragments = new ArrayList<>();
        fragments.add(new CartFragment());
        fragments.add(new OrderHistoryFragment());
        titles = new ArrayList<String >();
        titles.add("Carts");
        titles.add("Order Histories");
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}

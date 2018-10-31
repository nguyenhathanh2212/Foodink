package com.example.thanh.foodink.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.thanh.foodink.Fragment.CurrentNotificationFragment;
import com.example.thanh.foodink.Fragment.HomeFragment;
import com.example.thanh.foodink.Fragment.NotificationFragment;
import com.example.thanh.foodink.Fragment.OrderFragment;
import com.example.thanh.foodink.Fragment.ProfileFragment;
import com.example.thanh.foodink.Fragment.ShipperOrderListFragment;

import java.util.ArrayList;
import java.util.List;

public class NotificationTabAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment> fragments;
    private ArrayList<String> title;

    public NotificationTabAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        fragments = new ArrayList<>();
        fragments.add(new CurrentNotificationFragment());
        fragments.add(new ShipperOrderListFragment());

        title = new ArrayList<>();
        title.add("Thông báo");
        title.add("DS đơn hàng");
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }
}
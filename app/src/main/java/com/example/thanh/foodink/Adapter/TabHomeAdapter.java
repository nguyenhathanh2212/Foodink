package com.example.thanh.foodink.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.thanh.foodink.Fragment.HomeFragment;
import com.example.thanh.foodink.Fragment.NotificationFragment;
import com.example.thanh.foodink.Fragment.OrderFragment;
import com.example.thanh.foodink.Fragment.ProfileFragment;

import java.util.ArrayList;

public class TabHomeAdapter extends FragmentStatePagerAdapter {
//    private HomeFragment homeFragment;
//    private OrderFragment orderFragment;
//    private NotificationFragment notificationFragment;
//    private ProfileFragment profileFragment;

    private ArrayList<Fragment> fragments;
    private Context context;


    public TabHomeAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new OrderFragment());
        fragments.add(new NotificationFragment());
        fragments.add(new ProfileFragment());
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}

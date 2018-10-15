package com.example.thanh.foodink.Adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.thanh.foodink.Fragment.HomeFragment;
import com.example.thanh.foodink.Fragment.NotificationFragment;
import com.example.thanh.foodink.Fragment.OrderFragment;
import com.example.thanh.foodink.Fragment.ProfileFragment;
import com.example.thanh.foodink.R;

public class TabHomeAdapter extends FragmentStatePagerAdapter {
    private HomeFragment homeFragment;
    private OrderFragment orderFragment;
    private NotificationFragment notificationFragment;
    private ProfileFragment profileFragment;

    public TabHomeAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        homeFragment = new HomeFragment();
        orderFragment = new OrderFragment();
        notificationFragment = new NotificationFragment();
        profileFragment = new ProfileFragment();
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0: return homeFragment;
            case 1: return orderFragment;
            case 2: return notificationFragment;
            case 3: return profileFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}

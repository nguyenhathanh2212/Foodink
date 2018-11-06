package com.example.thanh.foodink.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.thanh.foodink.Activities.MainActivity;
import com.example.thanh.foodink.Adapter.NotificationTabAdapter;
import com.example.thanh.foodink.Adapter.TabHomeAdapter;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;

public class NotificationFragment extends Fragment {
    private View rootView;
    private TabLayout notificationTabLayout;
    private ViewPager notificationViewPager;
    private NotificationTabAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.notification_fragment, container, false);
        mappingWidgets();

        return rootView;
    }

    private void mappingWidgets() {
        notificationTabLayout = (TabLayout) rootView.findViewById(R.id.notification_tab_layout);
        notificationViewPager = (ViewPager) rootView.findViewById(R.id.notification_viewpager);
        adapter = new NotificationTabAdapter(MainActivity.getFragManager());
        notificationViewPager.setAdapter(adapter);
        notificationTabLayout.setupWithViewPager(notificationViewPager);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && User.checkUserAuth(getContext())) {
            mappingWidgets();
        }
    }
}

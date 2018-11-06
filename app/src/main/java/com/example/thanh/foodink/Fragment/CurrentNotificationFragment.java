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

import com.example.thanh.foodink.Adapter.CurrentNotificationRecyclerAdapter;
import com.example.thanh.foodink.Models.Notification;
import com.example.thanh.foodink.R;

import java.util.ArrayList;

public class CurrentNotificationFragment extends Fragment {
    private View rootView;
    private RecyclerView recyclerCurrentNotification;

    private CurrentNotificationRecyclerAdapter adapter;
    private ArrayList<Notification> listNotification;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.current_notification_fragment, container, false);

        listNotification = new ArrayList<>();
        listNotification.add(new Notification(1, "Abc's store", "13 Tôn đức thắng", "", 3.2f, 15000));
        listNotification.add(new Notification(1, "Vin's store", "23 Tôn đức thắng", "", 4.2f, 25000));
        listNotification.add(new Notification(1, "Zin's store", "131 Tôn đức thắng", "", 5f, 35000));
        listNotification.add(new Notification(1, "BB's store", "43 Tôn đức thắng", "", 7f, 10000));

        adapter = new CurrentNotificationRecyclerAdapter(listNotification);
        recyclerCurrentNotification = (RecyclerView) rootView.findViewById(R.id.recyclerCurrentNotification);
        recyclerCurrentNotification.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerCurrentNotification.setAdapter(adapter);
//        mappingWidgets();

        return rootView;
    }
}

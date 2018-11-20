package com.example.thanh.foodink.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.thanh.foodink.Adapter.CurrentNotificationRecyclerAdapter;
import com.example.thanh.foodink.Helpers.SessionManager;
import com.example.thanh.foodink.Models.Notification;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CurrentNotificationFragment extends Fragment {
    private View rootView;
    private RecyclerView recyclerCurrentNotification;

    private CurrentNotificationRecyclerAdapter adapter;
    private ArrayList<Notification> listNotification;
    private ImageView btnClearAll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.current_notification_fragment, container, false);
        loadData();

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
//            loadData();
        }
    }

    private void loadData() {
        SessionManager sessionManager = new SessionManager(getContext());
        String notificationsJson = sessionManager.get("NOTIFICATION_LIST");
        Gson gson = new Gson();
        listNotification = new ArrayList<>();

        if (!notificationsJson.equals("")) {
            Type type = new TypeToken<ArrayList<Notification>>() {}.getType();
            listNotification = gson.fromJson(notificationsJson, type);
        }

        adapter = new CurrentNotificationRecyclerAdapter(listNotification);
        recyclerCurrentNotification = (RecyclerView) rootView.findViewById(R.id.recyclerCurrentNotification);
        recyclerCurrentNotification.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerCurrentNotification.setAdapter(adapter);

        btnClearAll = (ImageView) rootView.findViewById(R.id.btnClearAll);
        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Xóa thông báo hiện tại")
                        .setMessage("Bạn có chắc chắn muốn xóa tất cả thông báo không?")
                        .setPositiveButton("Tất nhiên rồi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SessionManager sessionManager = new SessionManager(getContext());
                                sessionManager.forget("NOTIFICATION_LIST");
                                listNotification.clear();
                                adapter.notifyDataSetChanged();
                            }

                        })
                        .setNegativeButton("Không nhé", null)
                        .show();
            }
        });
    }
}

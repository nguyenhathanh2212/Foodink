package com.example.thanh.foodink.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thanh.foodink.Models.Notification;
import com.example.thanh.foodink.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CurrentNotificationRecyclerAdapter extends RecyclerView.Adapter<CurrentNotificationRecyclerAdapter.ViewHolder> {
    private ArrayList<Notification> listData;

    public CurrentNotificationRecyclerAdapter(ArrayList<Notification> listData) {
        this.listData = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_current_notification, viewGroup, false);

        return new CurrentNotificationRecyclerAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Notification notification = listData.get(i);
        viewHolder.tvStoreName.setText(notification.getStoreName());
        viewHolder.tvStoreAddress.setText(notification.getStoreAddress());
        viewHolder.tvDistanceToStore.setText(notification.getDistanceToStore() + "");
        viewHolder.tvShipCost.setText("Tiền ship: " + notification.getShipCost());

        String imageUrl = listData.get(i).getImgStore();

        if (!imageUrl.equals("")) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.loader)
                    .error(R.drawable.noimage)
                    .into(viewHolder.imgStore);
        } else {
            viewHolder.imgStore.setImageResource(R.drawable.noimage);
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgStore;
        private TextView tvStoreName;
        private TextView tvStoreAddress;
        private TextView tvDistanceToStore;
        private TextView tvShipCost;

        public ViewHolder(View itemView) {
            super(itemView);
            imgStore = itemView.findViewById(R.id.imgStore);
            tvStoreName = itemView.findViewById(R.id.tvStoreName);
            tvStoreAddress = itemView.findViewById(R.id.tvStoreAddress);
            tvDistanceToStore = itemView.findViewById(R.id.tvDistanceToStore);
            tvShipCost = itemView.findViewById(R.id.tvShipCost);
        }
    }
}

package com.example.thanh.foodink.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thanh.foodink.Activities.ShipperOrderDetailActivity;
import com.example.thanh.foodink.Helpers.SessionManager;
import com.example.thanh.foodink.Models.Notification;
import com.example.thanh.foodink.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
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
        viewHolder.tvDistanceToStore.setText(notification.getDistanceToStore() + "m");
        viewHolder.tvShipCost.setText(notification.getShipCost());

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

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClickItem(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(view.getContext(), ShipperOrderDetailActivity.class);
                intent.setAction(ShipperOrderDetailActivity.SHOW_RECEIVED_ORDER_ACTION);
                intent.putExtra("SHIPPER_ORDER_ID", listData.get(position).getOrderID());
                intent.putExtra("STORE_NAME", listData.get(position).getStoreName());
                view.getContext().startActivity(intent);
            }

            @Override
            public void onLongClickItem(final View view, final int position, boolean isLongClick) {
                new AlertDialog.Builder(view.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Xóa thông báo hiện tại")
                        .setMessage("Bạn có chắc chắn muốn xóa thông báo này không?")
                        .setPositiveButton("Tất nhiên rồi", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SessionManager sessionManager = new SessionManager(view.getContext());
                                String notificationsJson = sessionManager.get("NOTIFICATION_LIST");
                                ArrayList<Notification> nofiticationList = new ArrayList<>();
                                Gson gson = new Gson();

                                if (!notificationsJson.equals("")) {
                                    Type type = new TypeToken<ArrayList<Notification>>() {}.getType();
                                    nofiticationList = gson.fromJson(notificationsJson, type);
                                }

                                nofiticationList.remove(position);
                                listData.remove(position);
                                sessionManager.set("NOTIFICATION_LIST", gson.toJson(nofiticationList));

                                notifyDataSetChanged();
                            }

                        })
                        .setNegativeButton("Không nhé", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView imgStore;
        private TextView tvStoreName;
        private TextView tvStoreAddress;
        private TextView tvDistanceToStore;
        private TextView tvShipCost;
        private ItemClickListener itemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            imgStore = itemView.findViewById(R.id.imgStore);
            tvStoreName = itemView.findViewById(R.id.tvStoreName);
            tvStoreAddress = itemView.findViewById(R.id.tvStoreAddress);
            tvDistanceToStore = itemView.findViewById(R.id.tvDistanceToStore);
            tvShipCost = itemView.findViewById(R.id.tvShipCost);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClickItem(v, getAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onLongClickItem(v, getAdapterPosition(),true);
            return false;
        }
    }

    public interface ItemClickListener {
        void onClickItem(View view, int position, boolean isLongClick);
        void onLongClickItem(View view, int position, boolean isLongClick);
    }
}

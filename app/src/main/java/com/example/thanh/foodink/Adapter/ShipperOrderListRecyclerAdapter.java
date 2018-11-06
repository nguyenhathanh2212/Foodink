package com.example.thanh.foodink.Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thanh.foodink.Activities.ShipperOrderDetailActivity;
import com.example.thanh.foodink.Models.Notification;
import com.example.thanh.foodink.Models.ShipperOrder;
import com.example.thanh.foodink.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Vinh Nguyen on 11/1/2018.
 */

public class ShipperOrderListRecyclerAdapter extends RecyclerView.Adapter<ShipperOrderListRecyclerAdapter.ViewHolder> {
    private ArrayList<ShipperOrder> listData;

    public ShipperOrderListRecyclerAdapter(ArrayList<ShipperOrder> listData) {
        this.listData = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_shipper_order_list, viewGroup, false);

        return new ShipperOrderListRecyclerAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ShipperOrder shipperOrder = listData.get(i);
        viewHolder.tvStoreName.setText(shipperOrder.getStoreName());
        viewHolder.tvReceiverName.setText(shipperOrder.getReceiverName());
        viewHolder.tvShipCost.setText(shipperOrder.getShipCost());
        viewHolder.tvOrderStatus.setText(shipperOrder.getOrderStatus());

        if (shipperOrder.getOrderStatus().equals("Đang vận chuyển")) {
            viewHolder.tvOrderStatus.setTextColor(Color.parseColor("#00f208"));
        }

        String imageUrl = shipperOrder.getStoreImage();

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
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgStore;
        private TextView tvStoreName;
        private TextView tvReceiverName;
        private TextView tvShipCost;
        private TextView tvOrderStatus;
        private ItemClickListener itemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            imgStore = itemView.findViewById(R.id.imgStore);
            tvStoreName = itemView.findViewById(R.id.tvStoreName);
            tvReceiverName = itemView.findViewById(R.id.tvReceiverName);
            tvShipCost = itemView.findViewById(R.id.tvShipCost);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClickItem(v, getAdapterPosition(),false);
        }
    }

    public interface ItemClickListener {
        void onClickItem(View view, int position, boolean isLongClick);
    }
}

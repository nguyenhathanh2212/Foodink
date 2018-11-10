package com.example.thanh.foodink.Adapter;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thanh.foodink.Models.Order;
import com.example.thanh.foodink.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderRecyclerAdapter extends RecyclerView.Adapter<OrderRecyclerAdapter.OrderViewHolder> {
    private ArrayList<Order> listData;
    private DecimalFormat formatter = new DecimalFormat("###,###,###");

    public OrderRecyclerAdapter(ArrayList<Order> listData) {
        this.listData =listData;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_order, viewGroup, false);

        return new OrderRecyclerAdapter.OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, final int i) {
        orderViewHolder.txtNameStore.setText(listData.get(i).getStore().getName() + "");
        orderViewHolder.txtStatus.setText(listData.get(i).getStatusCustom() + "");
        orderViewHolder.txtShipCost.setText(formatter.format(listData.get(i).getShipCost()) + "đ");
        orderViewHolder.txtTotal.setText(formatter.format(listData.get(i).getTotal()) + "đ");
        orderViewHolder.txtQuantityProduct.setText(listData.get(i).getDetailOrders().size() + "");
        ArrayList<String> imageUrls = listData.get(i).getStore().getImages();

        if (imageUrls.size() == 0 || imageUrls.get(0).equals("")) {
            orderViewHolder.imageStore.setImageResource(R.drawable.noimage);
        } else {
            Picasso.get()
                    .load(imageUrls.get(0))
                    .placeholder(R.drawable.loader)
                    .error(R.drawable.noimage)
                    .into(orderViewHolder.imageStore);
        }

        orderViewHolder.setOrderClickListener(new OrderClickListener() {
            @Override
            public void onClickItem(View view, int position, boolean isLongClick) {
                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.dialog_order_detail);
                Window window = dialog.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.BOTTOM);
                TextView txtAddress, txtPhone;
                txtAddress = dialog.findViewById(R.id.txt_address);
                txtPhone = dialog.findViewById(R.id.txt_phone);
                txtPhone.setText(listData.get(i).getPhone() + "");
                txtAddress.setText(listData.get(i).getAddress() + "");
                RecyclerView orderDetailRecycleView = dialog.findViewById(R.id.recycle_list_order_detail);
                orderDetailRecycleView.setLayoutManager(new LinearLayoutManager(dialog.getContext(), LinearLayoutManager.VERTICAL, false));
                OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(listData.get(i).getDetailOrders());
                orderDetailRecycleView.setAdapter(orderDetailAdapter);
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtNameStore, txtStatus, txtTotal, txtShipCost, txtQuantityProduct;
        ImageView imageStore;
        private OrderRecyclerAdapter.OrderClickListener orderClickListener;

        public void setOrderClickListener(OrderRecyclerAdapter.OrderClickListener orderClickListener) {
            this.orderClickListener = orderClickListener;
            itemView.setOnClickListener(this);
        }

        public OrderViewHolder(View itemView) {
            super(itemView);
            txtNameStore = itemView.findViewById(R.id.txt_name_store);
            txtQuantityProduct = itemView.findViewById(R.id.txt_quantity_product);
            txtShipCost = itemView.findViewById(R.id.txt_ship_cost);
            txtStatus = itemView.findViewById(R.id.txt_status);
            txtTotal = itemView.findViewById(R.id.txt_price_total);
            imageStore = itemView.findViewById(R.id.img_store);
        }

        @Override
        public void onClick(View v) {
            orderClickListener.onClickItem(v, getAdapterPosition(), false);
        }
    }

    public interface OrderClickListener {
        void onClickItem(View view, int position,boolean isLongClick);
    }
}

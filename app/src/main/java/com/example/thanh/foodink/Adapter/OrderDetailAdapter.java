package com.example.thanh.foodink.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Models.Cart;
import com.example.thanh.foodink.Models.DetailOrder;
import com.example.thanh.foodink.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetaiViewHolder> {
    private ArrayList<DetailOrder> listData;
    private DecimalFormat formatter = new DecimalFormat("###,###,###");

    public OrderDetailAdapter(final ArrayList<DetailOrder> listData) {
        this.listData = listData;
    }
    @NonNull
    @Override
    public OrderDetaiViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.order_detail_item, viewGroup, false);

        return new OrderDetailAdapter.OrderDetaiViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetaiViewHolder orderDetaiViewHolder, int i) {
        orderDetaiViewHolder.txtName.setText(listData.get(i).getProduct().getName() + "");
        orderDetaiViewHolder.txtSize.setText(listData.get(i).getSize().getSize() + "");
        orderDetaiViewHolder.txtQuantity.setText(listData.get(i).getQuantity() + "");
        orderDetaiViewHolder.txtPrice.setText(formatter.format(listData.get(i).getSize().getPrice() * listData.get(i).getQuantity()) + "đ");
        ArrayList<String> imageUrls = listData.get(i).getImages();

        if (imageUrls.size() == 0 || imageUrls.get(0).equals("")) {
            orderDetaiViewHolder.imageView.setImageResource(R.drawable.noimage);
        } else {
            Picasso.get()
                    .load(imageUrls.get(0))
                    .placeholder(R.drawable.loader)
                    .error(R.drawable.noimage)
                    .into(orderDetaiViewHolder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class OrderDetaiViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView txtName, txtPrice, txtSize, txtQuantity;

        public OrderDetaiViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_product);
            txtName = itemView.findViewById(R.id.txt_name_product);
            txtPrice = itemView.findViewById(R.id.txt_price);
            txtSize = itemView.findViewById(R.id.txt_size);
            txtQuantity = itemView.findViewById(R.id.txt_quantity);
        }
    }
}

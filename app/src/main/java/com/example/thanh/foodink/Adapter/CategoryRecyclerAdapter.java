package com.example.thanh.foodink.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thanh.foodink.Activities.ProductCategoryActivity;
import com.example.thanh.foodink.Activities.StoreActivity;
import com.example.thanh.foodink.Models.Category;
import com.example.thanh.foodink.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder> {
    private ArrayList<Category> listData;

    public CategoryRecyclerAdapter(ArrayList<Category> listData) {
        this.listData = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_category, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.txtName.setText(listData.get(i).getName());
        String imageUrl = listData.get(i).getImage();
        if (!imageUrl.equals("")) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.loader)
                    .error(R.drawable.noimage)
                    .into(viewHolder.imageView);
        } else {
            viewHolder.imageView.setImageResource(R.drawable.noimage);
        }

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClickItem(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(view.getContext(), ProductCategoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("catedory_id", listData.get(position).getId());
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private TextView txtName;
        private ItemClickListener itemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_item_category);
            txtName = itemView.findViewById(R.id.txt_item_name_category);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClickItem(v, getAdapterPosition(), false);
        }
    }

    public interface ItemClickListener {
        void onClickItem(View view, int position,boolean isLongClick);
    }
}

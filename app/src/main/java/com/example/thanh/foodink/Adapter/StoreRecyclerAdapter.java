package com.example.thanh.foodink.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thanh.foodink.Activities.StoreActivity;
import com.example.thanh.foodink.Models.Store;
import com.example.thanh.foodink.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StoreRecyclerAdapter extends RecyclerView.Adapter<StoreRecyclerAdapter.StoreViewHolder> {
    private ArrayList<Store> listData;
    private int layoutId;
    private boolean isAllStores = false;

    public StoreRecyclerAdapter(ArrayList<Store> listData, int  layoutId) {
        this.listData = listData;
        this.layoutId = layoutId;
    }

    public StoreRecyclerAdapter(ArrayList<Store> listData, int  layoutId, boolean isAllStores) {
        this.listData = listData;
        this.layoutId = layoutId;
        this.isAllStores = isAllStores;
    }

    @NonNull
    @Override
    public StoreRecyclerAdapter.StoreViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(layoutId, viewGroup, false);

        return new StoreRecyclerAdapter.StoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreRecyclerAdapter.StoreViewHolder viewHolder, int i) {
        viewHolder.txtName.setText(listData.get(i).getName());
        viewHolder.txtDescription.setText(listData.get(i).getDescription(100));

        if (isAllStores) {
            viewHolder.txtLocation.setText(listData.get(i).getLocation(25));
        } else {
            viewHolder.txtLocation.setText(listData.get(i).getLocation(19));
        }
        ArrayList<String> imageUrls = listData.get(i).getImages();

        if (imageUrls.size() == 0 || imageUrls.get(0).equals("")) {
            viewHolder.imageView.setImageResource(R.drawable.noimage);
        } else {
            Picasso.get()
                    .load(listData.get(i).getImages().get(0))
                    .placeholder(R.drawable.loader)
                    .error(R.drawable.noimage)
                    .into(viewHolder.imageView);
        }


        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClickItem(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(view.getContext(), StoreActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("store", listData.get(position));
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class StoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        private ImageView imageView;
        private TextView txtName;
        private TextView txtDescription;
        private TextView txtLocation;
        private ItemClickListener itemClickListener;

        public StoreViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_item_store);
            txtName = itemView.findViewById(R.id.txt_item_name_store);
            txtDescription = itemView.findViewById(R.id.txt_item_description_store);
            txtLocation = itemView.findViewById(R.id.txt_item_location_store);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClickItem(v, getAdapterPosition(),false);
        }
    }

    public interface ItemClickListener {
        void onClickItem(View view, int position,boolean isLongClick);
    }
}

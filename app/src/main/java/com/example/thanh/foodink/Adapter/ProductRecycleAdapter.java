package com.example.thanh.foodink.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thanh.foodink.Dialog.ProductDialog;
import com.example.thanh.foodink.Models.Product;
import com.example.thanh.foodink.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductRecycleAdapter extends RecyclerView.Adapter<ProductRecycleAdapter.ItemViewHolder> {
    private ArrayList<Product> listData;
    private Context context;
    private FragmentManager fragmentManager;

    public ProductRecycleAdapter(ArrayList<Product> listData, Context context, FragmentManager fragmentManager) {
        this.listData = listData;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_product, viewGroup, false);

        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductRecycleAdapter.ItemViewHolder itemViewHolder, final int i) {
        itemViewHolder.txtName.setText(listData.get(i).getName());
        itemViewHolder.txtDescription.setText(listData.get(i).getDescription());
        int rate = (int) Math.round(listData.get(i).getRate());
        itemViewHolder.linearLayoutRate.removeAllViews();

        for (int star = 0; star < 5; star ++) {
            itemViewHolder.imageViewStar = new ImageView(context);

            if (star < rate) {
                itemViewHolder.imageViewStar.setImageResource(R.drawable.star_yellow);
            } else {
                itemViewHolder.imageViewStar.setImageResource(R.drawable.star_gray);
            }

            itemViewHolder.imageViewStar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            itemViewHolder.imageViewStar.setLayoutParams(new LinearLayout.LayoutParams(
                    25,
                    25
            ));
            itemViewHolder.linearLayoutRate.addView(itemViewHolder.imageViewStar);
        }
        ArrayList<String> imageUrls = listData.get(i).getImages();

        if (imageUrls.size() == 0 || imageUrls.get(0).equals("")) {
            itemViewHolder.imageView.setImageResource(R.drawable.noimage);
        } else {
            Picasso.get()
                    .load(listData.get(i).getImages().get(0))
                    .placeholder(R.drawable.loader)
                    .error(R.drawable.noimage)
                    .into(itemViewHolder.imageView);
        }

        itemViewHolder.imgDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDialog productDialog = ProductDialog.newInstance(listData.get(i));
                productDialog.show(fragmentManager, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder  {
        private ImageView imageView;
        private TextView txtName;
        private TextView txtDescription;
        private LinearLayout linearLayoutRate;
        private ImageView imageViewStar;
        private ImageView imgDetail;

        public ItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item_product);
            txtName = itemView.findViewById(R.id.txt_name_item_product);
            txtDescription = itemView.findViewById(R.id.txt_description_item_product);
            linearLayoutRate = itemView.findViewById(R.id.linearlayout_rate);
            imgDetail = itemView.findViewById(R.id.btimg_detail);
        }
    }
}

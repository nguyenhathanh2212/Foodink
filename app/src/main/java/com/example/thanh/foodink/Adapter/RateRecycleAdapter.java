package com.example.thanh.foodink.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thanh.foodink.Models.Rate;
import com.example.thanh.foodink.Models.Store;
import com.example.thanh.foodink.R;

import java.util.ArrayList;

public class RateRecycleAdapter extends RecyclerView.Adapter<RateRecycleAdapter.RateViewHolder> {
    private ArrayList<Rate> listData;
    private Context context;

    public RateRecycleAdapter(ArrayList<Rate> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }
    @NonNull
    @Override
    public RateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_rate, viewGroup, false);

        return new RateRecycleAdapter.RateViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RateViewHolder rateViewHolder, int i) {
        rateViewHolder.txtContent.setText(listData.get(i).getContent() + "");
        int rate = (int) Math.round(listData.get(i).getRate());
        rateViewHolder.linearLayoutRate.removeAllViews();

        for (int star = 0; star < 5; star ++) {
            rateViewHolder.imageViewStar = new ImageView(context);

            if (star < rate) {
                rateViewHolder.imageViewStar.setImageResource(R.drawable.star_yellow);
            } else {
                rateViewHolder.imageViewStar.setImageResource(R.drawable.star_gray);
            }

            rateViewHolder.imageViewStar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            rateViewHolder.imageViewStar.setLayoutParams(new LinearLayout.LayoutParams(
                    25,
                    25
            ));
            rateViewHolder.linearLayoutRate.addView(rateViewHolder.imageViewStar);
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class RateViewHolder extends RecyclerView.ViewHolder {
        private TextView txtContent;
        private LinearLayout linearLayoutRate;
        private ImageView imageViewStar;

        public RateViewHolder(View itemView) {
            super(itemView);
            txtContent = itemView.findViewById(R.id.txt_content);
            linearLayoutRate = itemView.findViewById(R.id.linearlayout_rate);
        }
    }
}

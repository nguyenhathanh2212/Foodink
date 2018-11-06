package com.example.thanh.foodink.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thanh.foodink.Models.Product;
import com.example.thanh.foodink.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShipperOrderProductAdapter extends ArrayAdapter<Product> {
    Activity context = null;
    ArrayList<Product> listProduct = null;
    int layoutId;

    public ShipperOrderProductAdapter(Activity context, int layoutId, ArrayList<Product> listProduct){
        super(context, layoutId, listProduct);
        this.context = context;
        this.layoutId = layoutId;
        this.listProduct = listProduct;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(layoutId, null);
        mappingWidgets(convertView, listProduct.get(position));

        return convertView;
    }

    private void mappingWidgets(View convertView, Product product) {
        ImageView productImage = convertView.findViewById(R.id.imgProduct);
        TextView tvProductName = convertView.findViewById(R.id.tvProductName);
        TextView tvProductType = convertView.findViewById(R.id.tvProductType);
        TextView tvProductSize = convertView.findViewById(R.id.tvProductSize);
        TextView tvProductPrice = convertView.findViewById(R.id.tvProductPrice);
        TextView tvProductQuantity = convertView.findViewById(R.id.tvProductQuantity);

        tvProductName.setText(product.getName());
        tvProductType.setText(product.getType());
        tvProductSize.setText(product.getSize());
        tvProductPrice.setText(product.getPrice());
        tvProductQuantity.setText("  x" + product.getQuantity());

        String imageUrl = product.getImage();

        if (!imageUrl.equals("")) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.loader)
                    .error(R.drawable.noimage)
                    .into(productImage);
        } else {
            productImage.setImageResource(R.drawable.noimage);
        }
    }
}

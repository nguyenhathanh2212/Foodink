package com.example.thanh.foodink.Adapter;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Models.Cart;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder> {
    private ArrayList<Cart> listData;
    private DecimalFormat formatter = new DecimalFormat("###,###,###");
    private RequestQueue requestQueue;
    private Progresser progress;

    public CartRecyclerAdapter(final ArrayList<Cart> listData) {
        this.listData = listData;
    }
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_cart, viewGroup, false);

        return new CartRecyclerAdapter.CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, final int i) {
        cartViewHolder.txtName.setText(listData.get(i).getProduct().getName() + "");
        cartViewHolder.txtSize.setText(listData.get(i).getSize().getSize() + "");
        cartViewHolder.txtQuantity.setText(listData.get(i).getQuantity() + "");
        cartViewHolder.txtPrice.setText(formatter.format(listData.get(i).getSize().getPrice() * listData.get(i).getQuantity()) + "đ");
        ArrayList<String> imageUrls = listData.get(i).getProduct().getImages();

        try {
            if (imageUrls.size() == 0 || imageUrls.get(0).equals("")) {
                cartViewHolder.imageView.setImageResource(R.drawable.noimage);
            } else {
                Picasso.get()
                        .load(imageUrls.get(0))
                        .placeholder(R.drawable.loader)
                        .error(R.drawable.noimage)
                        .into(cartViewHolder.imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cartViewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.dialog_confirm_delete);
                Button btnCancel, btnDelete;
                btnCancel = dialog.findViewById(R.id.btn_cancel);
                btnDelete = dialog.findViewById(R.id.btn_delete);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        progress = new Progresser(v.getContext(), "Xóa sản phẩm", "Đang xóa...");
                        progress.show();
                        requestQueue = Volley.newRequestQueue(v.getContext());
                        JsonObjectRequest objectRequest = new JsonObjectRequest(
                                Request.Method.DELETE,
                                ApiUrl.API_CARTS + "/" + listData.get(i).getId(),
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        progress.hide();
                                        Toast.makeText(v.getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        Cart oldCart = listData.remove(i);
                                        notifyItemRemoved(i);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progress.hide();
                                        Toast.makeText(v.getContext(), "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                                        Log.d("ApiError", error.toString());
                                        error.printStackTrace();
                                    }
                                }
                        ) {
                            public Map<String, String> getHeaders() {
                                Map<String, String> mHeaders = new ArrayMap<String, String>();
                                mHeaders.put("Authorization", User.getUserAuth(v.getContext()).getAuthToken());

                                return mHeaders;
                            }
                        };

                        requestQueue.add(objectRequest);
                    }
                });
                dialog.show();
            }
        });

        cartViewHolder.setCartClickListener(new CartClickListener() {
            @Override
            public void onClickItem(final View view, final int position, boolean isLongClick) {
                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.dialog_update_cart);
                Window window = dialog.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.BOTTOM);
                ImageView imgProduct;
                final TextView txtName, txtDescription, txtNumber, txtPrice, txtSize;
                Button btnCancel, btnUpdate;
                ImageButton btimgPlus, btimgSub;
                LinearLayout linearLayoutRate;
                ImageView imageViewStar;
                requestQueue = Volley.newRequestQueue(view.getContext());
                final DecimalFormat formatter = new DecimalFormat("###,###,###");
                final Cart cart = listData.get(position);
                txtName = dialog.findViewById(R.id.txt_name_detail_product);
                txtDescription = dialog.findViewById(R.id.txt_description_detail_product);
                txtNumber = dialog.findViewById(R.id.txt_number_item);
                imgProduct = dialog.findViewById(R.id.image_detail_product);
                txtPrice = dialog.findViewById(R.id.txt_price_total);
                txtSize = dialog.findViewById(R.id.txt_size);
                btnCancel = dialog.findViewById(R.id.btn_dialog_cancel);
                btnUpdate = dialog.findViewById(R.id.btn_dialog_update);
                btimgSub = dialog.findViewById(R.id.btimg_sub_number_item);
                btimgPlus = dialog.findViewById(R.id.btimg_plus_number_item);
                linearLayoutRate = dialog.findViewById(R.id.linearlayout_rate_detail);
                txtName.setText(cart.getProduct().getName() + "");
                txtDescription.setText(cart.getProduct().getDescription() + "");
                txtNumber.setText(cart.getQuantity() + "");
                txtPrice.setText(formatter.format(cart.getQuantity() * cart.getSize().getPrice()) + "đ");
                txtSize.setText(cart.getSize().getSize() + "");

                // add image
                ArrayList<String> imageUrls = cart.getProduct().getImages();

                if (imageUrls.size() == 0 || imageUrls.get(0).equals("")) {
                    imgProduct.setImageResource(R.drawable.noimage);
                } else {
                    Picasso.get()
                            .load(cart.getProduct().getImages().get(0))
                            .placeholder(R.drawable.loader)
                            .error(R.drawable.noimage)
                            .into(imgProduct);
                }

                // add rate
                int rate = (int) Math.round(cart.getProduct().getRate());
                linearLayoutRate.removeAllViews();

                for (int star = 0; star < 5; star ++) {
                    imageViewStar = new ImageView(view.getContext());

                    if (star < rate) {
                        imageViewStar.setImageResource(R.drawable.star_yellow);
                    } else {
                        imageViewStar.setImageResource(R.drawable.star_gray);
                    }

                    imageViewStar.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageViewStar.setLayoutParams(new LinearLayout.LayoutParams(
                            25,
                            25
                    ));
                    linearLayoutRate.addView(imageViewStar);
                }
                btimgPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity = Integer.parseInt(txtNumber.getText().toString());
                        quantity +=1;
                        txtNumber.setText(quantity + "");
                        txtPrice.setText(formatter.format(quantity * cart.getSize().getPrice()) + "đ");
                    }
                });

                btimgSub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity = Integer.parseInt(txtNumber.getText().toString());

                        if (quantity > 1) {
                            quantity -= 1;
                            txtNumber.setText(quantity + "");
                        }
                        txtNumber.setText(quantity + "");
                        txtPrice.setText(formatter.format(quantity * cart.getSize().getPrice()) + "đ");
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        try {
                            progress = new Progresser(v.getContext(), "Cập nhật giỏ hàng", "Đang load...");
                            progress.show();
                            Map<String, String> params = new HashMap();
                            params.put("quantity", txtNumber.getText().toString());
                            JSONObject parameters = new JSONObject(params);
                            JsonObjectRequest objectRequest = new JsonObjectRequest(
                                    Request.Method.PATCH,
                                    ApiUrl.API_CARTS + "/" + cart.getId(),
                                    parameters,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            progress.hide();
                                            Toast.makeText(v.getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            Cart oldCart = listData.get(position);
                                            oldCart.setQuantity(Integer.parseInt(txtNumber.getText().toString()));
                                            listData.set(position, oldCart);
                                            notifyItemChanged(position);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            progress.hide();
                                            Toast.makeText(v.getContext(), "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                                            Log.d("ApiError", error.toString());
                                            error.printStackTrace();
                                        }
                                    }
                            ) {
                                public Map<String, String> getHeaders() {
                                    Map<String, String> mHeaders = new ArrayMap<String, String>();
                                    mHeaders.put("Authorization", User.getUserAuth(view.getContext()).getAuthToken());

                                    return mHeaders;
                                }
                            };

                            requestQueue.add(objectRequest);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                dialog.show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private TextView txtName, txtPrice, txtSize, txtQuantity;
        private CartClickListener cartClickListener;
        private ImageView imgDelete;

        public void setCartClickListener(CartClickListener cartClickListener) {
            this.cartClickListener = cartClickListener;
        }

        public CartViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_product);
            txtName = itemView.findViewById(R.id.txt_name_product);
            txtPrice = itemView.findViewById(R.id.txt_price);
            txtSize = itemView.findViewById(R.id.txt_size);
            txtQuantity = itemView.findViewById(R.id.txt_quantity);
            imgDelete = itemView.findViewById(R.id.delete_item_cart);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            cartClickListener.onClickItem(v, getAdapterPosition(), false);
        }
    }

    public interface CartClickListener {
        void onClickItem(View view, int position,boolean isLongClick);
    }
}

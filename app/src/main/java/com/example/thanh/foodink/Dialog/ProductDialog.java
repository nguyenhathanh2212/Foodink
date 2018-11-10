package com.example.thanh.foodink.Dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Activities.LoginActivity;
import com.example.thanh.foodink.Activities.MainActivity;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Helpers.SessionManager;
import com.example.thanh.foodink.Models.Cart;
import com.example.thanh.foodink.Models.Product;
import com.example.thanh.foodink.Models.Size;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductDialog extends DialogFragment implements View.OnClickListener {
    private ImageView imgProduct;
    private TextView txtName, txtDescription, txtNumber;
    private Button btnAddCart;
    private ImageButton btimgPlus, btimgSub;
    private LinearLayout linearLayoutRate;
    private ImageView imageViewStar;
    private RadioGroup radioGroupSize;
    private DecimalFormat formatter = new DecimalFormat("###,###,###");
    private Product product;
    private RequestQueue requestQueue;
    private Progresser progress;

    public static ProductDialog newInstance(Product product) {
        ProductDialog dialog = new ProductDialog();
        Bundle args = new Bundle();
        args.putSerializable("product", product);
        dialog.setArguments(args);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_detail, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addControls(view);
        addEvents(view);
    }

    private void addEvents(View view) {
        btimgPlus.setOnClickListener(this);
        btimgSub.setOnClickListener(this);
        btnAddCart.setOnClickListener(this);
    }

    private void addControls(View view) {
        txtName = view.findViewById(R.id.txt_name_detail_product);
        txtDescription = view.findViewById(R.id.txt_description_detail_product);
        txtNumber = view.findViewById(R.id.txt_number_item);
        imgProduct = view.findViewById(R.id.image_detail_product);
        btnAddCart = view.findViewById(R.id.btn_detail_add_cart);
        btimgSub = view.findViewById(R.id.btimg_sub_number_item);
        btimgPlus = view.findViewById(R.id.btimg_plus_number_item);
        linearLayoutRate = view.findViewById(R.id.linearlayout_rate_detail);
        radioGroupSize = view.findViewById(R.id.group_size_detail);

        this.product = (Product) getArguments().getSerializable("product");
        txtName.setText(product.getName());
        txtDescription.setText(product.getDescription());
        txtDescription.setMovementMethod(new ScrollingMovementMethod());
        btnAddCart.setText("Thêm vào giỏ ("+ formatter.format(product.getSizes().get(0).getPrice()) + "đ)");

        RadioButton radioSize;
        int index = 0;
        for (Size size: product.getSizes()) {
            radioSize = new RadioButton(this.getContext());
            radioSize.setId(size.getId());
            radioSize.setText("Size " + size.getSize() + " (" + formatter.format(size.getPrice()) + "đ)");
            radioSize.setTextSize(13);

            if (index == 0) {
                radioSize.setChecked(true);
            }
            radioGroupSize.addView(radioSize);
            index++;
        }

        ArrayList<String> imageUrls = product.getImages();

        if (imageUrls.size() == 0 || imageUrls.get(0).equals("")) {
            imgProduct.setImageResource(R.drawable.noimage);
        } else {
            Picasso.get()
                    .load(product.getImages().get(0))
                    .placeholder(R.drawable.loader)
                    .error(R.drawable.noimage)
                    .into(imgProduct);
        }

        int rate = (int) Math.round(product.getRate());
        linearLayoutRate.removeAllViews();

        for (int star = 0; star < 5; star ++) {
            imageViewStar = new ImageView(this.getContext());

            if (star < rate) {
                imageViewStar.setImageResource(R.drawable.start_blue);
            } else {
                imageViewStar.setImageResource(R.drawable.start_gray);
            }

            imageViewStar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViewStar.setLayoutParams(new LinearLayout.LayoutParams(
                    25,
                    25
            ));
            linearLayoutRate.addView(imageViewStar);
        }
    }

    @Override
    public void onClick(View v) {
        int sizeId = radioGroupSize.getCheckedRadioButtonId();
        int quantity = 0;
        double price = 0;

        if (v.getId() == btimgPlus.getId()) {
            quantity = Integer.parseInt(txtNumber.getText().toString());
            quantity +=1;
            txtNumber.setText(quantity + "");

            for (Size size: product.getSizes()) {
                if (size.getId() == sizeId) {
                    price = quantity * size.getPrice();
                    btnAddCart.setText("Thêm vào giỏ ("+ formatter.format(price) + "đ)");
                    break;
                }
            }
        }

        if (v.getId() == btimgSub.getId()) {
            quantity = Integer.parseInt(txtNumber.getText().toString());

            if (quantity > 1) {
                quantity -= 1;
                txtNumber.setText(quantity + "");
            }

            for (Size size: product.getSizes()) {
                if (size.getId() == sizeId) {
                    price = quantity * size.getPrice();
                    btnAddCart.setText("Thêm vào giỏ ("+ formatter.format(price) + "đ)");
                    break;
                }
            }
        }

        if (v.getId() == btnAddCart.getId()) {
            quantity = Integer.parseInt(txtNumber.getText().toString());

            for (Size size: product.getSizes()) {
                if (size.getId() == sizeId) {
                    break;
                }
            }

            SessionManager sessionManager = new SessionManager(this.getContext());
            if (sessionManager.has(User.AUTH)) {
                Cart cart = new Cart(product.getId(), radioGroupSize.getCheckedRadioButtonId(), 1, quantity, price);
                addCart(cart, this);
            } else {
                final Dialog dialog = new Dialog(this.getContext());
                dialog.setContentView(R.layout.dialog_checklogin);
                Button btnCancel = dialog.findViewById(R.id.btn_dialog_checklogin_cancel);
                Button btnLogin = dialog.findViewById(R.id.btn_dialog_checklogin_login);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        }
    }

    private void addCart(Cart cart, final ProductDialog productDialog) {
        requestQueue = Volley.newRequestQueue(this.getContext());
        progress = new Progresser(getContext(), "Thêm giỏ hàng", "Đang load...");
        progress.show();
        Map<String, String> params = new HashMap();
        params.put("product_id", cart.getProduct_id() + "");
        params.put("size_id", cart.getSize_id() + "");
        params.put("quantity", cart.getQuantity() + "");
        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApiUrl.API_CARTS,
                parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.hide();
                        productDialog.dismiss();
                        Toast.makeText(getContext(), "Thêm giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hide();
                        Toast.makeText(getContext(), "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        Log.d("ApiError", error.toString());
                        error.printStackTrace();
                    }
                }
        ) {
            public Map<String, String> getHeaders() {
                Map<String, String> mHeaders = new ArrayMap<String, String>();
                mHeaders.put("Authorization", User.getUserAuth(getContext()).getAuthToken());

                return mHeaders;
            }
        };

        requestQueue.add(objectRequest);
    }
}

package com.example.thanh.foodink.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.thanh.foodink.Activities.LoginActivity;
import com.example.thanh.foodink.Activities.MainActivity;
import com.example.thanh.foodink.Adapter.CartRecyclerAdapter;
import com.example.thanh.foodink.Adapter.StoreRecyclerAdapter;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Dialog.CheckConnectionDialog;
import com.example.thanh.foodink.Exceptions.ValidationException;
import com.example.thanh.foodink.Helpers.CheckConnection;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Helpers.SessionManager;
import com.example.thanh.foodink.Helpers.Validation;
import com.example.thanh.foodink.Models.Cart;
import com.example.thanh.foodink.Models.Product;
import com.example.thanh.foodink.Models.Size;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartFragment extends Fragment implements View.OnClickListener {
    private View rootView;
    private RecyclerView cartRecyclerView;
    private CartRecyclerAdapter cartRecyclerAdapter;
    private Button btnOrder;
    private RequestQueue requestQueue;
    private Progresser progress;
    private SessionManager sessionManager;
    private ArrayList<Cart> carts;
    private LinearLayout linearRequest;
    private TextView txtMsg, txtTitle;
    private ImageView imgMsg;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CheckConnection checkConnection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.cart_fragment, container, false);
        addControls();
        addEvents();

        return rootView;
    }

    private void addEvents() {
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (checkConnection.isNetworkAvailable() && sessionManager.has(User.AUTH)) {
                            carts.clear();
                            showCarts();
                        } else {
                            Toast.makeText(getContext(), "Vui lòng bật kết nối mạng!", Toast.LENGTH_SHORT).show();
                        }

                        swipeRefreshLayout.setRefreshing(false);
                    }

                }
        );
    }

    private void addControls() {
        linearRequest = rootView.findViewById(R.id.request_screen);
        cartRecyclerView = rootView.findViewById(R.id.recycler_all_cart);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        btnOrder = rootView.findViewById(R.id.btn_order);
        requestQueue = Volley.newRequestQueue(this.getContext());
        sessionManager = new SessionManager(getContext());
        checkConnection = new CheckConnection(this.getContext());
        swipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);
        carts = new ArrayList<>();
        cartRecyclerAdapter = new CartRecyclerAdapter(carts);
        cartRecyclerView.setAdapter(cartRecyclerAdapter);

        if (checkConnection.isNetworkAvailable() && sessionManager.has(User.AUTH)) {
            progress = new Progresser(getContext(), "", "Đang load dữ liệu...");
            showCarts();

            btnOrder.setOnClickListener(this);
        }
    }

    private void showCarts() {
        progress.show();
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                ApiUrl.API_CARTS + "?get_all=1",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.hide();
                        try {
                            JSONArray jsonArray = response.getJSONArray("carts");
                            JSONObject jsonObject;
                            Product product;
                            Size size;
                            Cart cart;
                            JSONObject sizeObject, productObject;

                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                int quantity = jsonObject.getInt("quantity");
                                productObject = jsonObject.getJSONObject("product");

                                JSONArray jsonImageUrls = productObject.getJSONArray("pictures");
                                ArrayList<String> imageUrls = new ArrayList<String>();

                                for (int j = 0; j < jsonImageUrls.length(); j++) {
                                    String imageUrl = (String) jsonImageUrls.get(j);
                                    imageUrls.add(imageUrl);
                                }

                                product = new Product(
                                        productObject.getInt("id"),
                                        productObject.getString("name"),
                                        productObject.getString("description"),
                                        productObject.getDouble("avg_rate_score"),
                                        imageUrls
                                );

                                sizeObject = jsonObject.getJSONObject("size");
                                size = new Size(sizeObject.getInt("id"),
                                        sizeObject.getString("size"),
                                        sizeObject.getDouble("price")
                                );


                                cart = new Cart(id, quantity, product, size);
                                carts.add(cart);
                            }

                            if (carts.size() != 0) {
                                linearRequest.setVisibility(LinearLayout.INVISIBLE);
                            } else {
                                txtMsg = rootView.findViewById(R.id.txt_msg);
                                txtTitle = rootView.findViewById(R.id.txt_title);
                                imgMsg = rootView.findViewById(R.id.img_msg);
                                txtMsg.setText("Không có sản phẩm nào trong giỏ hàng");
                                txtTitle.setText("Giỏ hàng rỗng!");
                                imgMsg.setImageResource(R.drawable.empty_cart);
                                linearRequest.setVisibility(LinearLayout.VISIBLE);
                            }
                            cartRecyclerAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    @Override
    public void onClick(View v) {
        if (v.getId() == btnOrder.getId()) {
            final Dialog dialog = new Dialog(this.getContext());
            dialog.setContentView(R.layout.order_product);
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            dialog.setCancelable(false);
            final EditText edPhone = dialog.findViewById(R.id.ed_sdt);
            final EditText edAddress = dialog.findViewById(R.id.ed_address);
            Button btnCancel = dialog.findViewById(R.id.btn_cancel);
            Button btnOrder = dialog.findViewById(R.id.btn_dat_hang);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btnOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        progress = new Progresser(getContext(), "Thêm giỏ hàng", "Đang load...");
                        String phone = edPhone.getText().toString();
                        String address = edAddress.getText().toString();
                        validate(phone, address, edPhone, edAddress);
                        progress.show();
                        Map<String, String> params = new HashMap();
                        params.put("phone", phone);
                        params.put("address", address);
                        JSONObject parameters = new JSONObject(params);
                        JsonObjectRequest objectRequest = new JsonObjectRequest(
                                Request.Method.POST,
                                ApiUrl.API_ORDERS,
                                parameters,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        progress.hide();
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progress.hide();
                                        Toast.makeText(getContext(), "Bạn vừa nhập địa chỉ không đúng, vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
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
                    } catch (ValidationException e) {
                        Toast.makeText(dialog.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            dialog.show();
        }
    }

    private void validate(String phone, String address, EditText edPhone, EditText edAddress) throws ValidationException {
        if (phone.equals("")) {
            edPhone.requestFocus();
            throw new ValidationException("Số điện thoại không được để trống");
        }

        if (!Validation.isPhoneNumber(phone)) {
            edPhone.requestFocus();
            throw new ValidationException("Số điện thoại phải hợp lệ");
        }

        if (address.equals("")) {
            edAddress.requestFocus();
            throw new ValidationException("Địa chỉ không được để trống");
        }

        if (!address.equals("") && address.length() < 20) {
            edAddress.requestFocus();
            throw new ValidationException("Địa chỉ phải lớn hơn 20 kí tự");
        }

        if (!address.equals("") && address.length() > 1000) {
            edAddress.requestFocus();
            throw new ValidationException("Địa chỉ phải nhỏ hơn 1000 kí tự");
        }
    }
}

package com.example.thanh.foodink.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.thanh.foodink.Adapter.CartRecyclerAdapter;
import com.example.thanh.foodink.Adapter.OrderRecyclerAdapter;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Helpers.CheckConnection;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Helpers.SessionManager;
import com.example.thanh.foodink.Models.Cart;
import com.example.thanh.foodink.Models.DetailOrder;
import com.example.thanh.foodink.Models.Order;
import com.example.thanh.foodink.Models.Product;
import com.example.thanh.foodink.Models.Size;
import com.example.thanh.foodink.Models.Store;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class OrderHistoryFragment extends Fragment {
    private View rootView;
    private RecyclerView orderRecyclerView;
    private OrderRecyclerAdapter orderRecyclerAdapter;
    private RequestQueue requestQueue;
    private Progresser progress;
    private SessionManager sessionManager;
    private ArrayList<Order> orders;
    private LinearLayout linearRequest;
    private TextView txtMsg, txtTitle;
    private ImageView imgMsg;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.order_history_fragment, container, false);
        addControls();

        return rootView;
    }

    private void addControls() {
        linearRequest = rootView.findViewById(R.id.request_screen);
        orderRecyclerView = rootView.findViewById(R.id.recycler_all_order);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        requestQueue = Volley.newRequestQueue(this.getContext());
        sessionManager = new SessionManager(getContext());
        CheckConnection checkConnection = new CheckConnection(this.getContext());

        if (checkConnection.isNetworkAvailable() && sessionManager.has(User.AUTH)) {
            progress = new Progresser(getContext(), "", "Đang load dữ liệu...");
            showOrders();
        }
    }

    private void showOrders() {
        progress.show();
        orders = new ArrayList<Order>();
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                ApiUrl.API_ORDERS,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.hide();
                        try {
                            JSONArray jsonArray = response.getJSONArray("orders");
                            ArrayList<DetailOrder> detailOrders;
                            Store store;
                            Size size;
                            Product product;
                            orders = new ArrayList<>();
                            JSONObject jsonObject, storeObject, productObject, sizeObject, detailOrderObject;
                            JSONArray detailOrderArray;

                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String address = jsonObject.getString("address");
                                String phone = jsonObject.getString("phone");
                                double shipCost = jsonObject.getDouble("ship_cost");
                                double total = jsonObject.getDouble("total");
                                String status = jsonObject.getString("status");

                                storeObject = jsonObject.getJSONObject("store").getJSONObject("store");
                                JSONArray jsonImageStoreUrls = storeObject.getJSONArray("pictures");
                                ArrayList<String> imageStoreUrls = new ArrayList<String>();

                                for (int j = 0; j < jsonImageStoreUrls.length(); j++) {
                                    JSONArray imageArray = jsonImageStoreUrls.getJSONArray(j);
                                    String imageUrl = (String) imageArray.get(j);
                                    imageStoreUrls.add(imageUrl);
                                }
                                store = new Store(
                                        storeObject.getInt("id"),
                                        storeObject.getString("name"),
                                        imageStoreUrls
                                );

                                detailOrderArray = jsonObject.getJSONArray("detail_orders");
                                detailOrders = new ArrayList<DetailOrder>();
                                for (int j = 0; j < detailOrderArray.length(); j++) {
                                    detailOrderObject = detailOrderArray.getJSONObject(j);
                                    productObject = detailOrderObject.getJSONObject("product");
                                    product = new Product(
                                            productObject.getInt("id"),
                                            productObject.getString("name"),
                                            productObject.getString("description")
                                    );

                                    JSONArray jsonImageUrls = detailOrderObject.getJSONArray("images");
                                    ArrayList<String> imageUrls = new ArrayList<String>();

                                    for (int k = 0; k < jsonImageUrls.length(); k++) {
                                        JSONArray imageArray = jsonImageUrls.getJSONArray(k);
                                        String imageUrl = (String) imageArray.get(k);
                                        imageUrls.add(imageUrl);
                                    }

                                    sizeObject = detailOrderObject.getJSONObject("size");
                                    size = new Size(sizeObject.getInt("id"),
                                            sizeObject.getString("size"),
                                            sizeObject.getDouble("price")
                                    );

                                    DetailOrder detailOrder = new DetailOrder(
                                            detailOrderObject.getInt("id"),
                                            detailOrderObject.getInt("quantity"),
                                            detailOrderObject.getDouble("price"),
                                            detailOrderObject.getDouble("sub_total"),
                                            product,
                                            size,
                                            imageUrls
                                    );
                                    detailOrders.add(detailOrder);
                                }

                                orders.add(new Order(id, address, phone, shipCost, total, status, store, detailOrders));
                            }

                            if (orders.size() != 0) {
                                linearRequest.setVisibility(LinearLayout.INVISIBLE);
                            } else {
                                txtMsg = rootView.findViewById(R.id.txt_msg);
                                txtTitle = rootView.findViewById(R.id.txt_title);
                                imgMsg = rootView.findViewById(R.id.img_msg);
                                txtMsg.setText("Bạn chưa đặt mua sản phẩm nào");
                                txtTitle.setText("Danh sách đặt hàng rỗng!");
                                imgMsg.setImageResource(R.drawable.empty_order);
                                linearRequest.setVisibility(LinearLayout.VISIBLE);
                            }

                            orderRecyclerAdapter = new OrderRecyclerAdapter(orders);
                            orderRecyclerView.setAdapter(orderRecyclerAdapter);
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
}

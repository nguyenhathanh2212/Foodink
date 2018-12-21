package com.example.thanh.foodink.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Activities.ShipperOrderDetailActivity;
import com.example.thanh.foodink.Adapter.ShipperOrderProductAdapter;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Models.Product;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

public class OrderDetailFragment extends Fragment implements View.OnClickListener {
    private View rootView;
    private TextView tvStoreName;
    private TextView tvStoreAddress;
    private TextView tvStorePhone;
    private TextView tvCustomerName;
    private TextView tvCustomerAddress;
    private TextView tvCustomerPhone;
    private ListView lvProducts;
    private TextView tvProductsCost;
    private TextView tvShipCost;
    private TextView tvTotalCost;
    private TextView tvOrderStatus;
    private LinearLayout finishShippingLayout;
    private Button btnFinishShipping;

    private ArrayList<Product> listProducts;
    private ShipperOrderProductAdapter productAdapter;
    private int shipperOrderID;
    private RequestQueue requestQueue;
    private Progresser progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.order_detail_fragment, container, false);

        mappingWidgets();
        getOrders();

        return rootView;
    }

    private void mappingWidgets() {
        tvStoreName = (TextView) rootView.findViewById(R.id.tvStoreName);
        tvStoreAddress = (TextView) rootView.findViewById(R.id.tvStoreAddress);
        tvStorePhone = (TextView) rootView.findViewById(R.id.tvStorePhone);
        tvCustomerName = (TextView) rootView.findViewById(R.id.tvCustomerName);
        tvCustomerAddress = (TextView) rootView.findViewById(R.id.tvCustomerAddress);
        tvCustomerPhone = (TextView) rootView.findViewById(R.id.tvCustomerPhone);
        tvProductsCost = (TextView) rootView.findViewById(R.id.tvProductsCost);
        tvShipCost = (TextView) rootView.findViewById(R.id.tvShipCost);
        tvTotalCost = (TextView) rootView.findViewById(R.id.tvTotalCost);
        lvProducts = (ListView) rootView.findViewById(R.id.lvProducts);
        tvOrderStatus = (TextView) rootView.findViewById(R.id.tvOrderStatus);
        finishShippingLayout = (LinearLayout) rootView.findViewById(R.id.finishShippingLayout);

        requestQueue = Volley.newRequestQueue(getContext());
        Intent intent = getActivity().getIntent();
        shipperOrderID = intent.getIntExtra("ORDER_ID", 0);
        listProducts = new ArrayList<>();
        productAdapter = new ShipperOrderProductAdapter(getActivity(), R.layout.item_product_shipper_order, listProducts);
        lvProducts.setAdapter(productAdapter);
        progress = new Progresser(rootView.getContext(), "", "Đang tải...");
    }

    private void getOrders() {
        progress.show();
        try {
            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    ApiUrl.API_SHIPPER_ORDER_LIST + "/" + shipperOrderID,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress.hide();
                            try {
                                JSONObject shipperOrder = response.getJSONObject("shipper_order");
                                JSONObject order = response.getJSONObject("order");
                                JSONObject store = order.getJSONObject("store");
                                JSONObject customer = order.getJSONObject("customer");
                                JSONArray detailOrders = order.getJSONArray("detail_orders");

                                String orderStatus = shipperOrder.getString("status");
                                showOrderStatus(orderStatus);

                                tvStoreName.setText(store.getString("name"));
                                tvStoreAddress.setText(store.getString("address"));
                                tvStorePhone.setText(store.getString("phone"));

                                tvCustomerName.setText(customer.getString("name"));
                                tvCustomerAddress.setText(customer.getString("address"));
                                tvCustomerPhone.setText(customer.getString("phone"));

                                float shipCost = (float) order.getDouble("ship_cost");
                                float totalCost = (float) order.getDouble("total");
                                float productsCost = totalCost - shipCost;

                                DecimalFormat numberFormat = new DecimalFormat("##,###,### đ");
                                tvShipCost.setText(numberFormat.format(shipCost));
                                tvTotalCost.setText(numberFormat.format(totalCost));
                                tvProductsCost.setText(numberFormat.format(productsCost));

                                for (int i = 0; i < detailOrders.length(); i++) {
                                    JSONObject detailOrder = detailOrders.getJSONObject(i);
                                    JSONObject product = detailOrder.getJSONObject("product");
                                    JSONObject size = detailOrder.getJSONObject("size");
                                    JSONArray images = detailOrder.getJSONArray("images");

                                    int quantity = detailOrder.getInt("quantity");
                                    float price = (float) detailOrder.getDouble("price");
                                    String productName = product.getString("name");
                                    String productType = product.getString("product_type");

                                    String sizeName = size.getString("size");

                                    String image = "";

                                    Product productObj = new Product(productName, sizeName, image, quantity, price, productType);
                                    listProducts.add(productObj);
                                }

                                productAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Log.d("ApiError", e.toString());
                                e.printStackTrace();
                                Toast.makeText(rootView.getContext(), "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.hide();
                            Toast.makeText(rootView.getContext(), "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showOrderStatus(String status) {
        finishShippingLayout.removeAllViews();

        if (!status.equals("shipping")) {
            tvOrderStatus.setText("Hoàn thành");
            tvOrderStatus.setBackgroundResource(R.drawable.border_radius_purple_button);

            return;
        }

        tvOrderStatus.setText("Đang vận chuyển");
        tvOrderStatus.setBackgroundResource(R.drawable.border_radius_green_button);
        View finishShippingView = getLayoutInflater().inflate(R.layout.finish_shipping_fragment, null);
        finishShippingLayout.addView(finishShippingView);

        btnFinishShipping = (Button) rootView.findViewById(R.id.btnFinishShipping);
        btnFinishShipping.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFinishShipping:
                finishShipping();
                break;
            default:
                break;
        }
    }

    private void finishShipping() {
        progress.show();
        try {
            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.PATCH,
                    ApiUrl.API_SHIPPER_ORDER_LIST + "/" + shipperOrderID,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress.hide();
                            try {
                                boolean isSuccess = response.getBoolean("success");

                                if (!isSuccess) {
                                    throw new Exception();
                                }

                                showOrderStatus("done");
                            } catch (Exception e) {
                                Log.d("ApiError", e.toString());
                                e.printStackTrace();
                                Toast.makeText(rootView.getContext(), "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.hide();
                            Toast.makeText(rootView.getContext(), "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
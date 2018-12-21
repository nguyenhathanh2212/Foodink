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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Adapter.ShipperOrderListRecyclerAdapter;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Models.ShipperOrder;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Vinh Nguyen on 11/1/2018.
 */

public class ShipperOrderListFragment extends Fragment {
    private View rootView;
    private RecyclerView recyclerShipperOrderList;

    private ArrayList<ShipperOrder> listShipperOrder;
    private ShipperOrderListRecyclerAdapter adapter;

    private RequestQueue requestQueue;
    private Progresser progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.shipper_order_list_fragment, container, false);

        return rootView;
    }

    private void mappingWidgets() {
        requestQueue = Volley.newRequestQueue(getContext());
        recyclerShipperOrderList = (RecyclerView) rootView.findViewById(R.id.recyclerShipperOrderList);
        listShipperOrder = new ArrayList<>();
        adapter = new ShipperOrderListRecyclerAdapter(listShipperOrder);
        recyclerShipperOrderList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerShipperOrderList.setAdapter(adapter);
        progress = new Progresser(getContext(), "", "Đang tải...");
    }

    private void getShipperOrderList() {
        progress.show();

        try {
            JSONObject params = new JSONObject();
            params.put("get_all", 1);

            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    ApiUrl.API_SHIPPER_ORDER_LIST,
                    params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress.hide();

                            try {
                                JSONArray shipperOrders = response.getJSONArray("shipper_orders");

                                for (int i = 0; i < shipperOrders.length(); i++) {
                                    JSONObject shipperOrder = (JSONObject) shipperOrders.get(i);
                                    JSONObject order = (JSONObject) shipperOrder.get("order");
                                    int orderID = order.getInt("id");
                                    JSONObject store = (JSONObject) order.get("store");
                                    JSONObject customer = (JSONObject) order.get("customer");

                                    int shipperOrderID = shipperOrder.getInt("id");
                                    String orderStatus = shipperOrder.getString("status");
                                    String storeName = store.getString("name");
                                    String customerName = customer.getString("name");
                                    float shipCost = (float) order.getDouble("ship_cost");

                                    ShipperOrder shipperOrderObj = new ShipperOrder(orderID, shipperOrderID, storeName, customerName, shipCost, orderStatus, "");
                                    listShipperOrder.add(shipperOrderObj);
                                }

                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Log.d("ApiError", e.toString());
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && User.checkUserAuth(getContext())) {
            mappingWidgets();
            getShipperOrderList();
        }
    }
}

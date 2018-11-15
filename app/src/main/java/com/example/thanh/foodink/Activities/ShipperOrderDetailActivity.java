package com.example.thanh.foodink.Activities;

import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.thanh.foodink.Fragment.OrderDetailFragment;
import com.example.thanh.foodink.Fragment.ShipperOrderReceivedRejectFragment;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;

import org.json.JSONObject;

import java.util.Map;

public class ShipperOrderDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String ACCEPT_ACTION = "Accept";
    public static final String REJECT_ACTION = "Reject";
    public static final String SHOW_ACTION = "Show";
    public static final String SHOW_RECEIVED_ORDER_ACTION = "Show Received Order";

    private TextView tvStoreNameHeader;
    private Intent intent;
    private RequestQueue requestQueue;
    private Progresser progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_order_detail);

        if (!User.checkUserAuth(this)) {
            finish();
        }

        mappingWidgets();
        hideNotification();
        process();
    }

    private void mappingWidgets() {
        intent = getIntent();
        progress = new Progresser(this, "", "Đang tải...");
        requestQueue = Volley.newRequestQueue(this);
        tvStoreNameHeader = (TextView) findViewById(R.id.tvStoreNameHeader);
        String storeName = intent.getStringExtra("STORE_NAME");
        tvStoreNameHeader.setText(storeName);
    }

    private void process() {
        String action = intent.getAction();

        if (action == null) {
            return;
        }

        switch (action) {
            case ACCEPT_ACTION:
                acceptOrder();
                break;
            case SHOW_ACTION:
                showOrder();
                break;
            case REJECT_ACTION:
                finish();
                break;
            case SHOW_RECEIVED_ORDER_ACTION:
                showReceivedOrder();
                break;
            default:
                finish();
                break;
        }
    }

    private void hideNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(getIntent().getIntExtra("NOTIFICATION_ID", -1));
    }

    private void showReceivedOrder() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft_rep = fm.beginTransaction();
        ft_rep.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        ft_rep.replace(R.id.orderInfoFragment, new OrderDetailFragment());
        ft_rep.commit();
    }

    private void acceptOrder() {
        progress.show();
        try {
            int orderID = intent.getIntExtra("ORDER_ID", 0);
            JSONObject params = new JSONObject();
            params.put("order_id", orderID);

            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiUrl.API_SHIPPER_ORDER_LIST,
                    params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress.hide();
                            try {
                                boolean isSuccess = response.getBoolean("success");

                                if (isSuccess) {
                                    Toast.makeText(getApplicationContext(), "Chấp nhận đơn hàng thành công", Toast.LENGTH_LONG);
                                    JSONObject shipperOrder = response.getJSONObject("shipper_order");
                                    getIntent().putExtra("SHIPPER_ORDER_ID", shipperOrder.getInt("id"));
                                    showReceivedOrder();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Thất bại! Đã có người nhận đơn hàng này", Toast.LENGTH_LONG);
                                    FragmentManager fm = getSupportFragmentManager();
                                    FragmentTransaction ft_rep = fm.beginTransaction();
                                    ft_rep.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                                    ShipperOrderReceivedRejectFragment fragment = new ShipperOrderReceivedRejectFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("Message", "Đơn hàng đã có người nhận");
                                    fragment.setArguments(bundle);

                                    ft_rep.replace(R.id.orderInfoFragment, fragment);
                                    ft_rep.commit();
                                }
                            } catch (Exception e) {
                                Log.d("ApiError", e.toString());
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.hide();
                            Toast.makeText(getApplicationContext(), "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                            Log.d("ApiError", error.toString());
                            error.printStackTrace();
                        }
                    }
            ) {
                public Map<String, String> getHeaders() {
                    Map<String, String> mHeaders = new ArrayMap<String, String>();
                    mHeaders.put("Authorization", User.getUserAuth(getApplicationContext()).getAuthToken());

                    return mHeaders;
                }
            };

            requestQueue.add(objectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }

    private void showOrder() {
        progress.show();
        try {
            int orderID = intent.getIntExtra("ORDER_ID", 0);

            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    ApiUrl.API_SHIPPER_ORDER_LIST + "/" + orderID + "/exists",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress.hide();
                            try {
                                boolean isSuccess = response.getBoolean("success");
                                boolean isExists = response.getBoolean("exists");

                                if (isSuccess) {
                                    if (!isExists) {
                                        showOrderAction();
                                    } else {
                                        FragmentManager fm = getSupportFragmentManager();
                                        FragmentTransaction ft_rep = fm.beginTransaction();
                                        ft_rep.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                                        ShipperOrderReceivedRejectFragment fragment = new ShipperOrderReceivedRejectFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("Message", "Đơn hàng đã có người nhận");
                                        fragment.setArguments(bundle);

                                        ft_rep.replace(R.id.orderInfoFragment, fragment);
                                        ft_rep.commit();
                                    }
                                } else {
                                    throw new Exception();
                                }
                            } catch (Exception e) {
                                Log.d("ApiError", e.toString());
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.hide();
                            Toast.makeText(getApplicationContext(), "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                            Log.d("ApiError", error.toString());
                            error.printStackTrace();
                        }
                    }
            ) {
                public Map<String, String> getHeaders() {
                    Map<String, String> mHeaders = new ArrayMap<String, String>();
                    mHeaders.put("Authorization", User.getUserAuth(getApplicationContext()).getAuthToken());

                    return mHeaders;
                }
            };

            requestQueue.add(objectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }

    private void showOrderAction() {
        LinearLayout orderInfoFragment = findViewById(R.id.orderInfoFragment);
        orderInfoFragment.removeAllViews();

        View acceptRejectView = getLayoutInflater().inflate(R.layout.accept_reject_order_fragment, null);
        orderInfoFragment.addView(acceptRejectView);

        Button btnAccept = (Button) findViewById(R.id.btnAccept);
        Button btnReject = (Button) findViewById(R.id.btnReject);

        btnAccept.setOnClickListener(this);
        btnReject.setOnClickListener(this);
    }

    private void rejectOrder() {
        LinearLayout orderInfoFragment = findViewById(R.id.orderInfoFragment);
        orderInfoFragment.removeAllViews();

        Toast.makeText(this, "Từ chối đơn hàng thành công", Toast.LENGTH_SHORT);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft_rep = fm.beginTransaction();
        ft_rep.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        ShipperOrderReceivedRejectFragment fragment = new ShipperOrderReceivedRejectFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Message", "Đã từ chối đơn hàng");
        fragment.setArguments(bundle);

        ft_rep.replace(R.id.orderInfoFragment, fragment);
        ft_rep.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAccept:
                acceptOrder();
                break;
            case R.id.btnReject:
                rejectOrder();
                break;
        }
    }
}

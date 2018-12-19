package com.example.thanh.foodink.Activities;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.CustomMap.WorkaroundMapFragment;
import com.example.thanh.foodink.Fragment.OrderDetailFragment;
import com.example.thanh.foodink.Fragment.ShipperOrderReceivedRejectFragment;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Helpers.SessionManager;
import com.example.thanh.foodink.Models.Notification;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShipperOrderDetailActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    public static final String ACCEPT_ACTION = "Accept";
    public static final String REJECT_ACTION = "Reject";
    public static final String SHOW_ACTION = "Show";
    public static final String SHOW_RECEIVED_ORDER_ACTION = "Show Received Order";

    private TextView tvStoreNameHeader;
    private LinearLayout orderInfoFragment;
    private Intent intent;
    private RequestQueue requestQueue;
    private Progresser progress;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_order_detail);

        if (!User.checkUserAuth(this)) {
            finish();
        }

        final ScrollView scrollView = findViewById(R.id.scroll_view);
        WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
        mapFragment.getMapAsync(this);

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
        orderInfoFragment = (LinearLayout) findViewById(R.id.orderInfoFragment);
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
                removeNotification();
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
        orderInfoFragment.removeAllViews();
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
                                    orderInfoFragment.removeAllViews();

                                    ShipperOrderReceivedRejectFragment fragment = new ShipperOrderReceivedRejectFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("Message", "Đơn hàng đã có người nhận");
                                    fragment.setArguments(bundle);

                                    ft_rep.replace(R.id.orderInfoFragment, fragment);
                                    ft_rep.commit();
                                }

                                removeNotification();
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
                                        removeNotification();
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

    private void removeNotification()
    {
        int orderID = intent.getIntExtra("ORDER_ID", 0);
        SessionManager sessionManager = SessionManager.getInstant(this);
        String notificationsJson = sessionManager.get("NOTIFICATION_LIST");
        ArrayList<Notification> nofiticationList = new ArrayList<>();
        Gson gson = new Gson();

        if (!notificationsJson.equals("")) {
            Type type = new TypeToken<ArrayList<Notification>>() {}.getType();
            nofiticationList = gson.fromJson(notificationsJson, type);
        }

        for (int i = 0; i < nofiticationList.size(); i++) {
            if (nofiticationList.get(i).getOrderID() == orderID) {
                nofiticationList.remove(i);
            }
        }

        sessionManager.set("NOTIFICATION_LIST", gson.toJson(nofiticationList));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        progress.show();
        try {
            int orderID = intent.getIntExtra("SHIPPER_ORDER_ID", 0);
            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    ApiUrl.API_ORDER + "/" + orderID,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress.hide();
                            try {
                                boolean isSuccess = response.getBoolean("success");
                                if (isSuccess) {
                                    double longitudeStore = response.getJSONObject("store").getDouble("longitude");
                                    double latitudeStore = response.getJSONObject("store").getDouble("latitude");
                                    String addressCustomer = response.getJSONObject("order").getString("address");
                                    LatLng locationCustomer = getLocationFromAddress(getBaseContext(), addressCustomer);
                                    LatLng locationStore = new LatLng(latitudeStore, longitudeStore);
                                    UiSettings uiSettings = mMap.getUiSettings();
                                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    mMap.setMyLocationEnabled(true);
                                    uiSettings.setZoomGesturesEnabled(true);
                                    uiSettings.setZoomControlsEnabled(true);
                                    uiSettings.setCompassEnabled(true);
                                    uiSettings.setMapToolbarEnabled(true);
                                    mMap.addMarker(new MarkerOptions()
                                            .position(locationStore)
                                            .title("Cửa hàng")
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                    mMap.addMarker(new MarkerOptions()
                                            .position(locationCustomer)
                                            .title("Khách hàng")
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                    LatLng moveCamera = new LatLng((latitudeStore + locationCustomer.latitude) / 2,
                                            (longitudeStore + locationCustomer.longitude) / 2);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(moveCamera, 11.2f));
                                }
                            } catch (Exception e) {
                                Log.d("ApiError", e.toString());
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.hide();
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

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return p1;
    }
}

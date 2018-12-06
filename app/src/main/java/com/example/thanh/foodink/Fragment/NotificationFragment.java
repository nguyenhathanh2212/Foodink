package com.example.thanh.foodink.Fragment;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Activities.MainActivity;
import com.example.thanh.foodink.Adapter.NotificationTabAdapter;
import com.example.thanh.foodink.Adapter.TabHomeAdapter;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Helpers.CheckConnection;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Models.Notification;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;
import com.example.thanh.foodink.Services.UpdateLocationService;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class NotificationFragment extends Fragment implements SwitchCompat.OnCheckedChangeListener {
    private View rootView;
    private TabLayout notificationTabLayout;
    private ViewPager notificationViewPager;
    private NotificationTabAdapter adapter;
    private SwitchCompat swChangeShipperStatus;
    private ImageView imgShipperStatus;
    private LinearLayout linearRequest;

    private RequestQueue requestQueue;
    private Progresser progress;
    private String currentStatus;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CheckConnection checkConnection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.notification_fragment, container, false);
        mappingWidgets();

        return rootView;
    }

    private void mappingWidgets() {
        notificationTabLayout = (TabLayout) rootView.findViewById(R.id.notification_tab_layout);
        notificationViewPager = (ViewPager) rootView.findViewById(R.id.notification_viewpager);
        adapter = new NotificationTabAdapter(MainActivity.getFragManager());
        notificationViewPager.setAdapter(adapter);
        notificationTabLayout.setupWithViewPager(notificationViewPager);
        swChangeShipperStatus = (SwitchCompat) rootView.findViewById(R.id.swChangeShipperStatus);
        requestQueue = Volley.newRequestQueue(getContext());
        progress = new Progresser(getContext(), "", "Đang tải...");
        imgShipperStatus = (ImageView) rootView.findViewById(R.id.imgShipperStatus);
        currentStatus = "";
        linearRequest = (LinearLayout) rootView.findViewById(R.id.request_screen);
        swipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);
        checkConnection = new CheckConnection(this.getContext());

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (checkConnection.isNetworkAvailable() && User.checkUserAuth(getContext())) {
                            loadData();
                        } else {
                            Toast.makeText(getContext(), "Vui lòng bật kết nối mạng!", Toast.LENGTH_SHORT).show();
                        }

                        swipeRefreshLayout.setRefreshing(false);
                    }

                }
        );
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            if (User.checkUserAuth(getContext()) && User.getUserAuth(getContext()).getShipperId() > 0) {
                linearRequest.setVisibility(LinearLayout.INVISIBLE);
                loadData();
            } else {
                TextView tvTitle = (TextView) rootView.findViewById(R.id.txt_title);
                TextView tvMsg = (TextView) rootView.findViewById(R.id.txt_msg);
                tvTitle.setText("Không thể tải dữ liệu");
                tvMsg.setText("Chức năng này chỉ dành riêng cho Shipper");
                linearRequest.setVisibility(LinearLayout.VISIBLE);
            }
        }
    }

    private void loadData() {
        swChangeShipperStatus.setOnCheckedChangeListener(null);
        adapter = new NotificationTabAdapter(MainActivity.getFragManager());
        notificationViewPager.setAdapter(adapter);
        notificationTabLayout.setupWithViewPager(notificationViewPager);
        showShipperStatus();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.swChangeShipperStatus) {
            if (isChecked) {
                turnOnStatus();
            } else {
                turnOffStatus();
            }
        }
    }

    private void showShipperStatus() {
        progress.show();
        try {
            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    ApiUrl.API_SHIPPER + "/" + User.getUserAuth(getContext()).getShipperId(),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress.hide();
                            try {
                                boolean isSuccess = response.getBoolean("success");
                                currentStatus = response.getString("status");

                                if (isSuccess) {
                                    loadStatusToView();
                                }

                                swChangeShipperStatus.setOnCheckedChangeListener(NotificationFragment.this);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStatusToView() {
        if (currentStatus.equals("online")) {
            imgShipperStatus.setImageResource(R.drawable.shipper_online);
            swChangeShipperStatus.setChecked(true);
        } else {
            imgShipperStatus.setImageResource(R.drawable.shipper_offline);
            swChangeShipperStatus.setChecked(false);
        }
    }

    private void turnOnStatus() {
        progress.show();
        try {
            JSONObject params = new JSONObject();
            params.put("device_token", FirebaseInstanceId.getInstance().getToken());

            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiUrl.API_CHANGE_SHIPPER_STATUS,
                    params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress.hide();
                            try {
                                boolean isSuccess = response.getBoolean("success");

                                if (isSuccess) {
                                    imgShipperStatus.setImageResource(R.drawable.shipper_online);
                                    getActivity().startService(new Intent(getActivity(), UpdateLocationService.class));
                                    Toast.makeText(getContext(), "Thay đổi trạng thái thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Thay đổi trạng thái thất bại", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                loadStatusToView();
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
                            loadStatusToView();
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

    private void turnOffStatus() {
        progress.show();

        try {
            String deviceToken = FirebaseInstanceId.getInstance().getToken();

            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.DELETE,
                    ApiUrl.API_CHANGE_SHIPPER_STATUS + "/" + deviceToken,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress.hide();
                            try {
                                boolean isSuccess = response.getBoolean("success");

                                if (isSuccess) {
                                    imgShipperStatus.setImageResource(R.drawable.shipper_offline);
                                    getActivity().stopService(new Intent(getActivity(), UpdateLocationService.class));
                                    Toast.makeText(getContext(), "Thay đổi trạng thái thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Thay đổi trạng thái thất bại", Toast.LENGTH_SHORT).show();
                                }
                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                loadStatusToView();
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
                            loadStatusToView();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

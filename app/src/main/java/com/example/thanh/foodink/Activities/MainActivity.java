package com.example.thanh.foodink.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Adapter.TabHomeAdapter;
import com.example.thanh.foodink.Broadcast.ManagerConnect;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Dialog.CheckConnectionDialog;
import com.example.thanh.foodink.Helpers.CheckConnection;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;
import com.example.thanh.foodink.Services.UpdateLocationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int[] imageResId = {
            R.drawable.home,
            R.drawable.order,
            R.drawable.notification,
            R.drawable.profile
    };
    private int currentFragment;
    private static FragmentManager fragmentManager;
    private static Context mainContext;
    private ManagerConnect managerConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainContext = MainActivity.this;
//        managerConnect = new ManagerConnect();
//        final IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
//        registerReceiver(managerConnect, filter);

        if (User.checkUserAuth(this) && User.getUserAuth(this).checkTokenExpire()) {
            refreshLogin();
        } else {
            addControls();
        }

    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        unregisterReceiver(managerConnect);
//    }

    private void addControls() {
        fragmentManager = getSupportFragmentManager();
        viewPager = findViewById(R.id.home_viewpager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new TabHomeAdapter(getSupportFragmentManager(), getBaseContext()));
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        this.setTabIcon();
        CheckConnection checkConnection = new CheckConnection(getBaseContext());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        if (checkConnection.isNetworkAvailable() == false) {
            CheckConnectionDialog checkConnectionDialog = new CheckConnectionDialog();
            checkConnectionDialog.show(this.getSupportFragmentManager(), "");
        }

        Intent intent = new Intent(this, UpdateLocationService.class);
        startService(intent);
    }

    private void setTabIcon() {
        currentFragment = viewPager.getCurrentItem();
        tabLayout.getTabAt(0).setIcon(imageResId[0]);
        tabLayout.getTabAt(1).setIcon(imageResId[1]);
        tabLayout.getTabAt(2).setIcon(imageResId[2]);
        tabLayout.getTabAt(3).setIcon(imageResId[3]);
    }

    public static FragmentManager getFragManager() {
        return fragmentManager;
    }
    public static Context getMainContext() {
        return mainContext;
    }

    public void refreshLogin() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject params = new JSONObject();
            params.put("refresh_token", User.getUserAuth(this).getRefreshToken());

            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.PATCH,
                    ApiUrl.API_LOGIN_REFRESH,
                    params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject authInfo = response.getJSONObject("auth_token");
                                String authToken = authInfo.getString("token");
                                String refreshToken = authInfo.getString("refresh_token");
                                String expiredAt = authInfo.getString("expired_at");

                                User user = User.getUserAuth(getApplicationContext());
                                user.setAuthToken(authToken);
                                user.setRefreshToken(refreshToken);
                                user.setExpireAt(expiredAt);

                                User.setUserAuth(getApplicationContext(), user);
                                addControls();
                            } catch (Exception e) {
                                Log.d("ApiError", e.toString());
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
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
        }
    }
}

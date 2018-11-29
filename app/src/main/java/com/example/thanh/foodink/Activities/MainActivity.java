package com.example.thanh.foodink.Activities;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Dialog.CheckConnectionDialog;
import com.example.thanh.foodink.Helpers.CheckConnection;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

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

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<String>();
    private ArrayList<String> permissions = new ArrayList<String>();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        setContentView(R.layout.activity_main);

        if (User.checkUserAuth(this) && User.getUserAuth(this).checkTokenExpire()) {
            refreshLogin();
        } else {
            addControls();
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getBaseContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

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
                setIconTab(i);
            }

            @Override
            public void onPageSelected(int i) {
                setIconTab(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        if (checkConnection.isNetworkAvailable() == false) {
            CheckConnectionDialog checkConnectionDialog = new CheckConnectionDialog();
            checkConnectionDialog.show(this.getSupportFragmentManager(), "");
        }
    }

    private void setTabIcon() {
        currentFragment = viewPager.getCurrentItem();
        setIconTab(currentFragment);
    }

    public void setIconTab(int currentTab) {
        tabLayout.getTabAt(0).setIcon(imageResId[0]);
        tabLayout.getTabAt(1).setIcon(imageResId[1]);
        tabLayout.getTabAt(2).setIcon(imageResId[2]);
        tabLayout.getTabAt(3).setIcon(imageResId[3]);

        switch (currentTab) {
            case 0: {
                tabLayout.getTabAt(0).setIcon(R.drawable.home_active);
                break;
            }
            case 1: {
                tabLayout.getTabAt(1).setIcon(R.drawable.order_active);
                break;
            }
            case 2: {
                tabLayout.getTabAt(2).setIcon(R.drawable.notification_active);
                break;
            }
            case 3: {
                tabLayout.getTabAt(3).setIcon(R.drawable.profile_active);
                break;
            }
        }
    }

    public static FragmentManager getFragManager() {
        return fragmentManager;
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

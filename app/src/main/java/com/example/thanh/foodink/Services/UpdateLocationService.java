package com.example.thanh.foodink.Services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.thanh.foodink.Activities.MainActivity;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static java.security.AccessController.getContext;

public class UpdateLocationService extends Service implements LocationListener {

    private LocationManager locationManager;
    private Location location;
    boolean checkGPS = false;
    boolean checkNetwork = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;//0;
    private static final long MIN_TIME_BW_UPDATES = 1; //1000 * 60 * 1;
    private RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("AAAAAAAAAA", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("AAAAAAAAAA", "onStartCommand");
        locationManager = (LocationManager) MainActivity.getMainContext().getSystemService(LOCATION_SERVICE);
        checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!checkNetwork && !checkGPS) {
            Toast.makeText(MainActivity.getMainContext(), "Vui lòng bật mạng hoặc GPS!", Toast.LENGTH_SHORT).show();
        } else {
            if (checkGPS) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    // return ;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }

            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    updateLocation(location);
                }
            }
        }

        return START_STICKY;
    }

    private void updateLocation(Location location) {
        try {
            JSONObject params = new JSONObject();
            params.put("latitude", location.getLatitude());
            params.put("longitude", location.getLongitude());

            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.PATCH,
                    ApiUrl.API_SHIPPER,
                    params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                            } catch (Exception e) {
                                Log.d("ApiError", e.toString());
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.getMainContext(), "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                            Log.d("ApiError", error.toString());
                            error.printStackTrace();
                        }
                    }
            ) {
                public Map<String, String> getHeaders() {
                    Map<String, String> mHeaders = new ArrayMap<String, String>();
                    mHeaders.put("Authorization", User.getUserAuth(MainActivity.getMainContext()).getAuthToken());

                    return mHeaders;
                }
            };

            requestQueue.add(objectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

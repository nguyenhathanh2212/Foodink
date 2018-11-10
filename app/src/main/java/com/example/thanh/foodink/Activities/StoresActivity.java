package com.example.thanh.foodink.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Adapter.StoreRecyclerAdapter;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Helpers.CheckConnection;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Models.Store;
import com.example.thanh.foodink.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoresActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StoreRecyclerAdapter storeRecyclerAdapter;
    private RequestQueue requestQueue;
    private JsonObjectRequest objectRequest;
    private ApiUrl apiUrl;
    private Progresser progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CheckConnection checkConnection = new CheckConnection(getBaseContext());

        if (checkConnection.isNetworkAvailable()) {
            setContentView(R.layout.activity_stores);

            addControls();
        } else {
            setContentView(R.layout.login_request);
        }
    }

    private void addControls() {
        recyclerView = findViewById(R.id.recycler_all_stores);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        requestQueue = Volley.newRequestQueue(this);
        progress = new Progresser(this, "", "Đang load dữ liệu...");
        showStores();

    }


    private void showStores() {
        progress.show();
        objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl.API_STORES + "?get_all=1",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.hide();
                        try {
                            JSONArray jsonArray = response.getJSONArray("stores");

                            ArrayList<Store> listData = new ArrayList<Store>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String name = jsonObject.getString("name");
                                String description = jsonObject.getString("description");
                                String locaion = jsonObject.getString("address");
                                JSONArray jsonImageUrls = jsonObject.getJSONArray("pictures");
                                ArrayList<String> imageUrls = new ArrayList<String>();

                                for (int j = 0; j < jsonImageUrls.length(); j++) {
                                    JSONArray imageArray = jsonImageUrls.getJSONArray(j);
                                    String imageUrl = (String) imageArray.get(j);
                                    imageUrls.add(imageUrl);
                                }

                                listData.add(new Store(id, name, description, locaion, imageUrls));
                            }

                            storeRecyclerAdapter = new StoreRecyclerAdapter(listData, R.layout.item_all_stores, true);
                            recyclerView.setAdapter(storeRecyclerAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
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
        );
        requestQueue.add(objectRequest);
    }
}

package com.example.thanh.foodink.Fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Activities.MainActivity;
import com.example.thanh.foodink.Activities.StoresActivity;
import com.example.thanh.foodink.Adapter.CategoryRecyclerAdapter;
import com.example.thanh.foodink.Adapter.StoreRecyclerAdapter;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Dialog.CheckConnectionDialog;
import com.example.thanh.foodink.Helpers.CheckConnection;
import com.example.thanh.foodink.Helpers.FontManager;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Models.Category;
import com.example.thanh.foodink.Models.Store;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private View rootView;
    private RecyclerView categoryRecyclerView;
    private RecyclerView storeRecyclerView;
    private CategoryRecyclerAdapter categoryAdapter;
    private StoreRecyclerAdapter storeAdapter;
    private ViewFlipper viewFlipper;
    private RequestQueue requestQueue;
    private JsonObjectRequest objectRequest;
    private FragmentManager fragmentManager;
    private ApiUrl apiUrl;
    private Button btnViewAllStores;
    private Progresser progress;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Store> listStores;
    private ArrayList<Category> listCategories;
    private CheckConnection checkConnection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_fragment, container, false);
        addControls();
        addEvents();

        return rootView;
    }

    private void addEvents() {
        btnViewAllStores.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (checkConnection.isNetworkAvailable()) {
                            listStores.clear();
                            listCategories.clear();
                            showCategories();
                            showStores();
                        } else {
                            Toast.makeText(getContext(), "Vui lòng bật kết nối mạng!", Toast.LENGTH_SHORT).show();
                        }

                        swipeRefreshLayout.setRefreshing(false);
                    }

                }
        );
    }

    private void addControls() {
        Typeface iconFont = FontManager.getTypeface(getContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(rootView.findViewById(R.id.homeViewLayout), iconFont);
        viewFlipper = rootView.findViewById(R.id.view_flipper);
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setInAnimation(this.getContext(), R.anim.fade_in);
        viewFlipper.setOutAnimation(this.getContext(), R.anim.fade_out);
        viewFlipper.setAutoStart(true);

        categoryRecyclerView = rootView.findViewById(R.id.recycler_category);
        storeRecyclerView = rootView.findViewById(R.id.recycler_store);
        btnViewAllStores = rootView.findViewById(R.id.btn_more_store);
        categoryRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(linearLayoutManager);
        storeRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        requestQueue = Volley.newRequestQueue(this.getContext());
        swipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);

        listStores = new ArrayList<Store>();
        storeAdapter = new StoreRecyclerAdapter(listStores, R.layout.item_store);
        storeRecyclerView.setAdapter(storeAdapter);
        listCategories = new ArrayList<Category>();
        categoryAdapter = new CategoryRecyclerAdapter(listCategories);
        categoryRecyclerView.setAdapter(categoryAdapter);
        progress = new Progresser(getContext(), "", "Đang load dữ liệu...");

        checkConnection = new CheckConnection(this.getContext());

        if (checkConnection.isNetworkAvailable()) {
            showCategories();
            showStores();
        }
    }

    private void showStores() {
        progress.show();
        objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl.API_STORES,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progress.hide();
                            JSONArray jsonArray = response.getJSONArray("stores");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String name = jsonObject.getString("name");
                                String description = jsonObject.getString("description");
                                String locaion = jsonObject.getString("address");
                                JSONArray jsonImageUrls = jsonObject.getJSONArray("pictures");
                                ArrayList<String> imageUrls = new ArrayList<String>();

                                for (int j = 0; j < jsonImageUrls.length(); j++) {
                                    String imageUrl = (String) jsonImageUrls.get(j);
                                    imageUrls.add(imageUrl);
                                }

                                listStores.add(new Store(id, name, description, locaion, imageUrls));
                            }
                            storeAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
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
        );
        requestQueue.add(objectRequest);
    }

    private void showCategories() {
        progress.show();
        objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl.API_CATEGORIES,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.hide();
                        try {
                            JSONArray jsonArray = response.getJSONArray("categories");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String name = jsonObject.getString("name");
                                String imageUrl = jsonObject.getString("picture");
                                listCategories.add(new Category(id, name, imageUrl));
                            }
                            categoryAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
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
        );
        requestQueue.add(objectRequest);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnViewAllStores.getId()) {
            Intent intent = new Intent(this.getContext(), StoresActivity.class);
            startActivity(intent);
        }
    }
}

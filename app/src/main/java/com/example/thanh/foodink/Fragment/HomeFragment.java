package com.example.thanh.foodink.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Adapter.CategoryRecyclerAdapter;
import com.example.thanh.foodink.Adapter.StoreRecyclerAdapter;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Helpers.FontManager;
import com.example.thanh.foodink.Models.Category;
import com.example.thanh.foodink.Models.Store;
import com.example.thanh.foodink.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_fragment, container, false);
        addControls();

        return rootView;
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
        categoryRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(linearLayoutManager);
        storeRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        requestQueue = Volley.newRequestQueue(this.getContext());

        showCategories();

        showStores();
    }

    private void showStores() {
        objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl.API_STORES,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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

                            storeAdapter = new StoreRecyclerAdapter(listData);
                            storeRecyclerView.setAdapter(storeAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("RES", error.toString());
                    }
                }
        );
        requestQueue.add(objectRequest);
    }

    private void showCategories() {
        objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl.API_CATEGORIES,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("categories");

                            ArrayList<Category> listData = new ArrayList<Category>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String name = jsonObject.getString("name");
                                String imageUrl = jsonObject.getString("picture");
                                listData.add(new Category(id, name, imageUrl));
                            }

                            categoryAdapter = new CategoryRecyclerAdapter(listData);
                            categoryRecyclerView.setAdapter(categoryAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("RESs", error.toString());
                    }
                }
        );
        requestQueue.add(objectRequest);
    }
}

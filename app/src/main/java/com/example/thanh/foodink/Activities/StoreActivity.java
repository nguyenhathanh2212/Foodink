package com.example.thanh.foodink.Activities;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Adapter.ProductRecycleAdapter;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Helpers.FontManager;
import com.example.thanh.foodink.Models.Product;
import com.example.thanh.foodink.Models.Size;
import com.example.thanh.foodink.Models.Store;
import com.example.thanh.foodink.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoreActivity extends AppCompatActivity {
    private TextView txtName, txtAddress, txtDescription;
    private ImageView imgStatus;
    private ViewFlipper viewFlipper;
    private Animation in, out;
    private TabHost tabHostStore;
    private RecyclerView foodRecyclerView, drinkRecyclerView;
    private ProductRecycleAdapter foodRecycleAdapter, drinkRecycleAdapter;
    private RequestQueue requestQueue;
    private JsonObjectRequest objectRequest;
    private ApiUrl apiUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        addControls();
    }

    private void addControls() {
        Bundle bundle = getIntent().getExtras();
        Store store = (Store) bundle.getSerializable("store");
        txtName = findViewById(R.id.txt_name_store);
        txtAddress = findViewById(R.id.txt_address_store);
        txtDescription = findViewById(R.id.txt_desciption_store);
        viewFlipper = findViewById(R.id.img_background_store);
        imgStatus = findViewById(R.id.img_status_store);
        txtName.setText(store.getName());
        txtAddress.setText(store.getLocation());
        txtDescription.setText(store.getDescription());
        tabHostStore = findViewById(R.id.tab_host_store);
        foodRecyclerView = findViewById(R.id.recycler_food_store);
        drinkRecyclerView = findViewById(R.id.recycler_drink_store);
        LinearLayoutManager foodLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        foodRecyclerView.setLayoutManager(foodLayoutManager);
        LinearLayoutManager drinkLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        drinkRecyclerView.setLayoutManager(drinkLayoutManager);
        requestQueue = Volley.newRequestQueue(this);

        setTabHost();
        getDrinkProduct(store.getId());
        getFoodProduct(store.getId());

        ArrayList<String> imageUrls = store.getImages();
        setFlipper(imageUrls);
    }

    private void getDrinkProduct(int id) {
        objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl.API_STORES + "/" + id + "?get_all=1",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONObject("drink").getJSONArray("products");

                            ArrayList<Product> listData = new ArrayList<Product>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String name = jsonObject.getString("name");
                                String description = jsonObject.getString("description");
                                double rate = jsonObject.getDouble("avg_rate_score");

                                JSONArray sizeArray = jsonObject.getJSONArray("sizes");
                                ArrayList<Size> sizes = new ArrayList<Size>();

                                for (int j = 0; j < sizeArray.length(); j++) {
                                    JSONObject size = sizeArray.getJSONObject(j);
                                    int sizeId = size.getInt("id");
                                    String sizeName = size.getString("size");
                                    double price = size.getDouble("price");
                                    sizes.add(new Size(sizeId, sizeName, price));
                                }

                                JSONArray jsonImageUrls = jsonObject.getJSONArray("pictures");
                                ArrayList<String> imageUrls = new ArrayList<String>();

                                for (int j = 0; j < jsonImageUrls.length(); j++) {
                                    JSONArray imageArray = jsonImageUrls.getJSONArray(j);
                                    String imageUrl = (String) imageArray.get(j);
                                    imageUrls.add(imageUrl);
                                }

                                listData.add(new Product(id, name, description, rate, sizes, imageUrls));
                            }
                            drinkRecycleAdapter = new ProductRecycleAdapter(listData, getApplicationContext());
                            drinkRecyclerView.setAdapter(drinkRecycleAdapter);
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
    private void getFoodProduct(int id) {
        objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl.API_STORES + "/" + id + "?get_all=1",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONObject("food").getJSONArray("products");

                            ArrayList<Product> listData = new ArrayList<Product>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String name = jsonObject.getString("name");
                                String description = jsonObject.getString("description");
                                double rate = jsonObject.getDouble("avg_rate_score");

                                JSONArray sizeArray = jsonObject.getJSONArray("sizes");
                                ArrayList<Size> sizes = new ArrayList<Size>();

                                for (int j = 0; j < sizeArray.length(); j++) {
                                    JSONObject size = sizeArray.getJSONObject(j);
                                    int sizeId = size.getInt("id");
                                    String sizeName = size.getString("size");
                                    double price = size.getDouble("price");
                                    sizes.add(new Size(sizeId, sizeName, price));
                                }

                                JSONArray jsonImageUrls = jsonObject.getJSONArray("pictures");
                                ArrayList<String> imageUrls = new ArrayList<String>();

                                for (int j = 0; j < jsonImageUrls.length(); j++) {
                                    JSONArray imageArray = jsonImageUrls.getJSONArray(j);
                                    String imageUrl = (String) imageArray.get(j);
                                    imageUrls.add(imageUrl);
                                }

                                listData.add(new Product(id, name, description, rate, sizes, imageUrls));
                            }
                            foodRecycleAdapter = new ProductRecycleAdapter(listData, getApplicationContext());
                            foodRecyclerView.setAdapter(foodRecycleAdapter);
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

    private void setTabHost() {
        tabHostStore.setup();

        TabHost.TabSpec tabDrink = tabHostStore.newTabSpec("tabDrink");
        tabDrink.setIndicator("Drink");
        tabDrink.setContent(R.id.tab_drink);
        tabHostStore.addTab(tabDrink);

        TabHost.TabSpec tabFood = tabHostStore.newTabSpec("tabFood");
        tabFood.setIndicator("Food");
        tabFood.setContent(R.id.tab_food);
        tabHostStore.addTab(tabFood);
    }

    private void setFlipper(ArrayList<String> imageUrls) {
        in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        viewFlipper.setInAnimation(in);
        viewFlipper.setOutAnimation(out);
        viewFlipper.setFlipInterval(3000);
        viewFlipper.startFlipping();
        ImageView imageView;

        if (imageUrls.size() == 0) {
            imageView = new ImageView(getApplicationContext());
            imageView.setImageResource(R.drawable.noimage);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewFlipper.addView(imageView);
            viewFlipper.stopFlipping();
        } else {
            for (String imageUrl: imageUrls) {
                imageView = new ImageView(getApplicationContext());

                if (!imageUrl.equals("")) {
                    Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.loader)
                            .error(R.drawable.slider1)
                            .into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.noimage);
                }

                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                viewFlipper.addView(imageView);
            }

            if (imageUrls.size() == 1) {
                viewFlipper.stopFlipping();
            }
        }
    }
}

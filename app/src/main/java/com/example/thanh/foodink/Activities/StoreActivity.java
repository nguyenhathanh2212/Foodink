package com.example.thanh.foodink.Activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Adapter.ProductRecycleAdapter;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Helpers.CheckConnection;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Models.Product;
import com.example.thanh.foodink.Models.Size;
import com.example.thanh.foodink.Models.Store;
import com.example.thanh.foodink.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
    private Progresser progress;
    private static FragmentManager fragmentManager;
    private CheckConnection checkConnection;
    private ArrayList<String> imageUrls;
    private ArrayList<Product> listDrinkData;
    private ArrayList<Product> listFoodData;
    private Store store;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout swipeRefreshLayoutFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        setContentView(R.layout.activity_store);
        addControls();
        addEvents();
    }

    private void addEvents() {
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        try {
                            if (checkConnection.isNetworkAvailable()) {
                                listDrinkData.clear();
                                getDrinkProduct(store.getId());
                            } else {
                                Toast.makeText(getApplicationContext(), "Vui lòng bật kết nối mạng!", Toast.LENGTH_SHORT).show();
                            }

                            swipeRefreshLayout.setRefreshing(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
        );
        swipeRefreshLayoutFood.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        try {
                            if (checkConnection.isNetworkAvailable()) {
                                listFoodData.clear();
                                getFoodProduct(store.getId());
                            } else {
                                Toast.makeText(getApplicationContext(), "Vui lòng bật kết nối mạng!", Toast.LENGTH_SHORT).show();
                            }

                            swipeRefreshLayoutFood.setRefreshing(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
        );
    }

    private void addControls() {
        Bundle bundle = getIntent().getExtras();
        store = (Store) bundle.getSerializable("store");
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
        progress = new Progresser(this, "", "Đang load dữ liệu...");
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayoutFood = findViewById(R.id.swiperefresh2);
        setTabHost();
        listDrinkData = new ArrayList<Product>();
        drinkRecycleAdapter = new ProductRecycleAdapter(listDrinkData, getApplicationContext(), getSupportFragmentManager());
        drinkRecyclerView.setAdapter(drinkRecycleAdapter);
        listFoodData = new ArrayList<Product>();
        foodRecycleAdapter = new ProductRecycleAdapter(listFoodData, getApplicationContext(), getSupportFragmentManager());
        foodRecyclerView.setAdapter(foodRecycleAdapter);

        checkConnection = new CheckConnection(getBaseContext());



        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Calendar cal = Calendar.getInstance();
        String currentTime = dateFormat.format(cal.getTime());

        if (store.getOpentAt().compareTo(store.getCloseAt()) != 0 && store.getOpentAt().compareTo(currentTime) < 0 && store.getCloseAt().compareTo(currentTime) > 0) {
            imgStatus.setImageResource(R.drawable.offline);
        }

        if (checkConnection.isNetworkAvailable()) {
            getDrinkProduct(store.getId());
            tabHostStore.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                @Override
                public void onTabChanged(String tabId) {
                    if (tabId.equals("tabFood") && listFoodData.isEmpty()) {
                        getFoodProduct(store.getId());
                    }
                }
            });

            imageUrls = store.getImages();
            setFlipper(imageUrls);
        } else {
            Toast.makeText(getApplicationContext(), "Vui lòng bật kết nối mạng!", Toast.LENGTH_SHORT).show();
        }

    }

    private void getDrinkProduct(int id) {
        progress.show();
        objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl.API_STORES + "/" + id + "?get_all=1",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.hide();
                        try {
                            JSONArray jsonArray = response.getJSONObject("drink").getJSONArray("products");

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
                                    String imageUrl = (String) jsonImageUrls.get(j);
                                    imageUrls.add(imageUrl);
                                }

                                listDrinkData.add(new Product(id, name, description, rate, sizes, imageUrls));
                            }
                            drinkRecycleAdapter.notifyDataSetChanged();
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
    private void getFoodProduct(int id) {
        progress.show();
        objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl.API_STORES + "/" + id + "?get_all=1",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.hide();
                        try {
                            JSONArray jsonArray = response.getJSONObject("food").getJSONArray("products");

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
                                    String imageUrl = (String) jsonImageUrls.get(j);
                                    imageUrls.add(imageUrl);
                                }

                                listFoodData.add(new Product(id, name, description, rate, sizes, imageUrls));
                            }
                            foodRecycleAdapter.notifyDataSetChanged();
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

        Display display = getWindowManager().getDefaultDisplay();

        tabHostStore.getTabWidget().getChildAt(0).setLayoutParams(new
                LinearLayout.LayoutParams(display.getWidth() / 2, 100));
        tabHostStore.getTabWidget().getChildAt(1).setLayoutParams(new
                LinearLayout.LayoutParams(display.getWidth() / 2, 100));
        tabHostStore.getTabWidget().getChildAt(0).getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
        tabHostStore.getTabWidget().getChildAt(1).getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
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

    public static FragmentManager getFragManager() {
        return fragmentManager;
    }
}

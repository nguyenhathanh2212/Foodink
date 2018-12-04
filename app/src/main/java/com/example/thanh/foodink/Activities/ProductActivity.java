package com.example.thanh.foodink.Activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import com.example.thanh.foodink.Adapter.RateRecycleAdapter;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Dialog.ProductDialog;
import com.example.thanh.foodink.Helpers.CheckConnection;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Models.Product;
import com.example.thanh.foodink.Models.Rate;
import com.example.thanh.foodink.Models.Size;
import com.example.thanh.foodink.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    protected TextView txtName, txtDescription;
    protected ViewFlipper viewFlipper;
    private Animation in, out;
    private RecyclerView rateRecyclerView;
    private RateRecycleAdapter rateRecycleAdapter;
    private ArrayList<Rate> listRates;
    private RequestQueue requestQueue;
    private JsonObjectRequest objectRequest;
    private ApiUrl apiUrl;
    private Progresser progress;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CheckConnection checkConnection;
    private Product product;
    private Button btnOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
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
                                listRates.clear();
                                getRates(product.getId());
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

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDialog productDialog = ProductDialog.newInstance(product);
                productDialog.show(getSupportFragmentManager(), null);
            }
        });
    }

    private void addControls() {
        txtName = findViewById(R.id.txt_name);
        txtDescription = findViewById(R.id.txt_desciption);
        viewFlipper= findViewById(R.id.img_background);
        rateRecyclerView = findViewById(R.id.recycler_rates);
        Bundle bundle = getIntent().getExtras();
        product = (Product) bundle.getSerializable("product");
        txtName.setText(product.getName() + "");
        txtDescription.setText(product.getDescription() + "");
        setFlipper(product.getImages());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rateRecyclerView.setLayoutManager(layoutManager);
        listRates = new ArrayList<Rate>();
        rateRecycleAdapter = new RateRecycleAdapter(listRates, getApplicationContext());
        rateRecyclerView.setAdapter(rateRecycleAdapter);
        requestQueue = Volley.newRequestQueue(this);
        progress = new Progresser(this, "", "Đang load dữ liệu...");
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        checkConnection = new CheckConnection(getBaseContext());
        btnOrder = findViewById(R.id.btn_order);

        if (checkConnection.isNetworkAvailable()) {
            getRates(product.getId());
        } else {
            Toast.makeText(getApplicationContext(), "Vui lòng bật kết nối mạng!", Toast.LENGTH_SHORT).show();
        }
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

    public void getRates(int id) {
        progress.show();
        objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl.API_PRODUCT + "/" + id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.hide();
                        try {
                            JSONArray jsonArray = response.getJSONObject("product").getJSONArray("rates");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String content = jsonObject.getString("content");
                                double rate = jsonObject.getDouble("rate");

                                listRates.add(new Rate(content, rate));
                            }

                            if (listRates.size() == 0) {
                                rateRecyclerView.setVisibility(View.INVISIBLE);
                            } else {
                                rateRecycleAdapter.notifyDataSetChanged();
                            }
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

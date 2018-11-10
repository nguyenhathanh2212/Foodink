package com.example.thanh.foodink.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Adapter.ProductRecycleAdapter;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Models.Product;
import com.example.thanh.foodink.Models.Size;
import com.example.thanh.foodink.Models.Store;
import com.example.thanh.foodink.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductCategoryActivity extends AppCompatActivity {

    private RecyclerView productRecyclerView;
    private ProductRecycleAdapter productRecycleAdapter;
    private RequestQueue requestQueue;
    private JsonObjectRequest objectRequest;
    private ApiUrl apiUrl;
    private Progresser progress;
    private TextView txtNameCategory;
    private RelativeLayout layoutSearch;
    private ImageView btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_category);

        addControls();
    }

    private void addControls() {
        productRecyclerView = findViewById(R.id.recycler_product_categories);
        txtNameCategory = findViewById(R.id.name_category);
        layoutSearch = findViewById(R.id.layout_search);
        btnSearch = findViewById(R.id.btnSearch);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        requestQueue = Volley.newRequestQueue(this);
        progress = new Progresser(this, "", "Đang load dữ liệu...");
        Bundle bundle = getIntent().getExtras();
        int categoryId = bundle.getInt("category_id", 1);
        
        addProducts(categoryId);
    }

    private void addProducts(int categoryId) {
        progress.show();
        objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl.API_CATEGORIES + "/" + categoryId + "?get_all=1",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.hide();
                        try {
                            JSONObject jsonCategory = response.getJSONObject("category");
                            String nameCategory = jsonCategory.getString("name");
                            txtNameCategory.setText(nameCategory);

                            JSONArray jsonArray = response.getJSONArray("products");

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
                            productRecycleAdapter = new ProductRecycleAdapter(listData, getApplicationContext(), getSupportFragmentManager());
                            productRecyclerView.setAdapter(productRecycleAdapter);
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

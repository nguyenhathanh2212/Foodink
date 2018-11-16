package com.example.thanh.foodink.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Configs.ResponseStatusCode;
import com.example.thanh.foodink.Exceptions.ValidationException;
import com.example.thanh.foodink.Helpers.FontManager;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Helpers.Validation;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements OnClickListener {
    private TextView btnBack;

    private ImageView btnLoginFB;
    private ImageView btnLoginGG;
    private ImageView btnLoginTW;

    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;

    private TextView btnForgotPassword;
    private TextView btnRegister;

    private RequestQueue requestQueue;
    private Progresser progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Typeface iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.loginViewLayout), iconFont);

        mappingWidgets();
    }

    private void mappingWidgets() {
        btnBack = (TextView) findViewById(R.id.btnBack);

        btnLoginFB = (ImageView) findViewById(R.id.btnLoginFB);
        btnLoginGG = (ImageView) findViewById(R.id.btnLoginGG);
        btnLoginTW = (ImageView) findViewById(R.id.btnLoginTW);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnForgotPassword = (TextView) findViewById(R.id.btnForgotPassword);
        btnRegister = (TextView) findViewById(R.id.btnRegister);

        btnBack.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(this);

        progress = new Progresser(this, "Đăng nhập", "Đang đăng nhập...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnRegister:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void login() {
        try {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            validate(email, password);

            progress.show();
            JSONObject params = new JSONObject();
            params.put("email", email);
            params.put("password", password);

            JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ApiUrl.API_LOGIN,
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.hide();

                        try {
                            boolean isSuccess = response.getBoolean("success");

                            if (!isSuccess) {
                                throw new ValidationException("Đăng nhập thất bại, Email hoặc mật khẩu không đúng!");
                            }

                            JSONObject authInfo = response.getJSONObject("auth_token");
                            String authToken = authInfo.getString("token");
                            String refreshToken = authInfo.getString("refresh_token");
                            String expiredAt = authInfo.getString("expired_at");
                            JSONObject userInfo = authInfo.getJSONObject("user");
                            int id = userInfo.getInt("id");
                            String email = userInfo.getString("email");
                            String name = userInfo.getString("name");
                            String phone = userInfo.getString("phone");
                            String address = userInfo.getString("address");
                            String avatar = userInfo.getString("avatar");

                            JSONObject shipper = userInfo.getJSONObject("shipper");
                            int shipperId = (shipper != null) ? shipper.getInt("id") : 0;

                            User user = new User(id, email, name, phone, address, avatar, authToken, refreshToken, expiredAt, shipperId);
                            User.setUserAuth(LoginActivity.this, user);


                            if (shipperId > 0) {
                                updateStatusShipper();
                            }

                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (ValidationException e) {
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "Có lỗi! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                            Log.d("ApiError", e.toString());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hide();
                        String msg = "Có lỗi, vui lòng thử lại";

                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.statusCode == ResponseStatusCode.BAD_REQUEST) {
                            try {
                                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                JSONObject data = new JSONObject(res);
                                boolean isSuccess = data.getBoolean("success");

                                if (!isSuccess) {
                                    msg = "Đăng nhập thất bại, Email hoặc mật khẩu không đúng!";
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("ApiError", e.toString());
                            }
                        }

                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.d("ApiError", error.toString());
                        error.printStackTrace();
                    }
                }
            );

            requestQueue.add(objectRequest);
        } catch (ValidationException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Có lỗi! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void validate(String email, String password) throws ValidationException {
        if (email.equals("")) {
            edtEmail.requestFocus();
            throw new ValidationException("Email không được để trống");
        }

        if (password.equals("")) {
            edtPassword.requestFocus();
            throw new ValidationException("Mật khẩu không được để trống");
        }

        if (!Validation.isValidEmaillId(email)) {
            edtEmail.requestFocus();
            throw new ValidationException("Email phải hợp lệ");
        }
    }

    private void updateStatusShipper() {
        progress.show();
        try {
            JSONObject params = new JSONObject();
            params.put("device_token", FirebaseInstanceId.getInstance().getToken());

            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiUrl.API_CHANGE_SHIPPER_STATUS,
                    params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress.hide();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.hide();
                            Log.d("ApiError", error.toString());
                            error.printStackTrace();
                        }
                    }
            ) {
                public Map<String, String> getHeaders() {
                    Map<String, String> mHeaders = new ArrayMap<String, String>();
                    mHeaders.put("Authorization", User.getUserAuth(LoginActivity.this).getAuthToken());

                    return mHeaders;
                }
            };

            requestQueue.add(objectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


package com.example.thanh.foodink.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Exceptions.ValidationException;
import com.example.thanh.foodink.Helpers.FontManager;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Helpers.Validation;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.READ_CONTACTS;

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
        }
    }

    private void login() {
        try {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.equals("")) {
                edtEmail.requestFocus();
                throw new ValidationException("Email không được để trống");
            }

            if (password.equals("")) {
                edtPassword.requestFocus();
                throw new ValidationException("Password không được để trống");
            }

            if (!Validation.isValidEmaillId(email)) {
                edtEmail.requestFocus();
                throw new ValidationException("Email phải hợp lệ");
            }

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
                            JSONObject userInfo = authInfo.getJSONObject("user");
                            int id = userInfo.getInt("id");
                            String email = userInfo.getString("email");
                            String name = userInfo.getString("name");
                            String phone = userInfo.getString("phone");
                            String address = userInfo.getString("address");

                            User user = new User(id, email, name, phone, address, authToken);
                            User.setUserAuth(LoginActivity.this, user);

                            Toast.makeText(LoginActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(LoginActivity.this, "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
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


}


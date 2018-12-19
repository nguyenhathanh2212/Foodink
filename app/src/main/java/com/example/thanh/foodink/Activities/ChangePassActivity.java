package com.example.thanh.foodink.Activities;

import android.graphics.Typeface;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONObject;

import java.util.Map;

public class ChangePassActivity extends AppCompatActivity implements View.OnClickListener {

    protected EditText edOldPass, edNewPass, edConfirmPass;
    protected TextView btnBack;
    protected Button btnChange;
    private RequestQueue requestQueue;
    private Progresser progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnChange.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void addControls() {
        Typeface iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.loginViewLayout), iconFont);
        edNewPass = findViewById(R.id.edtNewPassword);
        edOldPass = findViewById(R.id.edtOldPassword);
        edConfirmPass = findViewById(R.id.edtPasswordConfirm);
        btnChange = findViewById(R.id.btnChange);
        btnBack = findViewById(R.id.btnBack);
        requestQueue = Volley.newRequestQueue(this);
        progress = new Progresser(this, "Đăng ký", "Đang đăng ký...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnChange:
                changePassWord();
                break;
        }
    }

    private void changePassWord() {
        try {
            String newPassword = edNewPass.getText().toString().trim();
            String oldPassword = edOldPass.getText().toString().trim();
            String passwordConfirm = edConfirmPass.getText().toString().trim();
            validate(oldPassword, newPassword, passwordConfirm);

            progress.show();
            JSONObject params = new JSONObject();
            params.put("password", newPassword);
            params.put("password_confirm", passwordConfirm);

            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.PATCH,
                    ApiUrl.API_CHANGEPASS,
                    params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress.hide();

                            try {
                                boolean isSuccess = response.getBoolean("success");

                                if (!isSuccess) {
                                    throw new ValidationException("Thay đổi mật khẩu thất bại");
                                }

                                Toast.makeText(ChangePassActivity.this, "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            } catch (ValidationException e) {
                                Toast.makeText(ChangePassActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            } catch (Exception e) {
                                Toast.makeText(ChangePassActivity.this, "Có lỗi! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
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
                            if (response != null && response.statusCode == ResponseStatusCode.UNPROCESSABLE_ENTITY) {
                                try {
                                    String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                    JSONObject data = new JSONObject(res);
                                    boolean isSuccess = data.getBoolean("success");

                                    if (!isSuccess) {
                                        msg = "Đăng ký thất bại! Email này đã được sử dụng!";
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d("ApiError", e.toString());
                                }
                            }

                            Toast.makeText(ChangePassActivity.this, msg, Toast.LENGTH_SHORT).show();
                            Log.d("ApiError", error.toString());
                            error.printStackTrace();
                        }
                    }
            ) {
                public Map<String, String> getHeaders() {
                    Map<String, String> mHeaders = new ArrayMap<String, String>();
                    mHeaders.put("Authorization", User.getUserAuth(getApplicationContext()).getAuthToken());

                    return mHeaders;
                }
            };

            requestQueue.add(objectRequest);
        } catch (ValidationException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Có lỗi! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void validate(String oldPassword, String newPassword, String passwordConfirm) throws ValidationException {
        if (oldPassword.equals("")) {
            edOldPass.requestFocus();
            throw new ValidationException("Mật khẩu củ không được để trống");
        }

        if (newPassword.equals("")) {
            edNewPass.requestFocus();
            throw new ValidationException("Mật khẩu mới không được để trống");
        }

        if (!newPassword.equals(passwordConfirm)) {
            edConfirmPass.requestFocus();
            throw new ValidationException("Mật khẩu xác nhận phải trùng với mật khẩu mới");
        }
    }
}

package com.example.thanh.foodink.Activities;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.thanh.foodink.R;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView btnBack;

    private ImageView btnRegisterFB;
    private ImageView btnRegisterGG;
    private ImageView btnRegisterTW;

    private EditText edtName;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtPasswordConfirm;
    private Button btnRegister;
    private TextView btnLogin;

    private RequestQueue requestQueue;
    private Progresser progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Typeface iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.loginViewLayout), iconFont);

        mappingWidgets();
    }

    private void mappingWidgets() {
        btnBack = (TextView) findViewById(R.id.btnBack);

        btnRegisterFB = (ImageView) findViewById(R.id.btnRegisterFB);
        btnRegisterGG = (ImageView) findViewById(R.id.btnRegisterGG);
        btnRegisterTW = (ImageView) findViewById(R.id.btnRegisterTW);

        edtName = (EditText) findViewById(R.id.edtName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPasswordConfirm = (EditText) findViewById(R.id.edtPasswordConfirm);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLogin = (TextView) findViewById(R.id.btnLogin);

        btnBack.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(this);

        progress = new Progresser(this, "Đăng ký", "Đang đăng ký...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnLogin:
                finish();
                break;
            case R.id.btnRegister:
                register();
                break;
        }
    }

    private void register() {
        try {
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String passwordConfirm = edtPasswordConfirm.getText().toString().trim();

            validate(name, email, password, passwordConfirm);

            progress.show();
            JSONObject params = new JSONObject();
            params.put("name", name);
            params.put("email", email);
            params.put("password", password);
            params.put("password_confirm", passwordConfirm);

            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    ApiUrl.API_REGISTER,
                    params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress.hide();

                            try {
                                boolean isSuccess = response.getBoolean("success");

                                if (!isSuccess) {
                                    throw new ValidationException("Đăng ký thất bại! Email này đã được sử dụng!");
                                }

                                Toast.makeText(RegisterActivity.this, "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            } catch (ValidationException e) {
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            } catch (Exception e) {
                                Toast.makeText(RegisterActivity.this, "Có lỗi! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    private void validate(String name, String email, String password, String passwordConfirm) throws ValidationException {
        if (name.equals("")) {
            edtName.requestFocus();
            throw new ValidationException("Tên không được để trống");
        }

        if (email.equals("")) {
            edtEmail.requestFocus();
            throw new ValidationException("Email không được để trống");
        }

        if (password.equals("")) {
            edtPassword.requestFocus();
            throw new ValidationException("Mật khẩu không được để trống");
        }

        if (name.length() < 6 || name.length() > 255) {
            edtName.requestFocus();
            throw new ValidationException("Tên phải có độ dài trong khoảng 6-255 ký tự");
        }

        if (!Validation.isValidEmaillId(email)) {
            edtEmail.requestFocus();
            throw new ValidationException("Email phải hợp lệ");
        }

        if (email.length() < 6 || email.length() > 255) {
            edtEmail.requestFocus();
            throw new ValidationException("Email phải có độ dài trong khoảng 6-255 ký tự");
        }

        if (password.length() < 6 || password.length() > 255) {
            edtPassword.requestFocus();
            throw new ValidationException("Mật khẩu phải có độ dài trong khoảng 6-255 ký tự");
        }

        if (!password.equals(passwordConfirm)) {
            edtPasswordConfirm.requestFocus();
            throw new ValidationException("Mật khẩu xác nhận phải trùng với mật khẩu");
        }
    }
}

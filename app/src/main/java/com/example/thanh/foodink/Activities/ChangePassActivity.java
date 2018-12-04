package com.example.thanh.foodink.Activities;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Exceptions.ValidationException;
import com.example.thanh.foodink.Helpers.FontManager;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Helpers.Validation;
import com.example.thanh.foodink.R;

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

        if (!oldPassword.equals(passwordConfirm)) {
            edConfirmPass.requestFocus();
            throw new ValidationException("Mật khẩu xác nhận phải trùng với mật khẩu mới");
        }
    }
}

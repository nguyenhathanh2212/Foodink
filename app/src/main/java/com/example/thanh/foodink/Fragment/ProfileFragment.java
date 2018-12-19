package com.example.thanh.foodink.Fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Activities.ChangePassActivity;
import com.example.thanh.foodink.Activities.LoginActivity;
import com.example.thanh.foodink.Activities.MainActivity;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Exceptions.ValidationException;
import com.example.thanh.foodink.Helpers.FontManager;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;
import com.example.thanh.foodink.Services.UpdateLocationService;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.Map;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private LinearLayout logoutLayout;

    private RelativeLayout btnLogout;
    private RelativeLayout btnChangePass;
    private Typeface iconFont;

    private RequestQueue requestQueue;
    private Progresser progress;

    private TextView txtPhone, txtAddress, txtEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        mappingWidgets();
        showUserHeaderLayout();

        return rootView;
    }

    private void mappingWidgets() {
        iconFont = FontManager.getTypeface(getContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(rootView.findViewById(R.id.profileViewLayout), iconFont);
        logoutLayout = (LinearLayout) rootView.findViewById(R.id.logoutLayout);
        requestQueue = Volley.newRequestQueue(getContext());
        txtAddress = rootView.findViewById(R.id.address);
        txtEmail = rootView.findViewById(R.id.email);
        txtPhone = rootView.findViewById(R.id.phone);

        progress = new Progresser(getContext(), "Đăng xuất", "Đang đăng xuất...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogout:
                logout();
                break;
            case R.id.btn_change_pass:
                changePassword();
                break;
        }
    }

    private void changePassword() {
        Intent intent = new Intent(getContext(), ChangePassActivity.class);
        startActivity(intent);
    }

    private void showUserHeaderLayout() {
        FragmentManager fm = MainActivity.getFragManager();
        FragmentTransaction ft_rep = fm.beginTransaction();
        ft_rep.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if (User.checkUserAuth(getContext())) {
            ft_rep.replace(R.id.headerLayout, new UserInforFragment());
            ft_rep = ft_rep.addToBackStack(null);
            ft_rep.commitAllowingStateLoss();

            logoutLayout.removeAllViews();
            View logoutView = getLayoutInflater().inflate(R.layout.logout_fragment, null);
            logoutLayout.addView(logoutView);
            FontManager.markAsIconContainer(rootView.findViewById(R.id.btnLogout), iconFont);
            FontManager.markAsIconContainer(rootView.findViewById(R.id.btn_change_pass), iconFont);

            btnLogout = (RelativeLayout) rootView.findViewById(R.id.btnLogout);
            btnChangePass = (RelativeLayout) rootView.findViewById(R.id.btn_change_pass);
            btnLogout.setOnClickListener(this);
            btnChangePass.setOnClickListener(this);

            if (!User.getUserAuth(getContext()).getPhone().equals("null")) {
                txtPhone.setText(User.getUserAuth(getContext()).getPhone() + "");
            }

            if (!User.getUserAuth(getContext()).getEmail().equals("null")) {
                txtEmail.setText(User.getUserAuth(getContext()).getEmail() + "");
            }

            if (!User.getUserAuth(getContext()).getAddress().equals("null")) {
                txtAddress.setText(User.getUserAuth(getContext()).getAddress() + "");
            }

        } else {
            logoutLayout.removeAllViews();
            ft_rep.replace(R.id.headerLayout, new LoginFragment());
            ft_rep = ft_rep.addToBackStack(null);
            ft_rep.commitAllowingStateLoss();
            txtPhone.setText("Số điện thoại");
            txtEmail.setText("Email");
            txtAddress.setText("Địa chỉ");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showUserHeaderLayout();
    }

    private void logout() {
        progress.show();

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                ApiUrl.API_LOGOUT,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.hide();

                        if (User.getUserAuth(getContext()).getShipperId() > 0) {
                            updateStatusShipper();
                        }

                        getActivity().stopService(new Intent(getActivity(), UpdateLocationService.class));
                        User.logout(getContext());
                        showUserHeaderLayout();
                        Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.hide();
                        Toast.makeText(getContext(), "Có lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        Log.d("ApiError", error.toString());
                        error.printStackTrace();
                    }
                }
        ) {
            public Map<String, String> getHeaders() {
                Map<String, String> mHeaders = new ArrayMap<String, String>();
                mHeaders.put("Authorization", User.getUserAuth(getContext()).getAuthToken());

                return mHeaders;
            }
        };

        requestQueue.add(objectRequest);
    }

    private void updateStatusShipper() {
        try {
            String deviceToken = FirebaseInstanceId.getInstance().getToken();

            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.DELETE,
                    ApiUrl.API_CHANGE_SHIPPER_STATUS + "/" + deviceToken,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("ApiError", error.toString());
                            error.printStackTrace();
                        }
                    }
            ) {
                public Map<String, String> getHeaders() {
                    Map<String, String> mHeaders = new ArrayMap<String, String>();
                    mHeaders.put("Authorization", User.getUserAuth(getContext()).getAuthToken());

                    return mHeaders;
                }
            };

            requestQueue.add(objectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

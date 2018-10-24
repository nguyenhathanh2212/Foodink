package com.example.thanh.foodink.Fragment;

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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Activities.LoginActivity;
import com.example.thanh.foodink.Configs.ApiUrl;
import com.example.thanh.foodink.Exceptions.ValidationException;
import com.example.thanh.foodink.Helpers.FontManager;
import com.example.thanh.foodink.Helpers.Progresser;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;

import org.json.JSONObject;

import java.util.Map;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private LinearLayout logoutLayout;

    private RelativeLayout btnLogout;
    private Typeface iconFont;

    private RequestQueue requestQueue;
    private Progresser progress;

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

        progress = new Progresser(getContext(), "Đăng xuất", "Đang đăng xuất...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogout:
                logout();
                break;
        }
    }

    private void showUserHeaderLayout() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft_rep = fm.beginTransaction();
        ft_rep.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if (User.checkUserAuth(getContext())) {
            ft_rep.replace(R.id.headerLayout, new UserInforFragment());
            ft_rep.commit();

            logoutLayout.removeAllViews();
            View logoutView = getLayoutInflater().inflate(R.layout.logout_fragment, null);
            logoutLayout.addView(logoutView);
            FontManager.markAsIconContainer(rootView.findViewById(R.id.btnLogout), iconFont);

            btnLogout = (RelativeLayout) rootView.findViewById(R.id.btnLogout);
            btnLogout.setOnClickListener(this);
        } else {
            logoutLayout.removeAllViews();
            ft_rep.replace(R.id.headerLayout, new LoginFragment());
            ft_rep.commit();
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
}

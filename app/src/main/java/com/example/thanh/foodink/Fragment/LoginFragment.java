package com.example.thanh.foodink.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.thanh.foodink.Activities.LoginActivity;
import com.example.thanh.foodink.R;

/**
 * Created by Vinh Nguyen on 10/21/2018.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {
        private View rootView;
        private LinearLayout btnLogin;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.login_fragment, container, false);

            btnLogin = (LinearLayout) rootView.findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(this);

            return rootView;
        }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}

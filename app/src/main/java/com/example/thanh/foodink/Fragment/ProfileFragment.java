package com.example.thanh.foodink.Fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thanh.foodink.Activities.LoginActivity;
import com.example.thanh.foodink.Helpers.FontManager;
import com.example.thanh.foodink.R;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private LinearLayout lbLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        Typeface iconFont = FontManager.getTypeface(getContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(rootView.findViewById(R.id.profileViewLayout), iconFont);

        lbLogin = (LinearLayout) rootView.findViewById(R.id.lbLogin);
        lbLogin.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lbLogin:
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}

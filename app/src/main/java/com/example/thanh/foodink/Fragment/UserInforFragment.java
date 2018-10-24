package com.example.thanh.foodink.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Vinh Nguyen on 10/21/2018.
 */

public class UserInforFragment extends Fragment {
    private View rootView;
    private ImageView imgAvatar;
    private TextView tvUsername;
    private User userAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.user_infor_fragment, container, false);

        mappingWidgets();

        return rootView;
    }

    private void mappingWidgets() {
        userAuth = User.getUserAuth(getContext());
        imgAvatar = (ImageView) rootView.findViewById(R.id.imgAvatar);
        tvUsername = (TextView) rootView.findViewById(R.id.tvUsername);
        tvUsername.setText(userAuth.getName());

        if (userAuth.getAvatar().equals("")) {
            return;
        }

        Picasso.get()
            .load(userAuth.getAvatar())
            .placeholder(R.drawable.avatar_default)
            .error(R.drawable.avatar_default)
            .into(imgAvatar);
    }
}

package com.example.thanh.foodink.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thanh.foodink.Activities.MainActivity;
import com.example.thanh.foodink.Adapter.OrderTabAdapter;
import com.example.thanh.foodink.Helpers.SessionManager;
import com.example.thanh.foodink.Models.User;
import com.example.thanh.foodink.R;

public class OrderFragment extends Fragment {private ViewPager viewPager;
    private TabLayout tabLayout;
    private View rootView;
    private SessionManager sessionManager;
    private LinearLayout linearRequest;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sessionManager = SessionManager.getInstant(getContext());
        rootView = inflater.inflate(R.layout.order_fragment, container, false);

        addControls();

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            addControls();
        }
    }

    private void addControls() {
        linearRequest = rootView.findViewById(R.id.request_screen);

        if (sessionManager.has(User.AUTH)) {
            linearRequest.setVisibility(LinearLayout.INVISIBLE);
            viewPager = rootView.findViewById(R.id.order_viewpager);
            viewPager.setAdapter(new OrderTabAdapter(MainActivity.getFragManager()));
            tabLayout = rootView.findViewById(R.id.order_tab_layout);
            tabLayout.setupWithViewPager(viewPager);
        } else {
            TextView tvTitle = (TextView) rootView.findViewById(R.id.txt_title);
            TextView tvMsg = (TextView) rootView.findViewById(R.id.txt_msg);
            tvTitle.setText("Không thể tải dữ liệu");
            tvMsg.setText("Bạn chưa đăng nhập, vui lòng đăng nhập để xem giỏ hàng");
            linearRequest.setVisibility(LinearLayout.VISIBLE);
        }
    }
}

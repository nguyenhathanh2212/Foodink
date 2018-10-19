package com.example.thanh.foodink.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.thanh.foodink.Adapter.TabHomeAdapter;
import com.example.thanh.foodink.R;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int[] imageResId = {
            R.drawable.home,
            R.drawable.order,
            R.drawable.notification,
            R.drawable.profile
    };
    private int currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
    }

    private void addControls() {
        viewPager = findViewById(R.id.home_viewpager);
        viewPager.setAdapter(new TabHomeAdapter(getSupportFragmentManager()));
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        this.setTabIcon();
    }

    private void setTabIcon() {
        currentFragment = viewPager.getCurrentItem();
        tabLayout.getTabAt(0).setIcon(imageResId[0]);
        tabLayout.getTabAt(1).setIcon(imageResId[1]);
        tabLayout.getTabAt(2).setIcon(imageResId[2]);
        tabLayout.getTabAt(3).setIcon(imageResId[3]);
    }
}

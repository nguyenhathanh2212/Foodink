package com.example.thanh.foodink.Activities;

import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.thanh.foodink.Adapter.TabHomeAdapter;
import com.example.thanh.foodink.Broadcast.ManagerConnect;
import com.example.thanh.foodink.Dialog.CheckConnectionDialog;
import com.example.thanh.foodink.Helpers.CheckConnection;
import com.example.thanh.foodink.R;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private static FragmentManager fragmentManager;
    private ManagerConnect managerConnect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        managerConnect = new ManagerConnect();
//        final IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
//        registerReceiver(managerConnect, filter);
        addControls();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        unregisterReceiver(managerConnect);
//    }

    private void addControls() {
        fragmentManager = getSupportFragmentManager();
        viewPager = findViewById(R.id.home_viewpager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new TabHomeAdapter(getSupportFragmentManager(), getBaseContext()));
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        this.setTabIcon();
        CheckConnection checkConnection = new CheckConnection(getBaseContext());

        if (checkConnection.isNetworkAvailable() == false) {
            CheckConnectionDialog checkConnectionDialog = new CheckConnectionDialog();
            checkConnectionDialog.show(this.getSupportFragmentManager(), "");
        }
    }

    private void setTabIcon() {
        currentFragment = viewPager.getCurrentItem();
        tabLayout.getTabAt(0).setIcon(imageResId[0]);
        tabLayout.getTabAt(1).setIcon(imageResId[1]);
        tabLayout.getTabAt(2).setIcon(imageResId[2]);
        tabLayout.getTabAt(3).setIcon(imageResId[3]);
    }

    public static FragmentManager getFragManager() {
        return fragmentManager;
    }
}

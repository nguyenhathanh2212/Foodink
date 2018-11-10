package com.example.thanh.foodink.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class ManagerConnect extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        ConnectivityManager conMan = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
//        Bundle bundle = new Bundle();
//        if (netInfo != null)
//            Log.d("AAAAAAAAAAA", "Have Wifi Connection");
//        else
//            Log.d("AAAAAAAAAAA", "Don't have Wifi Connection");
    }
}

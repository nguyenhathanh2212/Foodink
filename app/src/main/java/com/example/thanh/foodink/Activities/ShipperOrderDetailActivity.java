package com.example.thanh.foodink.Activities;

import android.app.NotificationManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.thanh.foodink.R;

public class ShipperOrderDetailActivity extends AppCompatActivity {
    public static final String ACCEPT_ACTION = "Accept";
    public static final String REJECT_ACTION = "Reject";
    public static final String SHOW_ACTION = "Show";

    private TextView tvOrderId;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_order_detail);

        mappingWidgets();
        hideNotification();
        process();
    }

    private void mappingWidgets() {
        intent = getIntent();
        int id = intent.getIntExtra("SHIPPER_ORDER_ID", 0);

        tvOrderId = (TextView) findViewById(R.id.tvOrderId);
        tvOrderId.setText(id + " - ");
    }

    private void process() {
        String action = intent.getAction();

        if (action == null) {
            return;
        }

        switch (action) {
            case ACCEPT_ACTION:
                tvOrderId.setText(tvOrderId.getText().toString() + "Accept");
                break;

            case REJECT_ACTION:
                tvOrderId.setText(tvOrderId.getText().toString() + "Reject");
                break;

            default:
                tvOrderId.setText(tvOrderId.getText().toString() + "Show");
                break;
        }
    }

    private void hideNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(getIntent().getIntExtra("NOTIFICATION_ID", -1));
    }
}

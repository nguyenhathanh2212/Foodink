package com.example.thanh.foodink.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.thanh.foodink.R;

/**
 * Created by Vinh Nguyen on 11/12/2018.
 */

public class ShipperOrderReceivedRejectFragment extends Fragment {
    private View rootView;
    private Button btnExit;
    private TextView tvMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.shipper_order_received_reject_fragment, container, false);

        btnExit = (Button) rootView.findViewById(R.id.btnExit);
        tvMessage = (TextView) rootView.findViewById(R.id.tvMessage);

        String message = getArguments().getString("Message");
        tvMessage.setText(message);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return rootView;
    }
}
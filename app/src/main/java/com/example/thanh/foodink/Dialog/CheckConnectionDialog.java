package com.example.thanh.foodink.Dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.thanh.foodink.R;

public class CheckConnectionDialog extends DialogFragment implements View.OnClickListener {
    private Button btnCancel, btnSetting;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_checkconnection, container);
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setCancelable(false);
        btnCancel = view.findViewById(R.id.btn_dialog_checkconnection_cancel);
        btnSetting = view.findViewById(R.id.btn_dialog_checkconnection_setting);
        btnSetting.setOnClickListener(this);
        btnSetting.setText("Ok");
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnCancel.getId()) {
            this.dismiss();
        }

        if (v.getId() == btnSetting.getId()) {
            this.dismiss();
//            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }
}

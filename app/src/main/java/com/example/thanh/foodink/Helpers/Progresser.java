package com.example.thanh.foodink.Helpers;

import android.app.ProgressDialog;
import android.content.Context;

public class Progresser {
    private ProgressDialog progress;

    public Progresser(Context context, String title, String content) {
        progress = new ProgressDialog(context);
        progress.setTitle(title);
        progress.setMessage(content);
        progress.setCancelable(false);
    }

    public void show() {
        progress.show();
    }

    public void hide() {
        progress.dismiss();
    }

    public void setTitle(String title) {
        progress.setTitle(title);
    }

    public void setContent(String title) {
        progress.setMessage(title);
    }
}

package com.example.smarthouse.util;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class DialogUtil {
    private final Activity activity;

    public DialogUtil(Activity activity) {
        this.activity = activity;
    }

    public void showError(String title, String message) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setCancelable(false)
                .show();
    }
}

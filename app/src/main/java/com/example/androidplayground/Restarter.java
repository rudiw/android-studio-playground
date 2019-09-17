package com.example.androidplayground;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class Restarter extends BroadcastReceiver {
    private static final String TAG = "Restarter";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Service tried to stop");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, ContentService.class));
        } else {
            context.startService(new Intent(context, ContentService.class));
        }
    }
}

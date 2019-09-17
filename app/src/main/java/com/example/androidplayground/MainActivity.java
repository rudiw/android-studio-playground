package com.example.androidplayground;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_READ_SMS = 1;

    private ContentService contentService;
    private Intent contentIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btnCheckAndReq = findViewById(R.id.btnCheckAndReq);
        btnCheckAndReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermission();
            }
        });

        final Button btnGetPhoneNumber = findViewById(R.id.btnGetPhoneNumber);
        btnGetPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( ContextCompat.checkSelfPermission(
                        MainActivity.this, Manifest.permission.READ_SMS)
                        == PackageManager.PERMISSION_GRANTED ) {

                    final TelephonyManager telephonyMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                    final String phoneNumber1 = telephonyMgr.getLine1Number();

                    Toast.makeText(getApplicationContext(),
                            "Phone number: " + phoneNumber1, Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please request Read Phone State permission first...", Toast.LENGTH_LONG).show();
                }
            }
        });

        this.contentService = new ContentService();
        this.contentIntent = new Intent(this, this.contentService.getClass());
        if (!isServiceRunning(this.contentService.getClass())) {
            startService(this.contentIntent);
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        final ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (final ActivityManager.RunningServiceInfo info :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (info.service.getClassName().equals(serviceClass.getName())) {
                Log.i(TAG, "Service " + serviceClass.getName() + " is running...");
                return true;
            }
        }
        Log.i(TAG, "Service " + serviceClass.getName() + " is NOT running...");
        return false;
    }

    @Override
    protected void onDestroy() {
        stopService(contentIntent);
        super.onDestroy();
    }

    private void checkAndRequestPermission() {
        Log.d("MainActivity", "check and request permission");
        if ( ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED ) {
            //not granted
            Log.d("MainActivity", "NOT granted...");

            if ( ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this,
                    Manifest.permission.READ_SMS) ) {
                Log.d("MainActivity", "should show request permission rationale...");
            } else {
                Log.d("MainActivity", "request permissions");

                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_SMS},
                        REQUEST_READ_SMS
                );
            }

        } else {
            //granted
            Toast.makeText(getApplicationContext(),
                    "Read Phone State permission is GRANTED", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_SMS) {
            if ( grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                Toast.makeText(getApplicationContext(),
                        "Read Phone State permission is GRANTED",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Read Phone State permission is NOT GRANTED!!!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}

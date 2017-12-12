package com.omni.omniguiderlocationsdk;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.omni.omnilocation.OGLocationService;

public class MainActivity extends AppCompatActivity implements OGLocationService.OGLocationListener {

    OGLocationService service;
    private static final int REQUEST_CODE_PERMISSIONS = 90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("@W@", "onCreate checkLocationService");
        checkLocationService();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        checkLocationService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (service != null) {
            Log.e("@W@", "unRegisterLocationService");
            service.unRegisterLocationService();
            service.destroy();
        }
    }

    private void registerService() {
        if (service == null) {
            service = new OGLocationService(this);
        }
        Log.e("@W@", "registerLocationService");
        service.registerLocationService(this);
    }

    @Override
    public void onLocationChanged(Location location, boolean isIndoor, float certainty) {
        Toast.makeText(MainActivity.this,
                "onLocationChanged lat : " + location.getLatitude() +
                        ", lng : " + location.getLongitude(),
                Toast.LENGTH_LONG).show();
        Log.e("@W@", "onLocationChanged lat : " + location.getLatitude() +
                ", lng : " + location.getLongitude());
    }

    @Override
    public void onEnterVenue(String venueId) {
        Log.e("@W@", "onEnterVenue venueId : " + venueId);
    }

    @Override
    public void onEnterFloor(String floorId) {
        Log.e("@W@", "onEnterFloor floorId : " + floorId);
        Toast.makeText(MainActivity.this,
                "onEnterFloor floorId : " + floorId,
                Toast.LENGTH_LONG).show();
    }

    private void checkLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e("@W@", "checkLocationService #1");
            ensurePermissions();
        } else {
            Log.e("@W@", "checkLocationService #2");
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("位置服務尚未開啟，請設定");
            dialog.setPositiveButton("open settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Toast.makeText(MainActivity.this, "沒有開啟位置服務，無法顯示正確位置", Toast.LENGTH_LONG).show();
                }
            });
            dialog.show();
        }
    }

    private void ensurePermissions() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.e("@W@", "ensurePermissions registerService #1");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_PERMISSIONS);

        } else {
            Log.e("@W@", "ensurePermissions registerService #2");
            registerService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            Log.e("@W@", "onRequestPermissionsResult #1");
            if (grantResults.length > 0) {

                boolean shouldRegetPermission = false;

                for (int result : grantResults) {
                    if (result != 0) {
                        shouldRegetPermission = true;
                        break;
                    }
                }

                if (shouldRegetPermission) {
                    Log.e("@W@", "onRequestPermissionsResult #2");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CHANGE_WIFI_STATE,
                                    Manifest.permission.ACCESS_WIFI_STATE,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_CODE_PERMISSIONS);
                } else {
                    Log.e("@W@", "onRequestPermissionsResult #3");
                    registerService();
                }

            }

        }
    }
}

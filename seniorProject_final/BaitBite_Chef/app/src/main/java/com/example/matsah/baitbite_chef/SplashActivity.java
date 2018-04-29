package com.example.matsah.baitbite_chef;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.matsah.baitbite_chef.GPS.GPSTracker;
import com.karan.churi.PermissionManager.PermissionManager;


public class SplashActivity extends AppCompatActivity {
    protected PermissionManager permissionnManager;
    private GPSTracker gpsTracker ;
    boolean IsGPSEnabled = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();





    }

}


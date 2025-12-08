package com.xploitspy.client;

import android.content.Context;
import android.provider.Settings;

/**
 * Konfigurasi Client
 * 
 * File ini akan di-generate oleh APK Builder
 * Ganti SERVER_URL dan SOCKET_URL dengan IP server Anda
 */
public class Config {
    // TODO: Ganti dengan IP server Anda
    // IP Server: 10.254.103.119 (auto-detected)
    public static final String SERVER_URL = "http://10.254.103.119:3000";
    public static final String SOCKET_URL = "http://10.254.103.119:3000";
    
    // Device ID akan di-generate secara otomatis
    public static String getDeviceId(Context context) {
        if (context == null) {
            return android.os.Build.SERIAL;
        }
        return Settings.Secure.getString(
            context.getContentResolver(),
            Settings.Secure.ANDROID_ID
        );
    }
}


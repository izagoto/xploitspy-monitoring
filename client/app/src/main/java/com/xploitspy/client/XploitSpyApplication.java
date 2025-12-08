package com.xploitspy.client;

import android.app.Application;
import com.google.firebase.FirebaseApp;

/**
 * Application class untuk initialize Firebase
 */
public class XploitSpyApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Firebase
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
    }
}


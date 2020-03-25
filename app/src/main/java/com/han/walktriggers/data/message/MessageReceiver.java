package com.han.walktriggers.data.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.util.ArrayList;

public class MessageReceiver extends BroadcastReceiver {
    // not all

    // framework background
    // manage triggers, add them easily
    // manage data source
    // manage notification

    //todo other apps has context provider
    // access;

    // config file? data source
    // different data source -> complex
    // more api -> improve
    // different -> don't call trigger

    // more complex notifications (images, buttons...)
    // step histories, less, more -> notification

    // 5 triggers
    // in report -> screen shots -> how the trigger works
    // highlight ->

    // behaviour changes
    // -> different user types -> type of triggers
    // a lot of | often miss targets
    // pick the right notification

    // low power -> save power -> don't check weather



    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "got it", Toast.LENGTH_SHORT).show();
        // todo task intent can be called
        // Get intent, action and MIME type
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // todo maybe get now step
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }
}

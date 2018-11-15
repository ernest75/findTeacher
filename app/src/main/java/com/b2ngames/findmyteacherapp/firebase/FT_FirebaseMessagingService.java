package com.b2ngames.findmyteacherapp.firebase;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.b2ngames.findmyteacherapp.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Ernest on 30/09/2017.
 */

public class FT_FirebaseMessagingService extends FirebaseMessagingService {

    private static final String LOG_TAG = FT_FirebaseMessagingService.class.getSimpleName();;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
    final RemoteMessage message = remoteMessage;
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(LOG_TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            Log.e(LOG_TAG, "Message data payload: " + remoteMessage.getData());

            JSONObject json = null;
            try {
                json = new JSONObject(remoteMessage.getData().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendPushNotification(json);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(LOG_TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log
        Log.e(LOG_TAG, "Notification JSON " + json.toString());
        try {
            //parsing json data
            String title = json.getString("title");
            String message = json.getString("message");
            String imageUrl = json.getString("image");

            //creating MyNotificationManager object
            FT_NotificationManager mNotificationManager = new FT_NotificationManager(getApplicationContext());

            //creating an intent for the notification,
            //// TODO: 09/10/2017 Change MainActivity class to activity with messages
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            //if there is no image
            if(imageUrl.equals("null")){
                //displaying small notification
                mNotificationManager.showSmallNotification(title, message, intent);
            }else{
                //if there is an image
                //displaying a big notification
                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception: " + e.getMessage());
        }

    }

}


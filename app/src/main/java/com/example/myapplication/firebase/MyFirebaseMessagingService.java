package com.example.myapplication.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;
import com.example.myapplication.user_interface.launcher.NavigationActivity;
import com.example.myapplication.helper.SharedPrefManager;
import com.example.myapplication.Constant;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // (Developer)Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        SharedPrefManager prefManager = new SharedPrefManager(this);
        boolean userIsLoggedIn = prefManager.getIsUserLoggedIn();

        if(userIsLoggedIn){
            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.e(TAG, "Message data payload: " + remoteMessage.getData());

                Map<String, String> dataMap = remoteMessage.getData();
                sendNotification(dataMap.get("message"));

                if (/* Check if data needs to be processed by long running job */ true) {
                    // For long-running tasks (10 seconds or more) use WorkManager.
                    //   scheduleJob();
                } else {
                    // Handle message within 10 seconds
                    handleNow();
                }

            }
        }

//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            sendNotification(remoteMessage.getNotification().getBody());
//        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]
    /**
     * Called if FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve
     * the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.e(TAG, "Refreshed token: " + token);
        SharedPrefManager prefManager = new SharedPrefManager(this);
        prefManager.setFcmToken(token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        // sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
//    private void scheduleJob() {
//        // [START dispatch_job]
//        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
//                .build();
//        WorkManager.getInstance().beginWith(work).enqueue();
//        // [END dispatch_job]
//    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.e(TAG, "Short lived task is done.");

    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any
     * server-side account maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        //  Implement this method to send token to your app server.
        Log.e("FCM TOKEN", token);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Log.e(TAG,"sendNotification CALLED");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.NOTIFICATION_RECEIVED, true);

        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, new Random().nextInt() /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_stat_notification)
                     // .setContentTitle(getString(R.string.fcm_message))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setSound(defaultSoundUri)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(createID() /* ID of notification */, notificationBuilder.build());
    }

    public int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        Log.e(TAG, "createId = " + String.valueOf(id));
        return id;
    }
}

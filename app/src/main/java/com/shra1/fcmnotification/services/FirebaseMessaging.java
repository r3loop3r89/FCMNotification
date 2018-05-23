package com.shra1.fcmnotification.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shra1.fcmnotification.MainActivity;
import com.shra1.fcmnotification.R;

import static com.trenzlr.firebasenotificationhelper.Constants.KEY_TEXT;

public class FirebaseMessaging extends FirebaseMessagingService {
    Context context;
    int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
    private NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        context = getApplicationContext();

        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                101,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );


        if (notificationManager == null) {

            notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        String CHANNEL_ID = "MyChannel";
        String name = "Shra1Channel";
        String description = "MyDesc";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            //Register the channel with the system
            notificationManager.createNotificationChannel(channel);
        }

        String title = remoteMessage.getFrom();
        String message = remoteMessage.getData().get(KEY_TEXT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_andy)
                .setContentTitle("From : " + title)
                .setContentText("Message : " + message)
                .setPriority(importance);

        notificationManager.notify(45, mBuilder.build());
    }

}
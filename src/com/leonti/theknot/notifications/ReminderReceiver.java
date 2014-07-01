package com.leonti.theknot.notifications;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
	
	String title = intent.getStringExtra("title");
	showNotification("Reminder", title, context);
    }

    private void showNotification(String title, String text, Context context) {
	NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
		.setSmallIcon(android.R.drawable.alert_light_frame)
		.setContentTitle(title)
		.setContentText(text)
		.setDefaults(Notification.DEFAULT_ALL);

	NotificationManager mNotificationManager = (NotificationManager) context
		.getSystemService(Context.NOTIFICATION_SERVICE);
	
	
	mNotificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }

}

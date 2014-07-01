package com.leonti.theknot.notifications;

import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.leonti.theknot.model.Reminder;

public class ReminderNotification {

    public static void scheduleNotifications(List<Reminder> reminders, Context context) {
	
	for (Reminder reminder : reminders) {
	    scheduleNotification(reminder, context);
	}
	
    }
    
    public static void scheduleNotification(Reminder reminder, Context context) {

	// remove reminder first in case of update
	removeNotification(reminder, context);
	
	PendingIntent pendingIntent = createIntent(reminder, context);
	
	AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	alarmManager.set(AlarmManager.RTC_WAKEUP, reminder.getTime().getWhen(), pendingIntent);
    }

    public static void removeNotification(Reminder reminder, Context context) {
	PendingIntent pendingIntent = createIntent(reminder, context);
	
	AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	alarmManager.cancel(pendingIntent);
    }
    
    private static PendingIntent createIntent(Reminder reminder, Context context) {
	Intent intent = new Intent(context, ReminderReceiver.class);
	intent.setAction("FAKE_ACTION_" + reminder.getId());
	
	intent.putExtra("title", reminder.getTitle());
	
	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
	return pendingIntent;
    }
    
}

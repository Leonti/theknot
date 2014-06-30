package com.leonti.theknot.dao;

import static com.leonti.theknot.dao.ReminderSqliteHelper.COLUMN_DESCRIPTION;
import static com.leonti.theknot.dao.ReminderSqliteHelper.COLUMN_ID;
import static com.leonti.theknot.dao.ReminderSqliteHelper.COLUMN_LOCATION_LATITUDE;
import static com.leonti.theknot.dao.ReminderSqliteHelper.COLUMN_LOCATION_LONGITUDE;
import static com.leonti.theknot.dao.ReminderSqliteHelper.COLUMN_TIME_WHEN;
import static com.leonti.theknot.dao.ReminderSqliteHelper.COLUMN_TITLE;
import static com.leonti.theknot.dao.ReminderSqliteHelper.COLUMN_TYPE;
import static com.leonti.theknot.dao.ReminderSqliteHelper.TABLE_REMINDERS;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.leonti.theknot.model.Reminder;

public class ReminderDao {

    public interface OnReminderChanged {
	void onChange();
    }

    // Database fields
    private SQLiteDatabase database;
    private ReminderSqliteHelper dbHelper;
    private List<OnReminderChanged> onReminderChangedCallbacks;
    
    public ReminderDao(Context context) {
	dbHelper = new ReminderSqliteHelper(context);
    }    

    public void open() throws SQLException {
	database = dbHelper.getWritableDatabase();
	onReminderChangedCallbacks = new LinkedList<>();
    }

    public void close() {
	dbHelper.close();
	onReminderChangedCallbacks = null;
    }
    
    public void addOnReminderChangedCallback(OnReminderChanged onReminderChangedCallback) {
	onReminderChangedCallbacks.add(onReminderChangedCallback);
    }  
    
    private void notifyCallbacks() {
	for (OnReminderChanged onReminderChangedCallback : onReminderChangedCallbacks) {
	    onReminderChangedCallback.onChange();
	}
    }

    public Reminder save(Reminder reminder) {

	ContentValues values = toContentValues(reminder);
	long insertId = database.insert(TABLE_REMINDERS, null, values);

	Cursor cursor = database.query(TABLE_REMINDERS, null, ReminderSqliteHelper.COLUMN_ID + " = " + insertId, null,
		null, null, null);
	cursor.moveToFirst();
	Reminder savedReminder = fromCursor(cursor);
	cursor.close();
	
	notifyCallbacks();
	return savedReminder;
    }

    private ContentValues toContentValues(Reminder reminder) {
	ContentValues values = new ContentValues();

	values.put(COLUMN_TITLE, reminder.getTitle());
	values.put(COLUMN_DESCRIPTION, reminder.getDescription());
	values.put(COLUMN_TYPE, reminder.getType().name());
	values.put(COLUMN_TIME_WHEN, reminder.getType() == Reminder.Type.TIME ? reminder.getTime().getWhen() : null);
	values.put(COLUMN_LOCATION_LATITUDE, reminder.getType() == Reminder.Type.LOCATION ? reminder.getLocation()
		.getLatitude() : null);
	values.put(COLUMN_LOCATION_LONGITUDE, reminder.getType() == Reminder.Type.LOCATION ? reminder.getLocation()
		.getLongitude() : null);

	return values;
    }

    private Reminder fromCursor(Cursor cursor) {

	long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
	String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
	String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
	Reminder.Type type = Reminder.Type.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));

	if (type == Reminder.Type.TIME) {
	    return new Reminder(id, title, description, new Reminder.Time(cursor.getLong(cursor
		    .getColumnIndex(COLUMN_TIME_WHEN))), false);

	} else if (type == Reminder.Type.LOCATION) {
	    return new Reminder(id, title, description, new Reminder.Location(cursor.getDouble(cursor
		    .getColumnIndex(COLUMN_LOCATION_LATITUDE)), cursor.getDouble(cursor
		    .getColumnIndex(COLUMN_LOCATION_LONGITUDE))), false);
	}

	throw new RuntimeException("Reminder is of unknown type");
    }

    public List<Reminder> readFuture() {
	List<Reminder> reminders = new LinkedList<>();

	Cursor cursor = database.rawQuery(
		"SELECT * FROM " + TABLE_REMINDERS + " WHERE " + COLUMN_TIME_WHEN + " >= '" + now() + "' ORDER BY " + COLUMN_TIME_WHEN + " ASC", null);

	cursor.moveToFirst();
	while (!cursor.isAfterLast()) {
	    reminders.add(fromCursor(cursor));
	    cursor.moveToNext();
	}

	cursor.close();
	return reminders;
    }

    public List<Reminder> readPast() {
	List<Reminder> reminders = new LinkedList<>();

	Cursor cursor = database.rawQuery(
		"SELECT * FROM " + TABLE_REMINDERS + " WHERE " + COLUMN_TIME_WHEN + " < '" + now() + "' ORDER BY " + COLUMN_TIME_WHEN + " ASC", null);

	cursor.moveToFirst();
	while (!cursor.isAfterLast()) {
	    reminders.add(fromCursor(cursor));
	    cursor.moveToNext();
	}

	cursor.close();
	return reminders;
    }
    
    private long now() {
	return System.currentTimeMillis();
    }

    public void remove(Long id) {
	database.execSQL("DELETE FROM " + TABLE_REMINDERS + " WHERE " + COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
    }

}

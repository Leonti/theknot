package com.leonti.theknot.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ReminderSqliteHelper extends SQLiteOpenHelper {

	public static final String TABLE_REMINDERS = "reminders";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_TIME_WHEN = "time_when";
	public static final String COLUMN_LOCATION_LATITUDE = "location_latitude";
	public static final String COLUMN_LOCATION_LONGITUDE = "location_longitude";

	private static final String DATABASE_NAME = "reminders.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ TABLE_REMINDERS + "(" + COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ COLUMN_TITLE + " TEXT NOT NULL,"
			+ COLUMN_DESCRIPTION + " TEXT NOT NULL,"
			+ COLUMN_TYPE + " TEXT NOT NULL,"
			+ COLUMN_TIME_WHEN + " INTEGER,"
			+ COLUMN_LOCATION_LATITUDE + " REAL,"
			+ COLUMN_LOCATION_LONGITUDE + " REAL"
			+ ");";

	public ReminderSqliteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(ReminderSqliteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
		onCreate(db);
	}
}

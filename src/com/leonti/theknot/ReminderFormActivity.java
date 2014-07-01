package com.leonti.theknot;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.leonti.theknot.DateSelectionAdapter.GetLabel;
import com.leonti.theknot.dao.ReminderDao;
import com.leonti.theknot.model.Reminder;
import com.leonti.theknot.notifications.ReminderNotification;

public class ReminderFormActivity extends Activity {

    private static String TAG = ReminderFormActivity.class.toString();

    private ReminderDao reminderDao;
    private Spinner dateSpinner;
    private Spinner timeSpinner;

    // need to be persisted
    private Day day;
    private TimeOfDay timeOfDay;

    @SuppressWarnings("serial")
    private static class Day implements Serializable {

	@SuppressLint("SimpleDateFormat")
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

	public final int year;
	public final int month;
	public final int dayOfMonth;

	public Day(int year, int month, int day) {
	    this.year = year;
	    this.month = month;
	    this.dayOfMonth = day;
	}

	public String format() {
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(year, month, dayOfMonth);

	    return dateFormat.format(calendar.getTime());
	}
    }

    @SuppressWarnings("serial")
    private static class TimeOfDay implements Serializable {
	public final int hour;
	public final int minute;
	private final boolean nextDay;

	public TimeOfDay(int hour, int minute, boolean nextDay) {
	    this.hour = hour;
	    this.minute = minute;
	    this.nextDay = nextDay;
	}

	public String format() {
	    return String.format("%02d", hour) + ":" + String.format("%02d", minute);
	}
    }

    private enum SelectedDay {
	TODAY("Today"), TOMORROW("Tomorrow"), SET_DATE("Set date ...");

	private final String caption;

	SelectedDay(String caption) {
	    this.caption = caption;
	}

	public String getCaption() {
	    return caption;
	}
    }

    private enum SelectedTime {
	IN_3_HOURS("In 3 hours"), NOON("Noon"), SET_TIME("Set time ...");

	private final String caption;

	private SelectedTime(String caption) {
	    this.caption = caption;
	}

	public String getCaption() {
	    return caption;
	}
    }

    private SelectedDay[] daysToSelect = new SelectedDay[] { SelectedDay.TODAY, SelectedDay.TOMORROW,
	    SelectedDay.SET_DATE };

    private SelectedTime[] fromSelectedDay(SelectedDay selectedDay) {

	if (selectedDay == SelectedDay.TODAY) {
	    return new SelectedTime[] { SelectedTime.IN_3_HOURS, SelectedTime.SET_TIME };
	} else {
	    return new SelectedTime[] { SelectedTime.NOON, SelectedTime.SET_TIME };
	}
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
	super.onSaveInstanceState(outState);
	Log.i(TAG, "Saving");
	Log.i(TAG, "Date: " + day.format());
	Log.i(TAG, "time: " + timeOfDay.format());
	outState.putSerializable("day", day);
	outState.putSerializable("timeOfDay", timeOfDay);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.reminder_form);

	if (savedInstanceState != null) {
	    day = (Day) savedInstanceState.get("day");
	    timeOfDay = (TimeOfDay) savedInstanceState.get("timeOfDay");
	} else {
	    day = toDay(Calendar.getInstance());
	    timeOfDay = new TimeOfDay(12, 0, false);
	}

	Log.i(TAG, "Time of day: " + timeOfDay.format());
	Log.i(TAG, "Date: " + day.format());

	reminderDao = new ReminderDao(this);
	reminderDao.open();

	dateSpinner = (Spinner) findViewById(R.id.reminder_date);
	final List<String> dateSpinnerItems = new LinkedList<>();
	for (SelectedDay dayToSelect : daysToSelect) {
	    dateSpinnerItems.add(dayToSelect.getCaption());
	}

	dateSpinner.setAdapter(createSpinnerAdapter(dateSpinnerItems, new GetLabel() {

	    @Override
	    public String get(int position) {
		if (position == dateSpinnerItems.size() - 1) {
		    return day.format();
		} else {
		    return dateSpinnerItems.get(position);
		}
	    }
	}));

	dateSpinner.post(new Runnable() {

	    @Override
	    public void run() {

		dateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

		    @Override
		    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

			SelectedDay selectedDay = daysToSelect[position];

			if (selectedDay == SelectedDay.TODAY) {
			    day = toDay(Calendar.getInstance());
			} else if (selectedDay == SelectedDay.TOMORROW) {
			    Calendar calendar = Calendar.getInstance();
			    calendar.add(Calendar.DAY_OF_YEAR, 1);
			    day = toDay(calendar);
			} else if (selectedDay == SelectedDay.SET_DATE) {

			    Log.i(TAG, "Showing date set dialog");

			    DatePickerDialog dialog = new DatePickerDialog(ReminderFormActivity.this,
				    datePickerListener, day.year, day.month, day.dayOfMonth);
			    dialog.show();
			}

			updateTimeSpinner();
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});

	    }

	});

	timeSpinner = (Spinner) findViewById(R.id.reminder_time);

	timeSpinner.post(new Runnable() {

	    @Override
	    public void run() {

		final List<String> timeSpinnerItems = getTimeSpinnerItems();
		timeSpinner.setAdapter(createSpinnerAdapter(timeSpinnerItems, createTimeSpinnerGet(timeSpinnerItems)));

		timeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

		    @Override
		    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

			SelectedTime selectedTime = fromSelectedDay(getSelectedDay())[position];

			if (selectedTime == SelectedTime.IN_3_HOURS) {
			    Calendar calendar = Calendar.getInstance();
			    int dayBefore = calendar.get(Calendar.DAY_OF_MONTH);
			    calendar.add(Calendar.HOUR_OF_DAY, 3);
			    int dayAfter = calendar.get(Calendar.DAY_OF_MONTH);
			    timeOfDay = new TimeOfDay(calendar.get(Calendar.HOUR_OF_DAY),
				    calendar.get(Calendar.MINUTE), dayBefore != dayAfter);
			} else if (selectedTime == SelectedTime.NOON) {
			    timeOfDay = new TimeOfDay(12, 0, false);
			} else if (selectedTime == SelectedTime.SET_TIME) {
			    TimePickerDialog dialog = new TimePickerDialog(ReminderFormActivity.this,
				    timePickerListener, timeOfDay.hour, timeOfDay.minute, true);
			    dialog.show();
			}
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
	    }
	});

	Button okButton = (Button) findViewById(R.id.button_ok);

	okButton.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, day.year);
		calendar.set(Calendar.MONTH, day.month);
		calendar.set(Calendar.DAY_OF_MONTH, day.dayOfMonth);
		calendar.set(Calendar.HOUR_OF_DAY, timeOfDay.hour);
		calendar.set(Calendar.MINUTE, timeOfDay.minute);

		if (timeOfDay.nextDay) {
		    calendar.add(Calendar.DAY_OF_YEAR, 1);
		}

		Log.i(TAG, "TIME IS: " + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.MONTH) + " "
			+ calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + " "
			+ calendar.get(Calendar.MINUTE));

		EditText editTextTitle = (EditText) findViewById(R.id.reminder_title);
		String title = editTextTitle.getText().toString().trim();

		Reminder reminder = new Reminder(null, title, "", new Reminder.Time(calendar.getTimeInMillis()), false);

		Reminder savedReminder = reminderDao.save(reminder);

		Log.i(TAG, "Reminder saved with id: " + savedReminder.getId());
		Intent returnIntent = new Intent();
		returnIntent.putExtra("id", savedReminder.getId());

		ReminderNotification.scheduleNotification(savedReminder, ReminderFormActivity.this);
		
		setResult(RESULT_OK, returnIntent);
		finish();
	    }
	});

	Button cancelButton = (Button) findViewById(R.id.button_cancel);

	cancelButton.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
		finish();
	    }
	});
    }

    private Day toDay(Calendar calendar) {
	return new Day(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private ArrayAdapter<String> createSpinnerAdapter(final List<String> spinnerItems, GetLabel getLabel) {

	ArrayAdapter<String> spinnerAdapter = new DateSelectionAdapter(this, android.R.layout.simple_spinner_item,
		spinnerItems, getLabel);
	spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	return spinnerAdapter;
    }

    private List<String> getTimeSpinnerItems() {
	List<String> timeSpinnerItems = new LinkedList<>();
	for (SelectedTime timeToSelect : fromSelectedDay(getSelectedDay())) {
	    timeSpinnerItems.add(timeToSelect.getCaption());
	}
	return timeSpinnerItems;
    }

    private SelectedDay getSelectedDay() {
	return daysToSelect[dateSpinner.getSelectedItemPosition()];
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

	public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

	    Log.i(TAG, "On date set");

	    day = new Day(selectedYear, selectedMonth, selectedDay);

	    @SuppressWarnings("unchecked")
	    ArrayAdapter<String> adapter = (ArrayAdapter<String>) dateSpinner.getAdapter();

	    adapter.notifyDataSetChanged();
	}
    };

    private void updateTimeSpinner() {
	List<String> timeSpinnerItems = getTimeSpinnerItems();
	ArrayAdapter<String> timeSpinnerAdapter = createSpinnerAdapter(timeSpinnerItems,
		createTimeSpinnerGet(timeSpinnerItems));
	timeSpinner.setAdapter(timeSpinnerAdapter);
	timeSpinnerAdapter.notifyDataSetChanged();
    }

    private GetLabel createTimeSpinnerGet(final List<String> timeSpinnerItems) {
	return new GetLabel() {
	    @Override
	    public String get(int position) {
		if (position == timeSpinnerItems.size() - 1) {
		    return timeOfDay.format();
		} else {
		    return timeSpinnerItems.get(position);
		}
	    }
	};
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

	    timeOfDay = new TimeOfDay(hourOfDay, minute, false);

	    @SuppressWarnings("unchecked")
	    ArrayAdapter<String> adapter = (ArrayAdapter<String>) timeSpinner.getAdapter();

	    adapter.notifyDataSetChanged();
	}
    };

    @Override
    protected void onResume() {
	reminderDao.open();
	super.onResume();
    }

    @Override
    protected void onPause() {
	reminderDao.close();
	super.onPause();
    }
}
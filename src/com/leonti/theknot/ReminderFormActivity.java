package com.leonti.theknot;

import com.leonti.theknot.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

public class ReminderFormActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminder_form);

		final Spinner dateSpinner = (Spinner) findViewById(R.id.reminder_date);
		String[] items = new String[] { "One", "Two", "Three" };
		ArrayAdapter<String> dateSpinnerAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, items);
		dateSpinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dateSpinner.setAdapter(dateSpinnerAdapter);

		dateSpinner.post(new Runnable() {

			@Override
			public void run() {
				dateSpinner
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> parent,
									View view, int position, long id) {
								DatePickerDialog dialog = new DatePickerDialog(
										ReminderFormActivity.this,
										datePickerListener, 2000, 1, 1);
								dialog.show();
							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {
							}
						});
			}

		});

		final Spinner timeSpinner = (Spinner) findViewById(R.id.reminder_time);
		String[] timeSpinnerItems = new String[] { "One", "Two", "Three" };
		ArrayAdapter<String> timeSpinnerAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, timeSpinnerItems);
		timeSpinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		timeSpinner.setAdapter(timeSpinnerAdapter);

		timeSpinner.post(new Runnable() {

			@Override
			public void run() {
				timeSpinner
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> parent,
									View view, int position, long id) {
								TimePickerDialog dialog = new TimePickerDialog(
										ReminderFormActivity.this,
										timePickerListener, 11, 12, true);
								dialog.show();
							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {
							}
						});
			}
		});

	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			// Do whatever you want
		}
	};

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub

		}
	};
}
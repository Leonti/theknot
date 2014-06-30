package com.leonti.theknot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.leonti.theknot.model.Reminder;

public class ReminderListAdapter extends BaseAdapter {

    @SuppressLint("SimpleDateFormat")
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    
    private final Context context;
    private List<Reminder> reminders;

    public ReminderListAdapter(Context context, List<Reminder> reminders) {
	this.context = context;
	this.reminders = reminders;
    }
    
    public void setReminders(List<Reminder> reminders) {
	this.reminders = reminders;
	notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

	if (convertView == null) {
	    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    convertView = inflater.inflate(R.layout.list_view_item, parent, false);
	}

	Reminder reminder = reminders.get(position);
	TextView firstLine = (TextView) convertView.findViewById(R.id.first_line);
	firstLine.setText(reminder.getTitle());

	TextView secondLine = (TextView) convertView.findViewById(R.id.second_line);
	String date = dateFormat.format(new Date(reminder.getTime().getWhen()));
	secondLine.setText(date);
	
	return convertView;
    }

    @Override
    public int getCount() {
	return reminders.size();
    }

    @Override
    public Object getItem(int position) {
	return reminders.get(position);
    }

    @Override
    public long getItemId(int position) {
	return reminders.get(position).getId();
    }
}

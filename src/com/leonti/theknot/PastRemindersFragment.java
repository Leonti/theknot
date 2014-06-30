package com.leonti.theknot;

import java.util.List;

import com.leonti.theknot.model.Reminder;

public class PastRemindersFragment extends AbstractRemindersFragment {

    @Override
    protected List<Reminder> readReminders() {
	
	return getReminderDao().readPast();
    }
   
}

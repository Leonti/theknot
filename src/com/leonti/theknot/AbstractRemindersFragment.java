package com.leonti.theknot;

import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.leonti.theknot.dao.ReminderDao;
import com.leonti.theknot.model.Reminder;

public abstract class AbstractRemindersFragment extends Fragment {
    
    abstract List<Reminder> readReminders();

    private ReminderDao reminderDao; 
    private ReminderListAdapter reminderListAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
	reminderDao = new ReminderDao(getActivity());
	reminderDao.open();
	
        View rootView = inflater.inflate(R.layout.fragment_future_reminders, container, false);        
        ListView listView = (ListView) rootView.findViewById(R.id.list_reminders);
        reminderListAdapter = new ReminderListAdapter(getActivity(), readReminders());
        listView.setAdapter(reminderListAdapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		String actions[] = new String[] {"Delete"};

		final Reminder reminder = (Reminder) reminderListAdapter.getItem(position);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(reminder.getTitle());
		builder.setItems(actions, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int position) {
		        
			reminderDao.remove(reminder.getId());
			reminderListAdapter.setReminders(readReminders());
		    }
		});
		builder.show();		
	    }
	});
        
        return rootView;
    }
    
    @Override
    public void onResume() {
	super.onResume();
	reminderDao.open();
	reminderListAdapter.setReminders(readReminders());
    }

    @Override
    public void onPause() {
	super.onPause();
	reminderDao.close();
    }
    
    protected ReminderDao getReminderDao() {
	return reminderDao;
    }

    public void refreshReminders() {
	if (reminderListAdapter != null) {
	    reminderDao.open();
	    reminderListAdapter.setReminders(readReminders());
	    reminderDao.close();
	}
    }
    
}

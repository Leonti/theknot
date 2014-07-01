package com.leonti.theknot;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.leonti.theknot.dao.ReminderDao;
import com.leonti.theknot.notifications.ReminderNotification;

public class MainActivity extends Activity implements ActionBar.TabListener {
    private static String TAG = MainActivity.class.toString();

    private static int ADD_REMINDER_REQUEST = 1;

    private ViewPager viewPager;
    private TabsPagerAdapter tabsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	final ActionBar actionBar = getActionBar();
	actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	tabsPagerAdapter = new TabsPagerAdapter(getFragmentManager());
	viewPager = (ViewPager) findViewById(R.id.pager);
	viewPager.setAdapter(tabsPagerAdapter);

	viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
	    @Override
	    public void onPageSelected(int position) {
		actionBar.setSelectedNavigationItem(position);
	    }
	});

	for (int i = 0; i < tabsPagerAdapter.getCount(); i++) {
	    actionBar.addTab(actionBar.newTab().setText(tabsPagerAdapter.getPageTitle(i)).setTabListener(this));
	}

	scheduleFutureNotifications();
    }

    private void scheduleFutureNotifications() {
	ReminderDao reminderDao = new ReminderDao(this);
	reminderDao.open();
	ReminderNotification.scheduleNotifications(reminderDao.readFuture(), this);
	reminderDao.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

	getMenuInflater().inflate(R.menu.main, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

	switch (item.getItemId()) {

	case R.id.action_settings:
	    return true;

	case R.id.action_add:
	    openReminderFormActivity();
	    return true;
	}

	return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    private void openReminderFormActivity() {
	Intent intent = new Intent(this, ReminderFormActivity.class);
	startActivityForResult(intent, ADD_REMINDER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (requestCode == ADD_REMINDER_REQUEST) {
	    if (resultCode == RESULT_OK) {

		Long id = data.getLongExtra("id", -1);

		Log.i(TAG, "Returned id is " + id);
		tabsPagerAdapter.refreshReminders();
	    }
	}
    }
}

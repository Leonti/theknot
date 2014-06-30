package com.leonti.theknot;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private FutureRemindersFragment futureRemindersFragment;
    private PastRemindersFragment pastRemindersFragment;

    public TabsPagerAdapter(FragmentManager fm) {
	super(fm);

	futureRemindersFragment = new FutureRemindersFragment();
	pastRemindersFragment = new PastRemindersFragment();
    }

    private String[] tabs = new String[] { "Future", "Past" };

    @Override
    public Fragment getItem(int index) {

	switch (index) {
	case 0:
	    return futureRemindersFragment;
	case 1:
	    return pastRemindersFragment;
	}

	return null;
    }

    @Override
    public String getPageTitle(int position) {
	return tabs[position];
    }

    @Override
    public int getCount() {
	return 2;
    }

    public void refreshReminders() {
	futureRemindersFragment.refreshReminders();
	pastRemindersFragment.refreshReminders();
    }

}

package com.leonti.theknot;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DateSelectionAdapter extends ArrayAdapter<String> {

    interface GetLabel {
	String get(int position);
    }

    private final GetLabel getLabel;
    
    public DateSelectionAdapter(Context ctx, int txtViewResourceId, List<String> objects, GetLabel getLabel) {
	super(ctx, txtViewResourceId, objects);
	this.getLabel = getLabel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

	View view = super.getView(position, convertView, parent);
	TextView textView = (TextView) view.findViewById(android.R.id.text1);

	textView.setText(getLabel.get(position));

	return view;
    }

}

package com.example.schedule;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class EventActivity extends Activity {

	private String eventId;
	private String eventName;
	private String eventContent;
	private Calendar calFrom;
	private Calendar calTo;
	private String photo;
	private String record;
	public EventActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent gainIntent = getIntent();
		eventId = gainIntent.getStringExtra("eventId");
		eventName = gainIntent.getStringExtra("eventName");
		eventContent = gainIntent.getStringExtra("eventContent");
		photo = gainIntent.getStringExtra("photo");
		record = gainIntent.getStringExtra("record");
		long calFromInMillis = gainIntent.getLongExtra("calFrom", 0);
		calFrom.setTimeInMillis(calFromInMillis);
		long calToInMillis = gainIntent.getLongExtra("calTo", 0);
		calTo.setTimeInMillis(calToInMillis);
		
	}

}

package com.example.schedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DateActivity extends Activity {

	private TextView dateTextView;
	public DateActivity(){
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_date);
		Intent gainIntent = getIntent(); 
		String date =gainIntent.getIntExtra("year",1990) + "-" 
				+ gainIntent.getIntExtra("month",5) + "-" 
				+ gainIntent.getIntExtra("dayOfMonth",10);
		dateTextView = (TextView)findViewById(R.id.tv_date);
		dateTextView.setText(date);
		
	}

}

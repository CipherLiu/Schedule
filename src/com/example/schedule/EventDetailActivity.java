package com.example.schedule;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class EventDetailActivity extends Activity {
	private ImageView []profile=new ImageView[8];
	private ImageButton previous;
	private ImageButton next;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_detail);
		//Debug info
		//System.out.println(getIntent().getStringExtra("whichGroup"));
		//Change title to display the group info
		setTitle("Group "+getIntent().getStringExtra("whichGroup"));
		//Find ImageView
		profile[1]=(ImageView) findViewById(R.id.profile1_event_detail);
		profile[2]=(ImageView) findViewById(R.id.profile2_event_detail);
		profile[3]=(ImageView) findViewById(R.id.profile3_event_detail);
		profile[4]=(ImageView) findViewById(R.id.profile4_event_detail);
		profile[5]=(ImageView) findViewById(R.id.profile5_event_detail);
		profile[6]=(ImageView) findViewById(R.id.profile6_event_detail);
		/*
		 * Test operations
		 * execute a AsyncTask here to get data from the server
		 */
		profile[1].setBackgroundResource(R.drawable.no_photo_small);
		profile[2].setBackgroundResource(R.drawable.no_photo_small);
		profile[3].setBackgroundResource(R.drawable.no_photo_small);
		profile[4].setBackgroundResource(R.drawable.no_photo_small);
		profile[5].setBackgroundResource(R.drawable.no_photo_small);
		profile[6].setBackgroundResource(R.drawable.no_photo_small);
		//For display the previous and next button
		previous = (ImageButton)findViewById(R.id.previous_event_detail);
		next = (ImageButton)findViewById(R.id.next_event_detail);
		DrawView dr = (DrawView)this.findViewById(R.id.drawView_event_detail);
		dr.setUserId(getIntent().getStringExtra("whichGroup"));
		previous.setOnClickListener(new ImageButton.OnClickListener(){

			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//System.out.println("Previous touched");
			}
			
		});
		next.setOnClickListener(new ImageButton.OnClickListener(){

			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//System.out.println("Next touched");
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_event_detail, menu);
		return true;
	}

}

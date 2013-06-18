package com.example.schedule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class MeFragment extends Fragment{


	private int iFirstDayOfWeek = Calendar.SUNDAY;
	private ScheduleCalendarView scheduleCalendarView ;
	private Calendar calSelected = Calendar.getInstance();
	private String userId = "";
	private boolean hasEventArray[] = new boolean[42];
	public MeFragment() {
	}
	
	public Calendar getCalSelected() {
		calSelected.setTimeInMillis(scheduleCalendarView.getCalSelected().getTimeInMillis());
		return calSelected;
	}
	public void setCalSelected(Calendar calSelected) {
		this.calSelected = calSelected;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userId = this.getArguments().getString("userId");
		hasEventArray = getArguments().getBooleanArray("hasEventArray");
		scheduleCalendarView = 
				new ScheduleCalendarView(MeFragment.this.getActivity(),hasEventArray);
		Calendar dateToCheck = scheduleCalendarView.updateStartDateForMonth();
		dateToCheck.set(Calendar.MILLISECOND, 0);
		dateToCheck.set(Calendar.SECOND, 0);
		dateToCheck.set(Calendar.MINUTE, 0);
		dateToCheck.set(Calendar.HOUR_OF_DAY, 0);

		String calString = dateToCheck.getTimeInMillis()+"";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) { 
		return this.scheduleCalendarView.generateContentViewPortrait();
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(scheduleCalendarView.getMonthTextView() == null){
			this.scheduleCalendarView.generateContentViewPortrait();
			update(calSelected);
		}else{
			update(calSelected);
		}
	}

	public void update(Calendar calSelected){
		scheduleCalendarView.update();
	}
	

}

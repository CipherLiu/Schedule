package com.example.schedule;

import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MeFragment extends Fragment{

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
				new ScheduleCalendarView(MeFragment.this.getActivity(),hasEventArray,userId);
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
		scheduleCalendarView.setCalSelected(calSelected);
		scheduleCalendarView.update();
	}
	public void update(boolean[] hasEventArray){
		scheduleCalendarView.setHasEventArray(hasEventArray);
		scheduleCalendarView.update();
	}
	public void update(Calendar calSelected,boolean[] hasEventArray){
		scheduleCalendarView.setCalSelected(calSelected);
		scheduleCalendarView.setHasEventArray(hasEventArray);
		scheduleCalendarView.update();
	}
}

package com.example.schedule;

import java.util.ArrayList;
import java.util.Calendar;

public class EventInfoForContactFrag {
	private String userId;
	private String userProfileUrl;
	private ArrayList<String> eventId;
	private String eventName;
	private Calendar calFrom;
	private Calendar calTo;
	public EventInfoForContactFrag()
	{
		this.userId="";
		this.userProfileUrl="";
		this.eventId=null;
		this.eventName="";
		this.calFrom = Calendar.getInstance();
		this.calTo = this.calFrom;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getUserProfileUrl() {
		return userProfileUrl;
	}
	public void setUserProfileUrl(String userProfileUrl) {
		this.userProfileUrl = userProfileUrl;
	}
	public ArrayList<String> getEventId() {
		return eventId;
	}
	public void setEventId(ArrayList<String> eventId) {
		this.eventId = eventId;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public Calendar getCalFrom() {
		return calFrom;
	}
	public void setCalFrom(Calendar calFrom) {
		this.calFrom = calFrom;
	}
	public Calendar getCalTo() {
		return calTo;
	}
	public void setCalTo(Calendar calTo) {
		this.calTo = calTo;
	}
	
}


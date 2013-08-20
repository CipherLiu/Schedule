package com.example.schedule;

import java.util.Calendar;

public class EventInfo {

	private String userName;
	private String userId;
	private String updateTime;
	private String eventId;
	private String eventName;
	private Calendar calFrom;
	private Calendar calTo;
	private String locationName;
	private String locationCoordinate;
	private String record;
	private String photo;
	private String description;
	private String targetGroup;
	private int commentCount;
	private String commentsString;
	private String userIamge;
	
	public String getCommentsString() {
		return commentsString;
	}
	public void setCommentsString(String commentsString) {
		this.commentsString = commentsString;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public String getTargetGroup() {
		return targetGroup;
	}
	public void setTargetGroup(String targetGroup) {
		this.targetGroup = targetGroup;
	}
	public String getUserIamge() {
		return userIamge;
	}
	public void setUserIamge(String userIamge) {
		this.userIamge = userIamge;
	}
	public EventInfo() {
		
		this.userName = "";
		this.userId = "";
		this.eventId = "";
		this.eventName = "";
		this.updateTime = "";
		this.calFrom = Calendar.getInstance();
		this.calTo = this.calFrom;
		this.locationName = "";
		this.locationCoordinate = "";
		this.record = "";
		this.photo = "";
		this.description = "";
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
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
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public String getLocationCoordinate() {
		return locationCoordinate;
	}
	public void setLocationCoordinate(String locationCoordinate) {
		this.locationCoordinate = locationCoordinate;
	}
	public String getRecord() {
		return record;
	}
	public void setRecord(String record) {
		this.record = record;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public boolean CalIsSet(){
		if(this.calFrom.compareTo(calTo) != 0)
			return true;
		else
			return false;
	}
}

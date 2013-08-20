package com.example.schedule;

import java.util.Calendar;

public class CommentInfo {

	private String CommentId;
	private String publisherId;
	private String publisherName;
	private Calendar publishTime;
	private String commentContent;
	private String publisherImage;
	public String getPublisherImage() {
		return publisherImage;
	}
	public void setPublisherImage(String publisherImage) {
		this.publisherImage = publisherImage;
	}
	public String getCommentId() {
		return CommentId;
	}
	public void setCommentId(String commentId) {
		CommentId = commentId;
	}
	public String getPublisherId() {
		return publisherId;
	}
	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}
	public String getPublisherName() {
		return publisherName;
	}
	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}
	public Calendar getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(Calendar publishTime) {
		this.publishTime = publishTime;
	}
	public String getCommentContent() {
		return commentContent;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
	public CommentInfo() {
		// TODO Auto-generated constructor stub
		this.commentContent = "";
		this.CommentId = "";
		this.publisherId = "";
		this.publisherImage = "";
		this.publisherName = "";
		this.publishTime = Calendar.getInstance();
	}

}

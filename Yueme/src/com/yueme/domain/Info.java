package com.yueme.domain;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class Info implements Serializable{
	private String id;
	private long create_day;
	private String content;
	private long deadline;
	private String category;
	private String nickname;
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getCreate_day() {
		return create_day;
	}
	public void setCreate_day(long create_day) {
		this.create_day = create_day;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getDeadline() {
		return deadline;
	}
	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}
	
}

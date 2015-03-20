package com.yueme.domain;

import java.io.Serializable;
import java.util.List;

public class Comment implements Serializable{
	private int id;
	private String content;
	private String i_id;
	private String u_id;
	private long create_time;
	private String t_u_id;
	private List<Subcomment> subcomments;
	private String nickname;
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getI_id() {
		return i_id;
	}
	public void setI_id(String i_id) {
		this.i_id = i_id;
	}
	public String getU_id() {
		return u_id;
	}
	public void setU_id(String u_id) {
		this.u_id = u_id;
	}
	public long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}
	public String getT_u_id() {
		return t_u_id;
	}
	public void setT_u_id(String t_u_id) {
		this.t_u_id = t_u_id;
	}
	public List<Subcomment> getSubcomments() {
		return subcomments;
	}
	public void setSubcomments(List<Subcomment> subcomments) {
		this.subcomments = subcomments;
	}
	
}

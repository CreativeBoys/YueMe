package com.yueme.domain;

public class Subcomment {
	private int id;
	private String content;
	private String u_id;
	private String t_u_id;
	private int c_id;
	private long create_time;
	private String nickname;
	private String t_nickname;
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getT_nickname() {
		return t_nickname;
	}
	public void setT_nickname(String t_nickname) {
		this.t_nickname = t_nickname;
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
	public String getU_id() {
		return u_id;
	}
	public void setU_id(String u_id) {
		this.u_id = u_id;
	}
	public String getT_u_id() {
		return t_u_id;
	}
	public void setT_u_id(String t_u_id) {
		this.t_u_id = t_u_id;
	}
	public int getC_id() {
		return c_id;
	}
	public void setC_id(int c_id) {
		this.c_id = c_id;
	}
	public long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}
	
}

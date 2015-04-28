package com.yueme.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class ChatGroupInfo implements Serializable {
	
	private static List<Bitmap> head_icons = new ArrayList<Bitmap>();
	private List<String> nick_names = new ArrayList<String>();
	private List<String> ids = new ArrayList<String>();
    private String group_id;
    
	public ChatGroupInfo(String group_id) {
		super();
		this.group_id = group_id;
	}

	public String getGroup_id() {
		return group_id;
	}

	public Bitmap getHeadIcon(String nickName){
		for(int i=0; i< ids.size(); i++) {
			if(nick_names.get(i).equals(nickName)) {
				return head_icons.get(i);
			}
		}
		return null;
	}
	
	
    
	public String getNickName(String id){
		for(int i=0; i < ids.size(); i++) {
			if(ids.get(i).equals(id)){
				return nick_names.get(i);
			}
		}
		return null;
	}
	public void addMember(String id, String nickName, Bitmap headIcon){
		ids.add(id);
		nick_names.add(nickName);
		head_icons.add(headIcon);
	}
	


}

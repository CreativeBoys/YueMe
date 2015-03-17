package com.yueme.domain;

import java.io.Serializable;

public class ProtocalResponse implements Serializable{
	private int responseCode;
	private String response;
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	
}

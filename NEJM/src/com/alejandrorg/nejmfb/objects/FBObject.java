package com.alejandrorg.nejmfb.objects;

import java.io.File;
import java.util.Date;

import com.alejandrorg.nejmfb.core.StaticUtils;
import com.fasterxml.jackson.databind.JsonNode;

public class FBObject {

	protected String id;
	protected Date createdDate;
	protected String createdDateOriginalString;
	protected String message;
	protected File file;
	
	public FBObject(JsonNode i, JsonNode cd, JsonNode m) {
		this.id = i != null ? i.asText() : "NA";
		this.createdDateOriginalString = cd.asText();
		this.createdDate = cd != null ? StaticUtils.parseFBDate(cd.asText()) : null;
		this.message = m != null ? m.asText() : "NA";
	}
	
	public FBObject(String id, String cd, String msg) {
		this.id = id;
		this.createdDateOriginalString = cd;
		this.createdDate = StaticUtils.parseFBDate(cd);
		this.message = msg;
	}
	
	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	public String getCreatedDateOriginalString() {
		return this.createdDateOriginalString;
	}
	
	public String toString() {
		String ret = "----------------------------------";
		ret += "ID: " + id + "\r\n";
		ret += "Message: " + message + "\r\n";
		ret += "----------------------------------\r\n";
		return ret;
	}
}

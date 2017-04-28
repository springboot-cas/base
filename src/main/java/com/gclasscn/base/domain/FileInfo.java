package com.gclasscn.base.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class FileInfo {

	private String name;
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private Date lastModify;
	
	public FileInfo() {}
	
	public FileInfo(String name, Date lastModify) {
		this.name = name;
		this.lastModify = lastModify;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastModify() {
		return lastModify;
	}

	public void setLastModify(Date lastModify) {
		this.lastModify = lastModify;
	}

	@Override
	public String toString() {
		return "FileInfo [name=" + name + ", lastModify=" + lastModify + "]";
	}

}

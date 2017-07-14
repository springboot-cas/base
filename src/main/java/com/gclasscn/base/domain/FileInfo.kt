package com.gclasscn.base.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.text.SimpleDateFormat

class FileInfo(name: String, lastModify: Date) {

	var name: String = name;
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	var lastModify: Date = lastModify;

	override fun toString() : String {
		return "user=[name=$name, lastModify=${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastModify)}]"
	}
	
}

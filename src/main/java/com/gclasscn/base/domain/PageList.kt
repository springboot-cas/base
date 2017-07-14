package com.gclasscn.base.domain

import java.util.ArrayList

class PageList<T>(record: Int, size: Int, data: ArrayList<T>) {

	var record: Int = record     // 总记录条数
	var totalPages: Int? = null // 总页数
	var size: Int = size       // 页面显示数据条数
	var data: ArrayList<T> = data       // 展示数据

	init {
		totalPages = if(record % size == 0) record / size else record / size + 1
	}

}

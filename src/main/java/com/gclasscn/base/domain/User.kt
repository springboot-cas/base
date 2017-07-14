package com.gclasscn.base.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
	
	var id: Long,
	var schoolId: Long,
	var account: String,
	var password: String,
	var avatar: String,
	var type: Int,
	var name: String,
	var telephone: String,
	var status: Int
	
)
package com.gclasscn.base.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties
class CommonPropertyConfiguration {
	
	var layout: String = ""
	var fullLayout: String = ""
	var filePath: String = ""
	var casServer: String = ""
	var casService: String = ""
	var casLogin: String = ""
	var casLogout: String = ""
	var userUrls = emptyArray<String>()
	
}

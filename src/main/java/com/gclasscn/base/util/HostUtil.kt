package com.gclasscn.base.util

import org.slf4j.LoggerFactory
import org.apache.http.HttpHost
import java.net.URL
import java.net.MalformedURLException

object HostUtil {
	
	private var logger = LoggerFactory.getLogger(HostUtil::class.java);
	
	fun hosts(urls: Array<String>): ArrayList<HttpHost>{
		var hosts = ArrayList<HttpHost>(urls.size)
		urls.forEach {
			str -> var url: URL = URL(str)
			hosts.add(HttpHost(url.host, url.port, url.protocol))
		}
		return hosts
	}
}
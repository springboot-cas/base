package com.gclasscn.base.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostUtil {

	private static final Logger logger = LoggerFactory.getLogger(HostUtil.class);
	
	public static HttpHost[] hosts(String[] urls){
		HttpHost[] hosts = new HttpHost[urls.length];
		for(int i=0; i<urls.length; i++){
			URL url = null;
			try {
				url = new URL(urls[i]);
			} catch (MalformedURLException e) {
				logger.error("URL协议、格式或者路径错误: {}", e.getMessage(), e);
				continue;
			}
			hosts[i] = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
		}
		return hosts;
	}
}

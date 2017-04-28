package com.gclasscn.base.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class CommonPropertyConfiguration {
	
	private String layout;
	private String fullLayout;
	private String filePath;
	private String casServer;
	private String casService;
	private String casLogin;
	private String casLogout;
	
	private String[] userUrls;

	public String getLayout() {
		return layout;
	}
	public void setLayout(String layout) {
		this.layout = layout;
	}
	public String getFullLayout() {
		return fullLayout;
	}
	public void setFullLayout(String fullLayout) {
		this.fullLayout = fullLayout;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getCasServer() {
		return casServer;
	}
	public void setCasServer(String casServer) {
		this.casServer = casServer;
	}
	public String getCasService() {
		return casService;
	}
	public void setCasService(String casService) {
		this.casService = casService;
	}
	public String getCasLogin() {
		return casLogin;
	}
	public void setCasLogin(String casLogin) {
		this.casLogin = casLogin;
	}
	public String getCasLogout() {
		return casLogout;
	}
	public void setCasLogout(String casLogout) {
		this.casLogout = casLogout;
	}
	public String[] getUserUrls() {
		return userUrls;
	}
	public void setUserUrls(String[] userUrls) {
		this.userUrls = userUrls;
	}
	
}

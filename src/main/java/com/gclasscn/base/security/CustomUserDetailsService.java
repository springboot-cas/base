package com.gclasscn.base.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gclasscn.base.config.CommonPropertyConfiguration;
import com.gclasscn.base.domain.User;
import com.gclasscn.base.util.HostUtil;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

public class CustomUserDetailsService implements UserDetailsService{	
	
	private Gson gson = new Gson();
	
	@Autowired
	private CommonPropertyConfiguration config;
	
	@Override
	public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
		RestClient restClient = RestClient.builder(HostUtil.hosts(config.getUserUrls())).build();
		User user = null;
		try {
			Response response = restClient.performRequest(RequestMethod.GET.name(), "/v1/users/account", Collections.singletonMap("account", account));
			user = gson.fromJson(EntityUtils.toString(response.getEntity()), User.class);
			restClient.close();
		} catch (IOException e) {
			throw new UsernameNotFoundException("account \"" + account + "\" not found");
		}
		if(user == null || user.getId() == 0){
			throw new UsernameNotFoundException("account \"" + account + "\" not found");
		}
		List<GrantedAuthority> authorities = Lists.newArrayList();
		return new SecurityUser(user, authorities);
	}
}

package com.gclasscn.base.security

import org.springframework.security.core.userdetails.UserDetailsService;
import com.google.gson.GsonBuilder
import org.springframework.beans.factory.annotation.Autowired
import com.gclasscn.base.config.CommonPropertyConfiguration
import org.springframework.security.core.userdetails.UserDetails
import org.elasticsearch.client.RestClient
import com.gclasscn.base.util.HostUtil
import org.springframework.web.bind.annotation.RequestMethod
import java.util.Collections
import org.apache.http.util.EntityUtils
import com.gclasscn.base.domain.User
import java.io.IOException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import com.google.common.collect.Lists
import org.springframework.security.core.GrantedAuthority

class CustomUserDetailsService : UserDetailsService {
	
	private var gson = GsonBuilder().create();
	
	@Autowired
	private var config: CommonPropertyConfiguration = CommonPropertyConfiguration();
	
	override fun loadUserByUsername(account: String): UserDetails {
		var restClient = RestClient.builder(HostUtil.hosts(config.userUrls).get(0)).build();
		try {
			var response = restClient.performRequest(RequestMethod.GET.name, "/v1/users/account", Collections.singletonMap("account", account));
			var user = gson.fromJson(EntityUtils.toString(response.getEntity()), User::class.java);
			if(user == null || user.id == 0L){
				throw UsernameNotFoundException("account \"" + account + "\" not found");
			}
			var authorities: MutableCollection<GrantedAuthority> = Lists.newArrayList();
			return SecurityUser(user, authorities);
		} catch (e: IOException) {
			throw UsernameNotFoundException("account \"" + account + "\" not found");
		} finally {
			restClient.close();
		}
	}
}
package com.gclasscn.base.controller

import com.gclasscn.base.config.CommonPropertyConfiguration
import com.gclasscn.base.domain.User
import com.gclasscn.base.util.HostUtil
import com.google.gson.GsonBuilder
import org.apache.http.util.EntityUtils
import org.elasticsearch.client.RestClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.util.Collections

@RestController
class UserController {

	private var gson = GsonBuilder().create()
	
	@Autowired
	private var restTemplate = RestTemplate()
	
	@Autowired
	private var config = CommonPropertyConfiguration()
	
	@GetMapping("/users/name/{name}")
	fun getUser(@PathVariable name: String) : User {
		var result = restTemplate.exchange(config.userUrls[0] + "/v1/users/account?account=" + name, HttpMethod.GET, null, User::class.java)
		return result.getBody()
	}
	
	@GetMapping("/users/{name}")
	fun login(@PathVariable name: String) : User {
		var restClient = RestClient.builder(HostUtil.hosts(config.userUrls).get(0)).build();
		var response = restClient.performRequest(RequestMethod.GET.name, "/v1/users/account", Collections.singletonMap("account", name))
		return gson.fromJson(EntityUtils.toString(response.getEntity()), User::class.java)
	}
	
}
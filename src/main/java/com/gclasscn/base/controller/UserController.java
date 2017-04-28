package com.gclasscn.base.controller;

import java.io.IOException;
import java.util.Collections;

import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gclasscn.base.domain.User;
import com.google.gson.Gson;

@RestController
public class UserController {

	private Gson gson = new Gson();
	
//	@Autowired
//	private RestTemplate restTemplate;
	
	@RequestMapping(value = "/users/name/{name}", method = RequestMethod.GET)
	public @ResponseBody User getUser(@PathVariable String name) throws IOException{
		RestClient restClient = RestClient.builder(new HttpHost("127.0.0.1", 9003, "http"), new HttpHost("qingdao.gclasscn.com", 9003, "http")).build();
		Response response = restClient.performRequest(RequestMethod.GET.name(), "/v1/users/account", Collections.singletonMap("account", name));
		return gson.fromJson(EntityUtils.toString(response.getEntity()), User.class);
	}
	
//	@RequestMapping(value = "/users/name/{name}", method = RequestMethod.GET)
//	public @ResponseBody User getUser(@PathVariable String name){
//		ResponseEntity<User> user = restTemplate.exchange("http://qingdao.gclasscn.com:9003/v1/users/account", HttpMethod.GET, null, User.class, Collections.singletonMap("account", name));
//		return user.getBody();
//	}
}

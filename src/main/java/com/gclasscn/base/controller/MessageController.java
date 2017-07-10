package com.gclasscn.base.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.gclasscn.base.domain.User;
import com.gclasscn.base.security.SecurityUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Controller
public class MessageController {

	private Logger logger = LoggerFactory.getLogger(MessageController.class);
	
	@Autowired
    private KafkaTemplate<String,String> template;
	
    private Gson gson = new GsonBuilder().create();
	
	@RequestMapping(value = "/kafka", method = RequestMethod.GET)
	public @ResponseBody void kafka(){
		User user = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		send(user);
	}
	
	private void send(User user) {
		//消息发送的监听器，用于回调返回信息
        template.setProducerListener(new ProducerListener<String, String>() {
            @Override
            public void onSuccess(String topic, Integer partition, String key, String value, RecordMetadata metadata) {
            	
            }

            @Override
            public void onError(String topic, Integer partition, String key, String value, Exception e) {
            	logger.error("发送消息失败: {}", e.getMessage(), e);
            }

            @Override
            public boolean isInterestedInSuccess() {
                return true;
            }
        });
        template.send("my-topic", gson.toJson(user));
    }
	
	public static void main(String[] args) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + getToken());
		headers.add("Content-Type", "application/json");
		HttpEntity<String> query = new HttpEntity<String>(getBody(), headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> result = restTemplate.exchange("http://192.168.1.146:8081/apis", HttpMethod.POST, query, String.class);
		System.out.println(result);
	}
	
	private static String getToken(){
		HttpEntity<String> query = new HttpEntity<String>("{\"username\" : \"admin\", \"password\" : \"admin\"}");
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> result = restTemplate.exchange("http://192.168.1.146:8081/login", HttpMethod.POST, query, String.class);
		Gson gson = new GsonBuilder().create();
		Map<?, ?> map = gson.fromJson(result.getBody(), Map.class);
		return (String) map.get("token");
	}
	
	private static String getBody(){
		try (BufferedReader reader = new BufferedReader(new FileReader("G:/body.json"))) {
			StringBuilder builder = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null){
				builder.append(line);
			}
			return builder.toString();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}
}
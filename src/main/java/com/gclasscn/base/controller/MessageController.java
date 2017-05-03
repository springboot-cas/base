package com.gclasscn.base.controller;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	
	@KafkaListener(group = "base", topics = {"my-topic"})
    public void onMessage(String message) {
        System.out.println("my-topic: " + message);
    }
	
}
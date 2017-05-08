package com.gclasscn.base.listener;

import org.springframework.kafka.annotation.KafkaListener;

public class MessageListener {

	@KafkaListener(topics = "my-topic")
	public void onMessage(String message){
		System.out.println("message: " + message);
	}
}

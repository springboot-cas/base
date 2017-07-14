package com.gclasscn.base.listener

import org.springframework.kafka.annotation.KafkaListener

class MessageListener {

	@KafkaListener(topics = arrayOf("my-topic"))
	fun onMessage(message: String){
		System.out.println("message: " + message)
	}
}

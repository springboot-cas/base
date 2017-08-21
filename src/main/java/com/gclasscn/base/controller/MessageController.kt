package com.gclasscn.base.controller

import com.gclasscn.base.domain.User
import com.gclasscn.base.security.SecurityUser
import com.google.gson.GsonBuilder
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.ProducerListener
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class MessageController {

	private val logger = LoggerFactory.getLogger(MessageController::class.java)
	
	@Autowired
    private var template: KafkaTemplate<String, String>? = null
	
    private var gson = GsonBuilder().create()
	
	@GetMapping("/kafka")
	@ResponseBody
	fun kafka(){
		var securityUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal() as SecurityUser
		send(securityUser.user)
	}
	
	private fun send(user: User?) {
		//消息发送的监听器，用于回调返回信息
        template?.setProducerListener(object : ProducerListener<String, String>{
            override fun onSuccess(topic: String?, partition: Int?, key: String?, value: String?, metadata: RecordMetadata?) {
            	
            }

            override fun onError(topic: String?, partition: Int?, key: String?, value: String?, e: Exception?) {
            	logger.error("发送消息失败: {}", e?.message, e)
            }

            override fun isInterestedInSuccess() : Boolean {
                return true
            }
        })
        template?.send("my-topic", gson.toJson(user))
    }
	
}
package com.gclasscn.base

import com.gclasscn.base.config.CommonPropertyConfiguration
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.support.SpringBootServletInitializer

@SpringBootApplication
@EnableConfigurationProperties(CommonPropertyConfiguration::class)
open class Application() : SpringBootServletInitializer() {
    
	override protected fun configure(builder: SpringApplicationBuilder) : SpringApplicationBuilder {
		return builder.sources(Application::class.java);
	}
	
	companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }
	
}
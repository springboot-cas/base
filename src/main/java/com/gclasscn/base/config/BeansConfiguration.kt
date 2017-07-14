package com.gclasscn.base.config

import java.util.LinkedHashMap

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer
import org.springframework.boot.web.servlet.ErrorPage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.http.HttpStatus
import org.springframework.util.MimeType
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.ViewResolver
import org.thymeleaf.spring4.SpringTemplateEngine
import org.thymeleaf.spring4.resourceresolver.SpringResourceResourceResolver
import org.thymeleaf.spring4.view.ThymeleafViewResolver
import org.thymeleaf.templateresolver.TemplateResolver

import nz.net.ultraq.thymeleaf.LayoutDialect
import java.util.Objects
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer

@Configuration
open class BeansConfiguration {

	@Autowired
	private var properties: ThymeleafProperties? = null

	@Bean
	open fun thymeleafResourceResolver(): SpringResourceResourceResolver {
		return SpringResourceResourceResolver()
	}

	@Bean
	open fun templateResolver(): TemplateResolver {
		var resolver = TemplateResolver()
		resolver.setResourceResolver(thymeleafResourceResolver())
		if (properties != null) {
			resolver.setPrefix(properties?.getPrefix())
			resolver.setSuffix(properties?.getSuffix())
			resolver.setTemplateMode(properties?.getMode())
			if (properties?.getEncoding() != null) {
				resolver.setCharacterEncoding(properties?.encoding?.name())
			}
			var cache = properties?.isCache
			if (cache != null) {
				resolver.setCacheable(cache)
			}
			var order = properties?.templateResolverOrder
			if (order != null) {
				resolver.setOrder(order)
			}
		}
		return resolver
	}

	@Bean
	open fun templateEngine(): SpringTemplateEngine {
		var templateEngine = SpringTemplateEngine()
		templateEngine.setTemplateResolver(templateResolver())
		templateEngine.addDialect(LayoutDialect())
		return templateEngine
	}

	@Bean
	open fun viewResolver(): ViewResolver {
		var resolver = CustomThymeleafViewResolver()
		resolver.setTemplateEngine(templateEngine())
		resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 5)
		if (properties != null) {
			resolver.setCharacterEncoding(properties?.getEncoding()?.name())
			resolver.setContentType(appendCharset(properties?.getContentType(), resolver.getCharacterEncoding()))
			resolver.setExcludedViewNames(properties?.getExcludedViewNames())
			resolver.setViewNames(properties?.getViewNames())
			var cache = properties?.isCache()
			if (cache != null) resolver.setCache(cache)
		}
		return resolver
	}

	private fun appendCharset(type: MimeType?, charset: String): String {
		if (type != null && type.getCharset() != null) {
			return type.toString()
		}
		var parameters = LinkedHashMap<String, String>()
		parameters.put("charset", charset)
		parameters.putAll(type?.getParameters() ?: LinkedHashMap<String, String>())
		return MimeType(type, parameters).toString()
	}

	@Bean
	open fun containerCustomizer(): EmbeddedServletContainerCustomizer {
		return EmbeddedServletContainerCustomizer {
			container ->
			container.addErrorPages(
					ErrorPage(HttpStatus.UNAUTHORIZED, "/" + HttpStatus.UNAUTHORIZED.value()),
					ErrorPage(HttpStatus.NOT_FOUND, "/" + HttpStatus.NOT_FOUND.value()),
					ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/" + HttpStatus.INTERNAL_SERVER_ERROR.value()))
		}
	}

	@Bean
	open fun restTemplate(): RestTemplate {
		return RestTemplate()
	}

}

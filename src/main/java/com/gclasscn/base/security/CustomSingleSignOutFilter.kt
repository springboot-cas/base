package com.gclasscn.base.security

import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.jasig.cas.client.configuration.ConfigurationKeys
import org.jasig.cas.client.session.SessionMappingStorage
import org.jasig.cas.client.session.SingleSignOutHandler
import org.jasig.cas.client.util.AbstractConfigurationFilter

public final class CustomSingleSignOutFilter(casServerUrlPrefix: String) : AbstractConfigurationFilter() {

	private var casServerUrlPrefix = casServerUrlPrefix
	
    private var handlerInitialized = AtomicBoolean(false)
	
	companion object {
		private val HANDLER = SingleSignOutHandler()
		protected fun getSingleSignOutHandler() : SingleSignOutHandler {
	        return HANDLER
	    }
	}

    override fun init(filterConfig: FilterConfig) {
        super.init(filterConfig)
        if (!isIgnoreInitConfiguration()) {
            setArtifactParameterName(getString(ConfigurationKeys.ARTIFACT_PARAMETER_NAME))
            setLogoutParameterName(getString(ConfigurationKeys.LOGOUT_PARAMETER_NAME))
            setFrontLogoutParameterName(getString(ConfigurationKeys.FRONT_LOGOUT_PARAMETER_NAME))
            setRelayStateParameterName(getString(ConfigurationKeys.RELAY_STATE_PARAMETER_NAME))
//          setCasServerUrlPrefix(getString(ConfigurationKeys.CAS_SERVER_URL_PREFIX))
            HANDLER.setCasServerUrlPrefix(casServerUrlPrefix)
            HANDLER.setArtifactParameterOverPost(getBoolean(ConfigurationKeys.ARTIFACT_PARAMETER_OVER_POST))
            HANDLER.setEagerlyCreateSessions(getBoolean(ConfigurationKeys.EAGERLY_CREATE_SESSIONS))
        }
        HANDLER.init()
        handlerInitialized.set(true)
    }

    fun setArtifactParameterName(name: String) {
        HANDLER.setArtifactParameterName(name)
    }

    fun setLogoutParameterName(name: String) {
        HANDLER.setLogoutParameterName(name)
    }

    fun setFrontLogoutParameterName(name: String) {
        HANDLER.setFrontLogoutParameterName(name)
    }

    fun setRelayStateParameterName(name: String) {
        HANDLER.setRelayStateParameterName(name)
    }

    fun setCasServerUrlPrefix(casServerUrlPrefix: String) {
        HANDLER.setCasServerUrlPrefix(casServerUrlPrefix)
    }

    fun setSessionMappingStorage(storage: SessionMappingStorage) {
        HANDLER.setSessionMappingStorage(storage)
    }

    override fun doFilter(servletRequest: ServletRequest , servletResponse: ServletResponse, filterChain: FilterChain) {
        val request = servletRequest as HttpServletRequest
        val response = servletResponse as HttpServletResponse

        if (!this.handlerInitialized.getAndSet(true)) {
            HANDLER.init()
        }

        if (HANDLER.process(request, response)) {
            filterChain.doFilter(servletRequest, servletResponse)
        }
    }

    override fun destroy() {
        // nothing to do
    }
    
}

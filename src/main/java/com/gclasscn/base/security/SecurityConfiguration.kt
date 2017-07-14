package com.gclasscn.base.security;

import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.cas.web.authentication.ServiceAuthenticationDetailsSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;

import com.gclasscn.base.config.CommonPropertyConfiguration;

@Configuration
@EnableWebSecurity
open class SecurityConfiguration() : WebSecurityConfigurerAdapter() {

	@Autowired
	private var commonConfig = CommonPropertyConfiguration();
	
	@Bean
	open fun serviceProperties() : ServiceProperties {
		var serviceProperties = ServiceProperties();
		serviceProperties.setService(commonConfig.casService);
		serviceProperties.setSendRenew(false);
		return serviceProperties;
	}
	
	@Bean
	open fun casAuthenticationProvider() : CasAuthenticationProvider {
		var casAuthenticationProvider = CasAuthenticationProvider();
		casAuthenticationProvider.setAuthenticationUserDetailsService(userDetailsByNameServiceWrapper());
		casAuthenticationProvider.setServiceProperties(serviceProperties());
		casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
		casAuthenticationProvider.setKey("cas");
		return casAuthenticationProvider;
	}
	
	@Bean
	open fun userDetailsByNameServiceWrapper() : UserDetailsByNameServiceWrapper<CasAssertionAuthenticationToken> {
		return UserDetailsByNameServiceWrapper<CasAssertionAuthenticationToken>(customUserDetailsService());
	}
	
	@Bean
	open fun customUserDetailsService() : CustomUserDetailsService {
		return CustomUserDetailsService();
	}
	
	@Bean
	open fun cas20ServiceTicketValidator() : Cas20ServiceTicketValidator {
		return Cas20ServiceTicketValidator(commonConfig.casServer);
	}
	
	@Bean
	open fun casAuthenticationFilter() : CasAuthenticationFilter {
		var casAuthenticationFilter = CasAuthenticationFilter();
		casAuthenticationFilter.setAuthenticationManager(authenticationManager());
		casAuthenticationFilter.setAuthenticationDetailsSource(serviceAuthenticationDetailsSource());
		return casAuthenticationFilter;
	}
	
	@Bean
	open fun sessionStrategy() : SessionAuthenticationStrategy {
		return SessionFixationProtectionStrategy();
	}
	
	@Bean
	open fun serviceAuthenticationDetailsSource() : ServiceAuthenticationDetailsSource {
		return ServiceAuthenticationDetailsSource(serviceProperties());
	}
	
	@Bean
	open fun casAuthenticationEntryPoint() : CasAuthenticationEntryPoint {
		var casAuthenticationEntryPoint = CasAuthenticationEntryPoint();
		casAuthenticationEntryPoint.setLoginUrl(commonConfig.casLogin);
		casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
		return casAuthenticationEntryPoint;
	}
	
	@Bean
	open fun singleSignOutFilter() : CustomSingleSignOutFilter {
		return CustomSingleSignOutFilter(commonConfig.casServer);
	}
	
	@Bean
	open fun logoutFilter() : LogoutFilter {
		return LogoutFilter(commonConfig.casLogout, SecurityContextLogoutHandler());
	}

	@Bean
	open fun daoAuthenticationProvider() : DaoAuthenticationProvider {
		var daoAuthenticationProvider = DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(CustomUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(BCryptPasswordEncoder(10));
		return daoAuthenticationProvider;
	}
	
	@Autowired
	open fun configureGlobal(auth: AuthenticationManagerBuilder) {
		auth.authenticationProvider(casAuthenticationProvider());
		auth.authenticationProvider(daoAuthenticationProvider());
	}

	override protected fun configure(http: HttpSecurity) {
		http
			.exceptionHandling()
			.authenticationEntryPoint(casAuthenticationEntryPoint())
			.and()
			.addFilter(casAuthenticationFilter())
			.addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter::class.java)
			.addFilterBefore(logoutFilter(), LogoutFilter::class.java)
			.csrf()
			.disable()
			.authorizeRequests()
			.antMatchers("/css/**", "/images/**", "/js/**")
			.permitAll()
			.anyRequest()
			.authenticated()
			.and()
			.logout()
			.logoutSuccessUrl("/logout")
			.permitAll()
			.invalidateHttpSession(true);
	}
}

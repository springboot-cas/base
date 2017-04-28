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
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private CommonPropertyConfiguration commonConfig;
	
	@Bean
	public ServiceProperties serviceProperties(){
		ServiceProperties serviceProperties = new ServiceProperties();
		serviceProperties.setService(commonConfig.getCasService());
		serviceProperties.setSendRenew(false);
		return serviceProperties;
	}
	
	@Bean
	public CasAuthenticationProvider casAuthenticationProvider(){
		CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
		casAuthenticationProvider.setAuthenticationUserDetailsService(userDetailsByNameServiceWrapper());
		casAuthenticationProvider.setServiceProperties(serviceProperties());
		casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
		casAuthenticationProvider.setKey("cas");
		return casAuthenticationProvider;
	}
	
	@Bean
	public UserDetailsByNameServiceWrapper<CasAssertionAuthenticationToken> userDetailsByNameServiceWrapper(){
		return new UserDetailsByNameServiceWrapper<CasAssertionAuthenticationToken>(customUserDetailsService());
	}
	
	@Bean
	public CustomUserDetailsService customUserDetailsService(){
		return new CustomUserDetailsService();
	}
	
	@Bean
	public Cas20ServiceTicketValidator cas20ServiceTicketValidator(){
		return new Cas20ServiceTicketValidator(commonConfig.getCasServer());
	}
	
	@Bean
	public CasAuthenticationFilter casAuthenticationFilter() throws Exception{
		CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
		casAuthenticationFilter.setAuthenticationManager(authenticationManager());
		casAuthenticationFilter.setAuthenticationDetailsSource(serviceAuthenticationDetailsSource());
		return casAuthenticationFilter;
	}
	
	@Bean
	public SessionAuthenticationStrategy sessionStrategy() {
		return new SessionFixationProtectionStrategy();
	}
	
	@Bean
	public ServiceAuthenticationDetailsSource serviceAuthenticationDetailsSource(){
		return new ServiceAuthenticationDetailsSource(serviceProperties());
	}
	
	@Bean
	public CasAuthenticationEntryPoint casAuthenticationEntryPoint(){
		CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
		casAuthenticationEntryPoint.setLoginUrl(commonConfig.getCasLogin());
		casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
		return casAuthenticationEntryPoint;
	}
	
	@Bean
	public CustomSingleSignOutFilter singleSignOutFilter(){
		return new CustomSingleSignOutFilter(commonConfig.getCasServer());
	}
	
	@Bean
	public LogoutFilter logoutFilter(){
		return new LogoutFilter(commonConfig.getCasLogout(), new SecurityContextLogoutHandler());
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider(){
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(new CustomUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder(10));
		return daoAuthenticationProvider;
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(casAuthenticationProvider());
		auth.authenticationProvider(daoAuthenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.exceptionHandling()
			.authenticationEntryPoint(casAuthenticationEntryPoint())
			.and()
			.addFilter(casAuthenticationFilter())
			.addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class)
			.addFilterBefore(logoutFilter(), LogoutFilter.class)
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

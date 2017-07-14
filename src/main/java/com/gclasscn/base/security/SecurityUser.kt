package com.gclasscn.base.security;

import org.springframework.security.core.GrantedAuthority;

import com.gclasscn.base.domain.User;


class SecurityUser : org.springframework.security.core.userdetails.User{

	var user: User? = null;
	
	constructor(user: User, authorities: MutableCollection<GrantedAuthority>):
			super(user.account, user.password, true, true, true, true, authorities) {
		this.user = user;	
	}
	
}

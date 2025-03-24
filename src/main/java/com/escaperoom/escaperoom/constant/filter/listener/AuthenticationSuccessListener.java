package com.escaperoom.escaperoom.constant.filter.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.service.LoginAttemptService;

@Component
public class AuthenticationSuccessListener {

	@Autowired
	private LoginAttemptService loginAttemptService;

	@Autowired
	public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
		super();
		this.loginAttemptService = loginAttemptService;
	}

	@EventListener
	public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
		
		Object principal = event.getAuthentication().getPrincipal();
		if(principal instanceof User) {
			User user = (User) event.getAuthentication().getPrincipal();
			
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
			
		}
	}
	
}

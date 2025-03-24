package com.escaperoom.escaperoom.constant.filter.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import com.escaperoom.escaperoom.service.LoginAttemptService;

@Component
public class AuthenticationFailureListener {
	
	@Autowired
	private LoginAttemptService loginAttemptService;

	@Autowired
	public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
		super();
		this.loginAttemptService = loginAttemptService;
	}
	
	@EventListener
	public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
		
		Object principal = event.getAuthentication().getPrincipal();
		if(principal instanceof String) {
			String username = (String) event.getAuthentication().getPrincipal();
			
			loginAttemptService.addUserToLoginAttemptCache(username);
			
		}
	}
	

}

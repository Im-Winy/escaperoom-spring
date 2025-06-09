package com.escaperoom.escaperoom.security;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.escaperoom.escaperoom.service.UserService;

public class LoginAttemptAuthenticationProvider implements AuthenticationProvider {

	private final UserService userService;
	private final LoginAttemptService loginAttemptService;
	private final PasswordEncoder passwordEncoder;

	public LoginAttemptAuthenticationProvider(UserService userService, LoginAttemptService loginAttemptService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.loginAttemptService = loginAttemptService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String presentedPassword = authentication.getCredentials().toString();

		if (loginAttemptService.isBlocked(username)) {
			throw new LockedException("Compte bloqué");
		}

		UserDetails userDetails = userService.loadUserByUsername(username); // cohérence ici

		if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
			loginAttemptService.loginFailed(username);
			throw new BadCredentialsException("Mot de passe incorrect");
		}

		loginAttemptService.loginSucceeded(username);

		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}

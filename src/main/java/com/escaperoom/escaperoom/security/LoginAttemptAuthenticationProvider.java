package com.escaperoom.escaperoom.security;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class LoginAttemptAuthenticationProvider implements AuthenticationProvider {

	private final UserDetailsService userDetailsService;
	private final LoginAttemptService loginAttemptService;
	private final PasswordEncoder passwordEncoder;

	public LoginAttemptAuthenticationProvider(UserDetailsService userDetailsService,
			LoginAttemptService loginAttemptService, PasswordEncoder passwordEncoder) {
		this.userDetailsService = userDetailsService;
		this.loginAttemptService = loginAttemptService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String presentedPassword = authentication.getCredentials().toString();

		if (loginAttemptService.isBlocked(username)) {
			throw new LockedException("Compte bloqué après trop de tentatives");
		}

		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
			loginAttemptService.loginFailed(username);
			throw new BadCredentialsException("Mot de passe incorrect");
		}

		// Connexion réussie, on reset le compteur et déverrouille si besoin
		loginAttemptService.loginSucceeded(username);

		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}

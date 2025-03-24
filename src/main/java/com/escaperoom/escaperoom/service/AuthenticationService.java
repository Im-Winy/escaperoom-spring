package com.escaperoom.escaperoom.service;

import com.escaperoom.dto.JwtAuthenticationResponse;
import com.escaperoom.dto.RefreshTokenRequest;
import com.escaperoom.dto.SignInRequest;
import com.escaperoom.dto.SignUpRequest;
import com.escaperoom.escaperoom.entity.User;

public interface AuthenticationService {

	User signup(SignUpRequest signUpRequest);
	JwtAuthenticationResponse signIn(SignInRequest signInRequest);
	JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}

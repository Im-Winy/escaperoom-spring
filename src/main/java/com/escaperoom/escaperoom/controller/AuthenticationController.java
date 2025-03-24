package com.escaperoom.escaperoom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.escaperoom.dto.JwtAuthenticationResponse;
import com.escaperoom.dto.RefreshTokenRequest;
import com.escaperoom.dto.SignInRequest;
import com.escaperoom.dto.SignUpRequest;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
	
	@Autowired
	AuthenticationService authenticationService;
	
	@PostMapping("/signup")
	public ResponseEntity<User> signup(@RequestBody SignUpRequest signUpRequest){
		return ResponseEntity.ok(authenticationService.signup(signUpRequest));		
		
	}
	
	@PostMapping("/signin")
	public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SignInRequest signInRequest){
		return ResponseEntity.ok(authenticationService.signIn(signInRequest));		
		
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<JwtAuthenticationResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest){
		return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));		
		
	}

}

package com.escaperoom.escaperoom.service.impl;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.escaperoom.dto.JwtAuthenticationResponse;
import com.escaperoom.dto.RefreshTokenRequest;
import com.escaperoom.dto.SignInRequest;
import com.escaperoom.dto.SignUpRequest;
import com.escaperoom.escaperoom.entity.Role;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.repository.IUserRepository;
import com.escaperoom.escaperoom.service.AuthenticationService;
import com.escaperoom.escaperoom.service.JWTService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

	@Autowired
	IUserRepository userRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JWTService jwtService;

	public User signup(SignUpRequest signUpRequest) {

		User user = new User();
		user.setEmail(signUpRequest.getEmail());
		user.setPrenom(signUpRequest.getPrenom());
		user.setNom(signUpRequest.getNom());
		user.setRole(Role.ROLE_USER); // User is not allow to be an admin in register
		user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

		return userRepository.save(user);

	}

	public JwtAuthenticationResponse signIn(SignInRequest signInRequest) {
		authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword()));
		
		var user = userRepository.findUserByEmail(signInRequest.getEmail());
		var jwt = jwtService.generateToken(user);
		var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
		
		JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
		
		jwtAuthenticationResponse.setToken(jwt);
		jwtAuthenticationResponse.setRefreshToken(refreshToken);
		jwtAuthenticationResponse.setEmail(user.getEmail());
		jwtAuthenticationResponse.setPrenom(user.getPrenom());
		jwtAuthenticationResponse.setNom(user.getNom());
		
		return jwtAuthenticationResponse;
		
	}
	
	public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
		String userEmail = jwtService.extractUserName(refreshTokenRequest.getToken());
		User user = userRepository.findUserByEmail(userEmail);
		
		if(jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
			var jwt = jwtService.generateToken(user);
			
			JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
			jwtAuthenticationResponse.setToken(jwt);
			
			jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
			
			return jwtAuthenticationResponse;
		}
		return null;
		
	}

}

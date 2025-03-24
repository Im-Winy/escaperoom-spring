package com.escaperoom.dto;

import lombok.Data;

@Data
public class JwtAuthenticationResponse {
	
	private String token;
	
	private String refreshToken;
	private String email;
	private String username;
	private String prenom;
	private String nom;

}
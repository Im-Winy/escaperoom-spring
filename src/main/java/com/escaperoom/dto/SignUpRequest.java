package com.escaperoom.dto;

import lombok.Data;

@Data
public class SignUpRequest {
	
	private String email;
	private String password;
	private String nom;
	private String prenom;
	
}

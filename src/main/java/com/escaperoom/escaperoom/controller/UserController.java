package com.escaperoom.escaperoom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.service.JWTService;
import com.escaperoom.escaperoom.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user/")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {
	@Autowired
	UserService userService;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	JWTService jwtToken;

	
	
	@PutMapping("user/{id}")
	public ResponseEntity<User> updateOneUser(@PathVariable("id") long idUser, 
			@RequestPart("prenom") String prenom,
			@RequestPart("nom") String nom, 
			@RequestPart("username") String username,
			@RequestPart("email") String email,
			@RequestPart(value = "password", required = false) String password) {
		User updatedUser = userService.updateOneUser(idUser, prenom, nom, username, email, password);
		return ResponseEntity.ok(updatedUser);
	}

	

}
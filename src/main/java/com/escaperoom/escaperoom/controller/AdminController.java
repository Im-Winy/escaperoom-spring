package com.escaperoom.escaperoom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminController {
	
	@Autowired
	UserService userService;
	
	@GetMapping
	public ResponseEntity<String> sayHello(){
		return ResponseEntity.ok("Hello Admin");
		}
	
	@PutMapping("update/{id}")
	public ResponseEntity<User> updateUser(@PathVariable("id") long idUser, 
			@RequestPart("prenom") String prenom,
			@RequestPart("nom") String nom, 
			@RequestPart("username") String username,
			@RequestPart("email") String email, 
			@RequestPart("role") String role) {
		User updatedUser = userService.updateUser(idUser, prenom, nom, username, email, role);
		return ResponseEntity.ok(updatedUser);
	}

}

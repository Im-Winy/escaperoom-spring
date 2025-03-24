package com.escaperoom.escaperoom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
	
	@GetMapping
	public ResponseEntity<String> sayHello(){
		return ResponseEntity.ok("Hello Admin");
		}

}

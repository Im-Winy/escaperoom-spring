package com.escaperoom.escaperoom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.escaperoom.escaperoom.constant.SecurityConstant;
import com.escaperoom.escaperoom.constant.UserImplConstant;
import com.escaperoom.escaperoom.entity.HttpResponse;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.exception.EmailExistException;
import com.escaperoom.escaperoom.exception.ExceptionHandling;
import com.escaperoom.escaperoom.exception.UsernameExistException;
import com.escaperoom.escaperoom.service.JWTService;
import com.escaperoom.escaperoom.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController extends ExceptionHandling {
	@Autowired
	UserService userService;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	JWTService jwtToken;

	@PostMapping("login")
	public ResponseEntity<User> login(@RequestBody User user) {
		authenticate(user.getUsername(), user.getPassword());
		User loginUser = userService.findUserByUsername(user.getUsername());
		User userPrincipal = new User();
		userPrincipal = loginUser;
		HttpHeaders jwtHeaders = getJwtHeader(userPrincipal);

		return new ResponseEntity<>(loginUser, jwtHeaders, HttpStatus.OK);
	}

	@PostMapping("register")
	public ResponseEntity<User> register(@RequestBody User user) throws UsernameExistException, EmailExistException {

		User newUser = userService.register(user.getPrenom(), user.getNom(), user.getUsername(), user.getEmail());
		if (newUser != null) {
			return new ResponseEntity<>(newUser, HttpStatus.OK);
		} else {
			throw new UsernameExistException(UserImplConstant.USERNAME_ALREADY_EXIST);
		}
	}

	private HttpHeaders getJwtHeader(User loginUser) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(SecurityConstant.JWT_TOKEN_HEADER, jwtToken.generateToken(loginUser));
		return headers;
	}

	private void authenticate(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}

	@GetMapping("list")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> user = userService.getUsers();
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@PostMapping("add")
	public ResponseEntity<User> addNewUser(@RequestParam("prenom") String prenom, @RequestParam("nom") String nom,
			@RequestParam("username") String username, @RequestParam("password") String password,
			@RequestParam("email") String email, @RequestParam("role") String role,
			@RequestParam("active") String active, @RequestParam("isNotLocked") String isNotLocked) {
		User newUser = userService.addNewUser(prenom, nom, username, password, email, role,
				Boolean.parseBoolean(active), Boolean.parseBoolean(isNotLocked));
		return new ResponseEntity<>(newUser, HttpStatus.OK);
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

	@DeleteMapping("delete/{id}")
	public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") long id) {
		userService.deleteUser(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
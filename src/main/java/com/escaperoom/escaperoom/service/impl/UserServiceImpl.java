package com.escaperoom.escaperoom.service.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.escaperoom.escaperoom.entity.Role;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.exception.EmailExistException;
import com.escaperoom.escaperoom.exception.UserNotFoundException;
import com.escaperoom.escaperoom.exception.UsernameExistException;
import com.escaperoom.escaperoom.repository.IUserRepository;
import com.escaperoom.escaperoom.service.EmailService;
import com.escaperoom.escaperoom.service.LoginAttemptService;
import com.escaperoom.escaperoom.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import static com.escaperoom.escaperoom.constant.UserImplConstant.*;
import static com.escaperoom.escaperoom.constant.FileConstant.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@Qualifier("UserDetailsService")
//@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService{
	

	@Autowired
	IUserRepository userRepository;
	private Logger LOGGER = LoggerFactory.getLogger(getClass()) ;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private LoginAttemptService loginAttemptService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	public UserServiceImpl(IUserRepository userRepository, BCryptPasswordEncoder passwordEncoder,LoginAttemptService loginAttemptService, EmailService emailService) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.loginAttemptService = loginAttemptService;
		this.emailService = emailService;
		this.request = request;
	}

	// Charge un utilisateur par son nom d'utilisateur pour l'authentification
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		// Vérifie d'abord si l'utilisateur est bloqué
		if(loginAttemptService.isBlocked()) {
			throw new RuntimeException("You are blocked");
		}
		
		try {
			User user = userRepository.findUserByUsername(username);
			
			if(user == null) {
				return new org.springframework.security.core.userdetails.User(" "," ",true,true,true,true,user.getAuthorities());	
			}
			
			return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(), user.isEnabled(),true,true,true,user.getAuthorities());
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Valide les tentatives de connexion et bloque un utilisateur
	// s’il a dépassé le nombre maximal de tentatives
	private void validateLoginAttempt(User user) throws ExecutionException {
		
		if(user.isNotLocked()){
			
			if(loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
				user.setNotLocked(false);
				
			}else {
				user.setNotLocked(true);
			}
			
		} else {
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
		}
	}

	// Retourne un service interne pour charger les utilisateurs
    // (Méthode redondante avec loadUserByUsername principale)
	@Override
	public UserDetailsService userDetailsService() {
		
		return new UserDetailsService() {
			
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				
				return userRepository.findUserByUsername(username);
			}
		};
	}

	// Enregistre un nouvel utilisateur avec un mot de passe généré
	@Override
	public User register(String prenom, String nom, String username, String email) {
		
		try {
			validateNewUsernameAndEmail(StringUtils.EMPTY,username,email);
			
			User user = new User();
			user.setId(generateUserId());
			String password = generatePassword();
			String encodedPassword = encodePassword(password);
			user.setPrenom(prenom);
			user.setNom(nom);
			user.setUsername(username);
			user.setEmail(email);
			user.setJoinDate(new Date());
			user.setPassword(encodedPassword);
			user.setActive(true);
			user.setNotLocked(true);
			user.setRole(Role.ROLE_USER);
			user.setAuthorities(Role.ROLE_USER.getAuthorities());
			userRepository.save(user);
			
			LOGGER.info(NEW_USER_PASSWORD + password);
			
			// Envoie un email de confirmation
			emailService.sendConfirmRegister(email, username, password);
			
			return user;
			
		} catch (UserNotFoundException | UsernameExistException | EmailExistException e) {
		
			e.printStackTrace();
		} 
		return null;  
	}

	// Encode un mot de passe en utilisant BCrypt
	private String encodePassword(String password) {
		
		return passwordEncoder.encode(password);
	}

	// Génère un mot de passe aléatoire de 12 caractères
	private String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(12);
	}

	// Génère un identifiant utilisateur numérique de 10 chiffres
	private String generateUserId() {
		return RandomStringUtils.randomNumeric(10);
	}

	// Valide que le nom d'utilisateur et l'email n'existent pas déjà
    // ou qu'ils sont bien associés à l'utilisateur actuel
	private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
	
		User userByNewUsername  = findUserByUsername(newUsername);
		User userByNewEmail  = findUserByEmail(newEmail);
		
		if(StringUtils.isNotBlank(currentUsername)) {
		User currentUser = findUserByUsername(currentUsername);
		
		if(currentUser == null) {
			throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME+currentUsername);
		}
		
		User userByUsername = findUserByUsername(newUsername);
		if(userByUsername !=null && !currentUser.getIdUser().equals(userByUsername.getIdUser())) {
			throw new UsernameExistException(USERNAME_ALREADY_EXIST);
			
		}
		User userByEmail = findUserByEmail(newEmail);
		if(userByEmail != null && !currentUser.getIdUser().equals(userByEmail.getIdUser())) {
			
			throw new EmailExistException(EMAIL_ALREADY_EXIST);
		}
			return currentUser;
		} else {
			
			if(userByNewUsername != null) {
				throw new UsernameExistException(USERNAME_ALREADY_EXIST +userByNewUsername);
				
			}
			if(userByNewEmail != null) {
				throw new EmailExistException(EMAIL_ALREADY_EXIST + currentUsername + userByNewEmail);
				
			}
		return null;
		}
		
	}

	// Retourne la liste de tous les utilisateurs
	@Override
	public List<User> getUsers() {
		
		return userRepository.findAll();
	}

	// Trouve un utilisateur par son nom d'utilisateur
	@Override
	public User findUserByUsername(String username) {
		
		return userRepository.findUserByUsername(username);
	}

	// Trouve un utilisateur par son email
	@Override
	public User findUserByEmail(String email) {
		
		return userRepository.findUserByEmail(email);
	}

	// Ajoute un nouvel utilisateur avec les informations fournies
    // Le rôle est fixé à USER, sans possibilité d'être admin
	@Override
	public User addNewUser(String prenom, String nom, String username, String password, String email,
			String role, boolean parseBoolean, boolean parseBoolean2) {
		
		User user = new User();
		String encodedPassword = encodePassword(password);
		user.setPrenom(prenom);
		user.setNom(nom);
		user.setUsername(username);
		user.setEmail(email);
		user.setRole(Role.ROLE_USER);  //User is not allow to be an admin in register
		user.setAuthorities(Role.ROLE_USER.getAuthorities());
		user.setPassword(encodedPassword);
		
		userRepository.save(user);
		
		LOGGER.info("Votre mode de passe "+ password);
		
		emailService.sendConfirmRegister(email, prenom, password);
		
		return user;
	}
	
	@Override
	public User updateUser(long idUser, String prenom, String nom, String username, String email, String role) {
		User user = userRepository.findById(idUser).get();
		user.setPrenom(prenom);
		user.setNom(nom);
		user.setUsername(username);
		user.setEmail(email);
		
		// Convertir la chaîne en Enum
	    user.setRole(Role.valueOf(role.toUpperCase()));
		
		return userRepository.save(user);
	}
	
	// Recherche un utilisateur par son ID
	@Override
    public User getUserById(String userId) {
        return userRepository.findById(userId);        
    }

	// Supprime un utilisateur par son identifiant
	@Override
	public void deleteUser(long id) {
		userRepository.deleteById(id);
		
	}

}
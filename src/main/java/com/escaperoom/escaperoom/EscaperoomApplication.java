package com.escaperoom.escaperoom;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.escaperoom.escaperoom.entity.Role;
import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.repository.IUserRepository;

@SpringBootApplication
public class EscaperoomApplication implements CommandLineRunner{
	
	@Autowired
	IUserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(EscaperoomApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		User adminAccount = userRepository.findByRole(Role.ROLE_ADMIN);
		
		if(adminAccount == null) {
			
			User user = new User();
			
			user.setEmail("admin@mail.com");
			user.setPrenom("admin");
			user.setUsername("admin");
			user.setNom("admin@mail.com");
			user.setRole(Role.ROLE_ADMIN);
			user.setPassword(new BCryptPasswordEncoder().encode("admin"));
			
			userRepository.save(user);
		}
		
	}

	@Bean
	public CorsFilter corsFilter() {
	    // Création d'une source de configuration basée sur les URL
	    UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
	    
	    // Nouvelle configuration CORS
	    CorsConfiguration corsConfiguration = new CorsConfiguration();
	    corsConfiguration.setAllowCredentials(true); // Autorise l'envoi des cookies ou headers type Authorization

	    // Autorise uniquement les requêtes venant de l'application Angular
	    corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200")); // Port Angular : 4200

	    // Spécifie les en-têtes autorisés dans les requêtes entrantes
	    corsConfiguration.setAllowedHeaders(Arrays.asList(
	            "Origin",
	            "Access-Control-Allow-Origin",
	            "Content-Type",
	            "Accept",
	            "Jwt-Token",
	            "Authorization",
	            "X-Requested-With",
	            "Access-Control-Request-Method",
	            "Access-Control-Request-Headers"
	    ));

	    // Déclare les en-têtes qui peuvent être exposés dans la réponse (lisibles côté front)
	    corsConfiguration.setExposedHeaders(Arrays.asList(
	            "Origin",
	            "Content-Type",
	            "Accept",
	            "Jwt-Token",
	            "Authorization",
	            "Access-Control-Allow-Origin",
	            "Access-Control-Allow-Credentials"
	    ));

	    // Méthodes HTTP autorisées pour les requêtes CORS
	    corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

	    // Applique cette configuration à toutes les routes de l’application
	    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

	    // Crée et retourne le filtre CORS avec la configuration définie
	    return new CorsFilter(urlBasedCorsConfigurationSource);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
	    // Déclare un bean pour encoder les mots de passe avec BCrypt (sécurisé et standard pour le hash)
	    return new BCryptPasswordEncoder();
	}

}

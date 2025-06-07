package com.escaperoom.escaperoom.security;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.repository.IUserRepository;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final LoginAttemptService loginAttemptService;
    private final IUserRepository userRepository; // Injection de repo pour débloquer en BDD

    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService, IUserRepository userRepository) {
        this.loginAttemptService = loginAttemptService;
        this.userRepository = userRepository;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            // Reset compteur de tentatives
            loginAttemptService.loginSucceeded(username);

            // Débloquer utilisateur en base si bloqué
            User user = userRepository.findUserByUsername(username);
            if (user != null && !user.isNotLocked()) {
                user.setNotLocked(true);
                userRepository.save(user);
            }

            System.out.println("Connexion réussie pour : " + username);
        }
    }
}

package com.escaperoom.escaperoom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.AntPathMatcher;

import com.escaperoom.escaperoom.constant.filter.JwtAccessDeniedHandler;
import com.escaperoom.escaperoom.constant.filter.JwtAuthenticationEntryPoint;
import com.escaperoom.escaperoom.constant.filter.JwtAuthorizationFilter;
import com.escaperoom.escaperoom.entity.Role;
import com.escaperoom.escaperoom.service.UserService;

import jakarta.security.auth.message.callback.PrivateKeyCallback.Request;
import lombok.RequiredArgsConstructor;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final UserService userService;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final JwtAuthorizationFilter jwtAuthorizationFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		
		http.csrf(AbstractHttpConfigurer::disable)
		.authorizeHttpRequests(
				request -> request.requestMatchers(antMatcher("/api/auth/**")).permitAll()
				.requestMatchers(antMatcher("/api/admin")).hasAuthority(Role.ROLE_ADMIN.name())
				.requestMatchers(antMatcher("/api/user")).hasAuthority(Role.ROLE_USER.name())
				.anyRequest().authenticated()
				)
		.sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authenticationProvider(authenticationProvider()).addFilterBefore(
				jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
		
		http.exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint));
		
		http.exceptionHandling(exception -> exception.accessDeniedHandler(jwtAccessDeniedHandler));
		
		return http.build();
		
		
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userService.userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		
		return config.getAuthenticationManager();
		
	}

}

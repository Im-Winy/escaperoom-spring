package com.escaperoom.escaperoom.utility;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.escaperoom.escaperoom.entity.User;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


import static com.escaperoom.escaperoom.constant.SecurityConstant.*;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.util.Arrays.stream;

@Component
public class JWTTokenProvider {
	
	@Value("${jwt.secret}")  //Vient du fichier application.properties
	private String secret;
	
	public String generateJwtToken(User user) {
		String[] claims = getClaimsFromUser(user);
		return JWT.create().withIssuer(GET_PARKAUTO_ARRAYS).withAudience(GET_ADMINISTRATION_ARRAYS).withIssuedAt( new Date()).withSubject(user.getUsername()).withArrayClaim(AUTHORITIES, claims).withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).sign(HMAC512(secret.getBytes()));
	}

	private String[] getClaimsFromUser(User user) {
		
		List<String> authorities = new ArrayList<>();
		
		for(GrantedAuthority grantedAuthority:user.getAuthorities()){
			
			authorities.add(grantedAuthority.getAuthority());
		}
		
		return authorities.toArray(new String[0]);
	}
	
	public List<GrantedAuthority> getAuthorities(String token) {
		String[] claims = getClaimsFromToken(token);
		return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());  //:: méthode qui référence à Java 8 dans l'utilisation des Collections
		
	}

	//Get authentication when we verify the token
	public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken usernamePasswordAuthToken = new UsernamePasswordAuthenticationToken(username, null,authorities);
		usernamePasswordAuthToken.setDetails( new WebAuthenticationDetailsSource().buildDetails(request));
		return usernamePasswordAuthToken;
	}
	
	public boolean isTokenValid(String username, String token) {
		JWTVerifier verifier = getJWTVerifier();
		return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier,token);
		
	}
	
	public String getSubject(String token) {
		JWTVerifier verifier = getJWTVerifier();
		return verifier.verify(token).getSubject();
	}
	
	private boolean isTokenExpired(JWTVerifier verifier, String token) {
		
		Date expiration = verifier.verify(token).getExpiresAt();
		return expiration.before(new Date());
	}

	private String[] getClaimsFromToken(String token) {
		JWTVerifier verifier = getJWTVerifier();
		
		return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
	}

	private JWTVerifier getJWTVerifier() {
		JWTVerifier verifier;
		try {
			Algorithm algorithm = HMAC512(secret);
			verifier = JWT.require(algorithm).withIssuer(GET_PARKAUTO_ARRAYS).build();
			
		} catch (JWTVerificationException e) {
			throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
		}
		
		return verifier;
	}
}

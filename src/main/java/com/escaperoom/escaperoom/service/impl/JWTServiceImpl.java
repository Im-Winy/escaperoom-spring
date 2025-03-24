package com.escaperoom.escaperoom.service.impl;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.escaperoom.escaperoom.service.JWTService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoder;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTServiceImpl implements JWTService{
	
	public String generateToken(UserDetails userDetails) {
		return Jwts.builder().setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration( new Date(System.currentTimeMillis()+1000*60*60*24))  //Expiration pour 1 jour
				.signWith(getSigninKey(), SignatureAlgorithm.HS256)
				.compact();
				
		
	}
	
	public String generateRefreshToken(Map<String, Object> extraClaims,UserDetails userDetails ) {
		return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+604800000))  //Expiration pour 7 jours
				.signWith(getSigninKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public Key getSigninKey() {
		
		byte[] key = Decoders.BASE64.decode("413F4428472B4B6250655368566D5970337336763979244226452948404D6351"); //Mettre notre clé secrete
		return Keys.hmacShaKeyFor(key);
	}
	
	private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
		final Claims claims = extractAllClaims(token);
		return claimsResolvers.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		
		return Jwts.parserBuilder().setSigningKey(getSigninKey())
				.build().parseClaimsJws(token).getBody();
	}
	
	public String extractUserName(String token) {
		return extractClaim(token, Claims::getSubject);
		
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUserName(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
		
	}

	private boolean isTokenExpired(String token) {
		
		return extractClaim(token, Claims::getExpiration)
				.before(new Date());
	}
}

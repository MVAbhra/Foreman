package com.foreman.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.foreman.security.CustomUserDetails;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secretKey;
	
	@Value("${jwt.expiration}")
	private long jwtExpiration;
	
	
	private SecretKey getSigningKey() {
	    return Keys.hmacShaKeyFor(secretKey.getBytes());
	}
	
	
	public String generateToken(CustomUserDetails userDetails) {

	    return Jwts.builder()
	            .subject(userDetails.getUsername())
	            .issuedAt(new Date())
	            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
	            .signWith(getSigningKey())
	            .compact();
	}
	
	
	private Claims extractAllClaims(String token) {

	    return Jwts.parser()
	            .verifyWith(getSigningKey())
	            .build()
	            .parseSignedClaims(token)
	            .getPayload();
	}
	
	
	public String extractEmail(String token) {
		
	    return extractAllClaims(token).getSubject();
	}
	
	
	private boolean isTokenExpired(String token) {

	    return extractAllClaims(token)
	            .getExpiration()
	            .before(new Date());
	}
	
	
	public boolean isTokenValid(String token, UserDetails userDetails) {

	    String email = extractEmail(token);

	    return email.equals(userDetails.getUsername())
	            && isTokenExpired(token)==false;
	}
}

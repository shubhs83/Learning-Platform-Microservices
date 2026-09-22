package com.learningplatform.registrationlogin.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secretString;

	private final long EXPIRATION_MS = 86400000;

	private SecretKey getKey() {
		return Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(String email, List<String> roles, String fullName) {
		return Jwts.builder().subject(email).claim("roles", roles).claim("fullName", fullName).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS)).signWith(getKey()).compact();
	}

	public String extractEmail(String token) {
		return extractAllClaims(token).getSubject();
	}

	@SuppressWarnings("unchecked")
	public List<String> extractRoles(String token) {
		return (List<String>) extractAllClaims(token).get("roles");
	}

	public String extractFullName(String token) {
		return (String) extractAllClaims(token).get("fullName");
	}

	public boolean isTokenValid(String token) {
		try {
			extractAllClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
	}
}
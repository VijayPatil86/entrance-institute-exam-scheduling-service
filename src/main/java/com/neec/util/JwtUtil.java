package com.neec.util;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {
	@Value("${jwt.secret.text}")
	private String secretKeyText;

	private SecretKey secretKey;

	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(secretKeyText.getBytes());
	}

	public Claims getJwtPayload(String jwtToken) {
		return Jwts.parser().verifyWith(secretKey).build()
				.parseSignedClaims(jwtToken)
				.getPayload();
	}
}

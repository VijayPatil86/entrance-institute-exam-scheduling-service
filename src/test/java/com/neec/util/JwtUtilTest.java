package com.neec.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtilTest {
	private JwtUtil jwtUtil;
	private SecretKey testSecretKey;

	@BeforeEach
	void setup() {
		jwtUtil = new JwtUtil();
		String secretText = "India is my country. I love my country.";
		ReflectionTestUtils.setField(jwtUtil,
				"secretKeyText",
				secretText,
				String.class);
		jwtUtil.init();
		this.testSecretKey = Keys.hmacShaKeyFor(secretText.getBytes());
	}

	private String generateTestToken(String subject, Date dateIssued, Date dateExpired) {
		return Jwts.builder()
				.subject(subject)
				.claim("roles", List.of("ADMIN"))
				.issuedAt(dateIssued)
				.expiration(dateExpired)
				.signWith(testSecretKey)
				.compact();
	}

	@Test
	void test_getJwtPayload_InvalidToken() {
		String invalidToken = "invalid-token";
		assertThrows(JwtException.class,
				() -> jwtUtil.getJwtPayload(invalidToken));
	}

	@Test
	void test_getJwtPayload_ExpiredToken() {
		Instant now = Instant.now();
		Date tokenIssued = Date.from(now.minus(10, ChronoUnit.HOURS));
		Date tokenExpires = Date.from(now.minus(3, ChronoUnit.HOURS));
		String validExpiredJwtToken = generateTestToken("101", tokenIssued, tokenExpires);
		assertThrows(ExpiredJwtException.class,
				() -> jwtUtil.getJwtPayload(validExpiredJwtToken));
	}

	@Test
	void test_getJwtPayload_LiveToken() {
		Instant now  = Instant.now();
		Date tokenIssued = Date.from(now);
		Date tokenExpires = Date.from(now.plus(2, ChronoUnit.HOURS));
		String liveToken = generateTestToken("101", tokenIssued, tokenExpires);
		Claims payload = jwtUtil.getJwtPayload(liveToken);
		assertNotNull(payload);
		assertEquals("101", payload.getSubject());
	}
}

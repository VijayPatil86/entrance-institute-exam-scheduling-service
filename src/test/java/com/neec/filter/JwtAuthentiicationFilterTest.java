package com.neec.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neec.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
public class JwtAuthentiicationFilterTest {
	@Mock
	private JwtUtil mockJwtUtil;
	@Mock
	private MockHttpServletRequest mockHttpServletRequest;
	private MockHttpServletResponse mockHttpServletResponse;
	@Mock
	private MockFilterChain mockFilterChain;
	@InjectMocks
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setup() throws UnsupportedEncodingException {
		this.objectMapper = new ObjectMapper();
		this.mockHttpServletResponse = new MockHttpServletResponse();
		//this.mockFilterChain = new MockFilterChain();
		ReflectionTestUtils.setField(jwtAuthenticationFilter, "objectMapper", new ObjectMapper());
	}

	@Test
	void test_doFilterInternal_Missing_AuthorizationHeader() throws IOException, ServletException {
		when(mockHttpServletRequest.getHeader(anyString())).thenReturn(null);
		jwtAuthenticationFilter.doFilterInternal(mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);
		verify(mockHttpServletRequest).getHeader(anyString());
		verify(mockFilterChain, never()).doFilter(mockHttpServletRequest, mockHttpServletResponse);
		assertEquals(MockHttpServletResponse.SC_UNAUTHORIZED, mockHttpServletResponse.getStatus());
		JsonNode jsonNode = toJsonNode(mockHttpServletResponse.getContentAsString());
		assertTrue(jsonNode.get("error").asText().equals("missing or invalid Authorization header"));
	}

	@Test
	void test_doFilterInternal_Token_NotStartsWith_Bearer() throws IOException, ServletException {
		when(mockHttpServletRequest.getHeader(anyString())).thenReturn("token-not-starting-with-Bearer ");
		jwtAuthenticationFilter.doFilterInternal(mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);
		verify(mockHttpServletRequest).getHeader(anyString());
		verify(mockFilterChain, never()).doFilter(mockHttpServletRequest, mockHttpServletResponse);
		assertEquals(MockHttpServletResponse.SC_UNAUTHORIZED, mockHttpServletResponse.getStatus());
		JsonNode jsonNode = toJsonNode(mockHttpServletResponse.getContentAsString());
		assertTrue(jsonNode.get("error").asText().equals("missing or invalid Authorization header"));
	}

	@Test
	void test_doFilterInternal_Bearer_Token_Invalid() throws IOException, ServletException {
		when(mockHttpServletRequest.getHeader(anyString())).thenReturn("Bearer invalid-token");
		when(mockJwtUtil.getJwtPayload(anyString())).thenThrow(new MalformedJwtException("invalid token"));
		jwtAuthenticationFilter.doFilterInternal(mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);
		verify(mockHttpServletRequest).getHeader(anyString());
		verify(mockJwtUtil).getJwtPayload(anyString());
		verify(mockFilterChain, never()).doFilter(mockHttpServletRequest, mockHttpServletResponse);
		assertEquals(MockHttpServletResponse.SC_UNAUTHORIZED, mockHttpServletResponse.getStatus());
		JsonNode jsonNode = toJsonNode(mockHttpServletResponse.getContentAsString());
		assertTrue(jsonNode.get("error").asText().equals("invalid token"));
	}

	@Test
	void test_doFilterInternal_Bearer_Token_Expired() throws IOException, ServletException {
		String validExpiredJwtToken =
				"Bearer eyJhbGciOiJIUzI1NiJ9."
				+ "eyJzdWIiOiJqb2huLnJheUBjb2xsZWdlb2Zjb21tZXJjZS5lZHUiLCJpYXQiOjE3NTMwNjExODQsImV4cCI6MTc1MzA2MTc4NCwicm9sZSI6IkFETUlOIiwidXNlcklkIjo2fQ."
				+ "Qw5VBitXSipQj6K8XAvV9PXk95N54dA4lkc8kHxPslY";
		when(mockHttpServletRequest.getHeader(anyString())).thenReturn(validExpiredJwtToken);
		when(mockJwtUtil.getJwtPayload(anyString())).thenThrow(new ExpiredJwtException(null, null, "token expired"));
		jwtAuthenticationFilter.doFilterInternal(mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);
		verify(mockHttpServletRequest).getHeader(anyString());
		verify(mockJwtUtil).getJwtPayload(anyString());
		verify(mockFilterChain, never()).doFilter(mockHttpServletRequest, mockHttpServletResponse);
		assertEquals(MockHttpServletResponse.SC_UNAUTHORIZED, mockHttpServletResponse.getStatus());
		JsonNode jsonNode = toJsonNode(mockHttpServletResponse.getContentAsString());
		assertTrue(jsonNode.get("error").asText().equals("token expired"));
	}

	@Test
	void test_doFilterInternal_Bearer_Token_Invalid_Signature() throws IOException, ServletException {
		String validInavlidSignatureJwtToken =
				"Bearer eyJhbGciOiJIUzI1NiJ9."
				+ "TTTzdWIiOiJqb2huLnJheUBjb2xsZWdlb2Zjb21tZXJjZS5lZHUiLCJpYXQiOjE3NTMwNjExODQsImV4cCI6MTc1MzA2MTc4NCwicm9sZSI6IkFETUlOIiwidXNlcklkIjo2fQ."
				+ "Qw5VBitXSipQj6K8XAvV9PXk95N54dA4lkc8kHxPAAA";
		when(mockHttpServletRequest.getHeader(anyString())).thenReturn(validInavlidSignatureJwtToken);
		when(mockJwtUtil.getJwtPayload(anyString())).thenThrow(new SignatureException("invalid token"));
		jwtAuthenticationFilter.doFilterInternal(mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);
		verify(mockHttpServletRequest).getHeader(anyString());
		verify(mockJwtUtil).getJwtPayload(anyString());
		verify(mockFilterChain, never()).doFilter(mockHttpServletRequest, mockHttpServletResponse);
		assertEquals(MockHttpServletResponse.SC_UNAUTHORIZED, mockHttpServletResponse.getStatus());
		JsonNode jsonNode = toJsonNode(mockHttpServletResponse.getContentAsString());
		assertTrue(jsonNode.get("error").asText().equals("invalid token"));
	}

	@Test
	void test_doFilterInternal_Bearer_Token_Missing_Role() throws IOException, ServletException {
		String validLiveJwtToken =
				"Bearer eyJhbGciOiJIUzI1NiJ9."
				+ "eyvzdWIiOiJqb2huLnJheUBjb2xsZWdlb2Zjb21tZXJjZS5lZHUiLCJpYXQiOjE3NTMwNjExODQsImV4cCI6MTc1MzA2MTc4NCwicm9sZSI6IkFETUlOIiwidXNlcklkIjo2fQ."
				+ "Qw5VBitXSipQj6K8XAvV9PXk95N54dA4lkc8kHxsweR";
		when(mockHttpServletRequest.getHeader(anyString())).thenReturn(validLiveJwtToken);
		Claims claims = Jwts.claims().build();
		when(mockJwtUtil.getJwtPayload(anyString())).thenReturn(claims);
		jwtAuthenticationFilter.doFilterInternal(mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);
		verify(mockHttpServletRequest).getHeader(anyString());
		verify(mockJwtUtil).getJwtPayload(anyString());
		verify(mockFilterChain, never()).doFilter(mockHttpServletRequest, mockHttpServletResponse);
		assertEquals(MockHttpServletResponse.SC_UNAUTHORIZED, mockHttpServletResponse.getStatus());
		JsonNode jsonNode = toJsonNode(mockHttpServletResponse.getContentAsString());
		assertTrue(jsonNode.get("error").asText().equals("missing Roles in token"));
	}

	@Test
	void test_doFilterInternal_Bearer_Token_Live() throws IOException, ServletException {
		String validLiveJwtToken =
				"Bearer eyJhbGciOiJIUzI1NiJ9."
				+ "eyvzdWIiOiJqb2huLnJheUBjb2xsZWdlb2Zjb21tZXJjZS5lZHUiLCJpYXQiOjE3NTMwNjExODQsImV4cCI6MTc1MzA2MTc4NCwicm9sZSI6IkFETUlOIiwidXNlcklkIjo2fQ."
				+ "Qw5VBitXSipQj6K8XAvV9PXk95N54dA4lkc8kHxsweR";
		when(mockHttpServletRequest.getHeader(anyString())).thenReturn(validLiveJwtToken);
		Claims claims = Jwts.claims()
				.add(Map.of("roles", List.of("ADMIN")))
				.subject("102")
				.add("emailAddress", "admin@neec.com")
				.build();
		when(mockJwtUtil.getJwtPayload(anyString())).thenReturn(claims);
		jwtAuthenticationFilter.doFilterInternal(mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);
		verify(mockHttpServletRequest).getHeader(anyString());
		verify(mockJwtUtil).getJwtPayload(anyString());
		verify(mockFilterChain, times(1)).doFilter(mockHttpServletRequest, mockHttpServletResponse);
		assertEquals(MockHttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
	}

	private JsonNode toJsonNode(String jsonString) throws JsonMappingException, JsonProcessingException {
		JsonNode jsonNode = objectMapper.readTree(jsonString);
		return jsonNode;
	}
}

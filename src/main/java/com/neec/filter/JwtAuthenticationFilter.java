package com.neec.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neec.dto.CustomPrincipal;
import com.neec.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private JwtUtil jwtUtil;
	private ObjectMapper objectMapper;

	public JwtAuthenticationFilter(JwtUtil jwtUtil, ObjectMapper objectMapper) {
		this.jwtUtil = jwtUtil;
		this.objectMapper = objectMapper;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "missing or invalid Authorization header");
			return;
		}
		String jwtToken = authHeader.substring(7);
		try {
			Claims payload = jwtUtil.getJwtPayload(jwtToken);
			if(payload.get("roles") == null) {
				sendErrorResponse(response, HttpServletResponse.SC_FOUND, "missing Roles in token");
				return;
			}
			CustomPrincipal customPrincipal = CustomPrincipal.builder()
					.subject(payload.getSubject())
					.emailAddress(payload.get("emailAddress", String.class))
					.build();
			List<GrantedAuthority> lstGrantedAuthorities = ((List<?>) payload.get("roles", List.class)).stream()
					.map(role -> (GrantedAuthority)new SimpleGrantedAuthority(role.toString()))
					.toList();
			UsernamePasswordAuthenticationToken authenticationToken =
					UsernamePasswordAuthenticationToken.authenticated(customPrincipal, null, lstGrantedAuthorities);
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			filterChain.doFilter(request, response);
		} catch(ExpiredJwtException ex){
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "token expired");
			return;
		} catch(SignatureException ex) {
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "invalid token");
			return;
		} catch(MalformedJwtException ex) {
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "invalid token");
			return;
		} catch(JwtException ex) {
			sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "invalid token");
			return;
		}
	}

	private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
		response.setStatus(status);
		response.setContentType(MediaType.APPLICATION_JSON.toString());
		response.getWriter().write(getErrorMessageJsonString(message));
	}

	private String getErrorMessageJsonString(String message) {
		ObjectNode jsonNodeError = objectMapper.createObjectNode();
		jsonNodeError.put("error", message);
		return jsonNodeError.toString();
	}
}

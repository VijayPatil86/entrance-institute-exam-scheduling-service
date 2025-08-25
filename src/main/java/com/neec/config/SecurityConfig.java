package com.neec.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.neec.filter.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	final static private String[] END_POINTS_ADMIN = {
		"/api/v1/admin/**"
	};

	final static private String[] END_POINTS_APPLICANT = {
		"/api/v1/centers/**"
	};

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.cors(Customizer.withDefaults())
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth.requestMatchers(END_POINTS_ADMIN).hasAuthority("ADMIN")
					.requestMatchers(END_POINTS_APPLICANT).hasAuthority("APPLICANT")
					.anyRequest().authenticated())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}
}

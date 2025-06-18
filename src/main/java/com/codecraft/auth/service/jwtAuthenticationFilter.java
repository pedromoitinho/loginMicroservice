package com.codecraft.auth.service;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class jwtAuthenticationFilter extends OncePerRequestFilter {
	@Autowired
	private jwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;

		System.out.println("🔐 JWT Filter - Processing request to: " + request.getRequestURI());

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			System.out.println("🎫 JWT Token found: " + token.substring(0, Math.min(20, token.length())) + "...");
			try {
				username = jwtUtil.getUsernameFromToken(token);
				System.out.println("👤 Username extracted from token: '" + username + "'");
			} catch (Exception e) {
				System.out.println("❌ JWT token parsing error: " + e.getMessage());
				logger.error("JWT token parsing error: " + e.getMessage());
			}
		} else {
			System.out.println("🚫 No Authorization header or invalid format");
		}
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			boolean isValid = jwtUtil.validateToken(token);
			boolean isExpired = jwtUtil.isTokenExpired(token);
			System.out.println("🔍 Token validation: valid=" + isValid + ", expired=" + isExpired);

			if (isValid && !isExpired) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null,
						new ArrayList<>());
				SecurityContextHolder.getContext().setAuthentication(authToken);
				System.out.println("✅ Authentication set for user: " + username);
			} else {
				System.out.println("❌ Token validation failed or expired");
			}
		}
		filterChain.doFilter(request, response);
	}
}

package com.codecraft.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codecraft.auth.entity.User;
import com.codecraft.auth.repository.UserRepository;
import com.codecraft.auth.dto.AuthResponse;
import com.codecraft.auth.dto.ErrorResponse;

@Service
public class AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private jwtUtil jwtUtil;

	public Object register(String username, String password) {
		// Validate input
		if (username == null || username.trim().isEmpty()) {
			return new ErrorResponse("VALIDATION_ERROR", "Escreva seu Nome de Usuário!");
		}
		if (password == null || password.length() < 6) {
			return new ErrorResponse("VALIDATION_ERROR", "A Senha Precisa ter no Minimo 6 Caractéres");
		}

		// Check if user exists
		if (userRepository.findByUsername(username) != null) {
			return new ErrorResponse("USER_EXISTS", "Esse Nome já Existe");
		}

		// Create new user
		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		userRepository.save(user);

		// Generate token
		String token = jwtUtil.generateToken(username);
		return new AuthResponse(token, username, "Usuário Registrado com Sucesso");
	}

	public Object login(String username, String password) {
		// Validate input
		if (username == null || username.trim().isEmpty()) {
			return new ErrorResponse("VALIDATION_ERROR", "Escreva seu Nome de Usuário!");
		}
		if (password == null || password.trim().isEmpty()) {
			return new ErrorResponse("VALIDATION_ERROR", "Escreva uma Senha!");
		}

		// Find user
		User user = userRepository.findByUsername(username);
		if (user == null) {
			return new ErrorResponse("AUTH_FAILED", "Nome ou Senha Inválidos");
		}

		// Verify password
		if (!passwordEncoder.matches(password, user.getPassword())) {
			return new ErrorResponse("AUTH_FAILED", "Nome ou Senha Inválidos");
		}

		// Generate token
		String token = jwtUtil.generateToken(username);
		return new AuthResponse(token, username, "Login bem Sucedido");
	}

	@Transactional
	public Object deleteUser(String username) {
		// Validate input
		if (username == null || username.trim().isEmpty()) {
			return new ErrorResponse("VALIDATION_ERROR", "Nome de usuário não pode estar vazio");
		}

		// Check if user exists
		if (!userRepository.existsByUsername(username)) {
			return new ErrorResponse("USER_NOT_FOUND", "Usuário não encontrado");
		}

		try {
			// Delete the user
			userRepository.deleteByUsername(username);
			return new AuthResponse(null, username, "Usuário deletado com sucesso");
		} catch (Exception e) {
			return new ErrorResponse("DELETE_ERROR", "Erro ao deletar usuário: " + e.getMessage());
		}
	}
}

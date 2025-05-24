package com.codecraft.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codecraft.auth.entity.User;
import com.codecraft.auth.repository.UserRepository;

@Service
public class AuthService {
	@Autowired
	private UserRepository userRepository;

	public String register(String username, String password) {
		if (userRepository.findByUsername(username) != null) {
			return "Esse nome já existe";
		}
		if (userRepository.findAll().stream().anyMatch(user -> user.getPassword().equals(password))) {
			return "Essa senha já existe";
		}
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		userRepository.save(user);
		return "Usuário registrado com sucesso";
	}

	public String login(String username, String password) {
		User user = userRepository.findByUsername(username);
		if (user == null || !password.equals(user.getPassword())) {
			return "Login falhou";
		}
		return "Login bem sucedido";
	}
}

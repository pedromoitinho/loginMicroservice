package com.codecraft.auth.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codecraft.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private AuthService authService;

	@PostMapping("/register")
	public String register(@RequestParam String username, @RequestParam String password) {
		return authService.register(username, password);
	}

	@PostMapping("/login")
	public String login(@RequestParam String username, @RequestParam String password) {
		return authService.login(username, password);
	}

}

package com.codecraft.auth.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codecraft.auth.dto.AuthResponse;
import com.codecraft.auth.dto.ErrorResponse;
import com.codecraft.auth.dto.UserDTO;
import com.codecraft.auth.entity.User;
import com.codecraft.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestParam String username, @RequestParam String password,
			@RequestParam(required = false) String userGroup) {
		Object result = authService.register(username, password, userGroup);

		if (result instanceof AuthResponse) {
			return ResponseEntity.ok(result);
		} else {
			return ResponseEntity.badRequest().body(result);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
		Object result = authService.login(username, password);

		if (result instanceof AuthResponse) {
			return ResponseEntity.ok(result);
		} else {
			return ResponseEntity.badRequest().body(result);
		}
	}

	@GetMapping("/me")
	public ResponseEntity<?> getCurrentUser(Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			User user = authService.getUserByUsername(authentication.getName());
			if (user != null) {
				return ResponseEntity.ok(new UserDTO(user.getId(), user.getUsername(), user.getUserGroup()));
			}
			return ResponseEntity.ok(new UserDTO(null, authentication.getName(), null));
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse("UNAUTHORIZED", "No valid token provided"));
	}

	@GetMapping("/protected")
	public ResponseEntity<?> protectedEndpoint(Authentication authentication) {
		return ResponseEntity.ok().body("This is a protected endpoint. Welcome, " + authentication.getName() + "!");
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteUser(@RequestParam String username) {
		Object result = authService.deleteUser(username);

		if (result instanceof AuthResponse) {
			return ResponseEntity.ok(result);
		} else {
			ErrorResponse error = (ErrorResponse) result;
			if (error != null && error.getError().equals("USER_NOT_FOUND")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
			} else {
				return ResponseEntity.badRequest().body(result);
			}
		}
	}

	@GetMapping("/users")
	public ResponseEntity<?> getAllUsers() {
		Object result = authService.getAllUsers();
		return ResponseEntity.ok(result);
	}
}

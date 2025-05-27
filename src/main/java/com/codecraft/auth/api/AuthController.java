package com.codecraft.auth.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.codecraft.auth.service.AuthService;
import com.codecraft.auth.dto.AuthResponse;
import com.codecraft.auth.dto.ErrorResponse;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestParam String username, @RequestParam String password) {
		Object result = authService.register(username, password);

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
			return ResponseEntity.ok().body(new AuthResponse(null, authentication.getName(), "User authenticated"));
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
			if (error.getError().equals("USER_NOT_FOUND")) {
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

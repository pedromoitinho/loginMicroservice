package com.codecraft.auth.dto;

public class AuthResponse {
	private String token;
	private String type = "Bearer";
	private String username;
	private String message;

	public AuthResponse(String token, String username, String message) {
		this.token = token;
		this.username = username;
		this.message = message;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

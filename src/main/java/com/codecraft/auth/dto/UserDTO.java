package com.codecraft.auth.dto;

public class UserDTO {
	private Long id;
	private String username;
	private String userGroup;

	// Construtor vazio necessário para serialização
	public UserDTO() {
	}

	// Construtor para converter de User para UserDTO
	public UserDTO(Long id, String username) {
		this.id = id;
		this.username = username;
	}

	public UserDTO(Long id, String username, String userGroup) {
		this.id = id;
		this.username = username;
		this.userGroup = userGroup;
	}

	// Getters e setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}
}

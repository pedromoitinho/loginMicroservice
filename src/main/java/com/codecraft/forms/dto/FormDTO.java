package com.codecraft.forms.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FormDTO {
	private Long id;
	private String title;
	private String description;
	private String createdBy;
	private LocalDateTime createdAt;
	private Boolean isActive;
	private List<QuestionDTO> questions;
	private String allowedGroups; // comma-separated group names

	public FormDTO() {
	}

	public FormDTO(Long id, String title, String description, String createdBy, LocalDateTime createdAt,
			Boolean isActive) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.createdBy = createdBy;
		this.createdAt = createdAt;
		this.isActive = isActive;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<QuestionDTO> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionDTO> questions) {
		this.questions = questions;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getAllowedGroups() {
		return allowedGroups;
	}

	public void setAllowedGroups(String allowedGroups) {
		this.allowedGroups = allowedGroups;
	}
}

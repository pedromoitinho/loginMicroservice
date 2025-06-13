package com.codecraft.forms.dto;

import java.util.List;

public class UpdateFormDTO {
	private String title;
	private String description;
	private Boolean isActive;
	private List<CreateQuestionDTO> questions;
	private String allowedGroups;

	public UpdateFormDTO() {
	}

	public UpdateFormDTO(String title, String description, Boolean isActive, List<CreateQuestionDTO> questions,
			String allowedGroups) {
		this.title = title;
		this.description = description;
		this.isActive = isActive;
		this.questions = questions;
		this.allowedGroups = allowedGroups;
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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public List<CreateQuestionDTO> getQuestions() {
		return questions;
	}

	public void setQuestions(List<CreateQuestionDTO> questions) {
		this.questions = questions;
	}

	public String getAllowedGroups() {
		return allowedGroups;
	}

	public void setAllowedGroups(String allowedGroups) {
		this.allowedGroups = allowedGroups;
	}
}

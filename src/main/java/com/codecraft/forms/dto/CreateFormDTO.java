package com.codecraft.forms.dto;

import java.util.List;

public class CreateFormDTO {
	private String title;
	private String description;
	private List<CreateQuestionDTO> questions;
	private String allowedGroups; // comma-separated group names

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

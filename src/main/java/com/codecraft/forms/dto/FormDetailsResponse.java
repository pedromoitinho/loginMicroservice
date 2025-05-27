package com.codecraft.forms.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FormDetailsResponse {
	private Long id;
	private String title;
	private String description;
	private LocalDateTime createdAt;
	private Boolean isActive;
	private List<QuestionResponse> questions;

	public static class QuestionResponse {
		private Long id;
		private String questionText;
		private String type;
		private Integer order;
		private Boolean isRequired;
		private String options;

		// Constructors
		public QuestionResponse() {
		}

		public QuestionResponse(Long id, String questionText, String type, Integer order, Boolean isRequired,
				String options) {
			this.id = id;
			this.questionText = questionText;
			this.type = type;
			this.order = order;
			this.isRequired = isRequired;
			this.options = options;
		}

		// Getters and Setters
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getQuestionText() {
			return questionText;
		}

		public void setQuestionText(String questionText) {
			this.questionText = questionText;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Integer getOrder() {
			return order;
		}

		public void setOrder(Integer order) {
			this.order = order;
		}

		public Boolean getIsRequired() {
			return isRequired;
		}

		public void setIsRequired(Boolean isRequired) {
			this.isRequired = isRequired;
		}

		public String getOptions() {
			return options;
		}

		public void setOptions(String options) {
			this.options = options;
		}
	}

	// Constructors
	public FormDetailsResponse() {
	}

	public FormDetailsResponse(Long id, String title, String description, LocalDateTime createdAt, Boolean isActive,
			List<QuestionResponse> questions) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.createdAt = createdAt;
		this.isActive = isActive;
		this.questions = questions;
	}

	// Getters and Setters
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public List<QuestionResponse> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionResponse> questions) {
		this.questions = questions;
	}
}

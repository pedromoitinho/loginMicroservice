package com.codecraft.forms.dto;

import java.util.List;

public class QuestionDTO {
	private Long id;
	private String questionText;
	private String type;
	private Integer questionOrder;
	private List<QuestionOptionDTO> options;
	private String questionType; // Added questionType field to match entity

	public QuestionDTO() {
	}

	public QuestionDTO(Long id, String questionText, String type, Integer questionOrder) {
		this.id = id;
		this.questionText = questionText;
		this.type = type;
		this.questionType = type; // Set both for consistency
		this.questionOrder = questionOrder;
	}

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
		this.questionType = type; // Update both for consistency
	}

	public String getQuestionType() {
		return questionType != null ? questionType : type;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
		// Only update type if it's not already set
		if (this.type == null) {
			this.type = questionType;
		}
	}

	public Integer getQuestionOrder() {
		return questionOrder;
	}

	public void setQuestionOrder(Integer questionOrder) {
		this.questionOrder = questionOrder;
	}

	public List<QuestionOptionDTO> getOptions() {
		return options;
	}

	public void setOptions(List<QuestionOptionDTO> options) {
		this.options = options;
	}
}

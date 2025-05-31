package com.codecraft.forms.dto;

import java.util.List;

import com.codecraft.forms.type.QuestionType;

public class CreateQuestionDTO {
	private String questionText;
	private QuestionType type;
	private Boolean required;
	private Integer questionOrder;
	private List<String> options;

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public QuestionType getType() {
		return type;
	}

	public void setType(QuestionType type) {
		this.type = type;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Integer getQuestionOrder() {
		return questionOrder;
	}

	public void setQuestionOrder(Integer questionOrder) {
		this.questionOrder = questionOrder;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}
}

package com.codecraft.forms.dto;

public class QuestionResponseDTO {
	private Long questionId;
	private String responseText;
	private Long selectedOptionId;
	private Double responseNumber;

	public QuestionResponseDTO() {
	}

	public QuestionResponseDTO(Long questionId, String responseText, Long selectedOptionId) {
		this.questionId = questionId;
		this.responseText = responseText;
		this.selectedOptionId = selectedOptionId;
	}

	public QuestionResponseDTO(Long questionId, String responseText, Long selectedOptionId, Double responseNumber) {
		this.questionId = questionId;
		this.responseText = responseText;
		this.selectedOptionId = selectedOptionId;
		this.responseNumber = responseNumber;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}

	public Long getSelectedOptionId() {
		return selectedOptionId;
	}

	public void setSelectedOptionId(Long selectedOptionId) {
		this.selectedOptionId = selectedOptionId;
	}

	public Double getResponseNumber() {
		return responseNumber;
	}

	public void setResponseNumber(Double responseNumber) {
		this.responseNumber = responseNumber;
	}
}

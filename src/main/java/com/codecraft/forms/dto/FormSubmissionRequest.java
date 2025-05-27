package com.codecraft.forms.dto;

import java.util.List;

public class FormSubmissionRequest {
	private Long formId;
	private List<AnswerRequest> answers;

	public static class AnswerRequest {
		private Long questionId;
		private String answerText;
		private Integer answerNumber;

		// Constructors
		public AnswerRequest() {
		}

		public AnswerRequest(Long questionId, String answerText) {
			this.questionId = questionId;
			this.answerText = answerText;
		}

		public AnswerRequest(Long questionId, Integer answerNumber) {
			this.questionId = questionId;
			this.answerNumber = answerNumber;
		}

		// Getters and Setters
		public Long getQuestionId() {
			return questionId;
		}

		public void setQuestionId(Long questionId) {
			this.questionId = questionId;
		}

		public String getAnswerText() {
			return answerText;
		}

		public void setAnswerText(String answerText) {
			this.answerText = answerText;
		}

		public Integer getAnswerNumber() {
			return answerNumber;
		}

		public void setAnswerNumber(Integer answerNumber) {
			this.answerNumber = answerNumber;
		}
	}

	// Constructors
	public FormSubmissionRequest() {
	}

	public FormSubmissionRequest(Long formId, List<AnswerRequest> answers) {
		this.formId = formId;
		this.answers = answers;
	}

	// Getters and Setters
	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public List<AnswerRequest> getAnswers() {
		return answers;
	}

	public void setAnswers(List<AnswerRequest> answers) {
		this.answers = answers;
	}
}

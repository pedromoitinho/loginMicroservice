package com.codecraft.forms.dto;

import java.util.List;

public class FormStatisticsDTO {
	private Long formId;
	private String formTitle;
	private int totalResponses;
	private List<QuestionStatisticsDTO> questionsAnalytics;

	public static class QuestionStatisticsDTO {
		private Long questionId;
		private String questionText;
		private String questionType; // Keep as String for serialization
		private java.util.Map<String, Integer> answerCounts;
		private List<String> textAnswers;
		private Double averageRating;

		public Long getQuestionId() {
			return questionId;
		}

		public void setQuestionId(Long questionId) {
			this.questionId = questionId;
		}

		public String getQuestionText() {
			return questionText;
		}

		public void setQuestionText(String questionText) {
			this.questionText = questionText;
		}

		public String getQuestionType() {
			return questionType;
		}

		public void setQuestionType(String questionType) {
			this.questionType = questionType;
		}

		public java.util.Map<String, Integer> getAnswerCounts() {
			return answerCounts;
		}

		public void setAnswerCounts(java.util.Map<String, Integer> answerCounts) {
			this.answerCounts = answerCounts;
		}

		public List<String> getTextAnswers() {
			return textAnswers;
		}

		public void setTextAnswers(List<String> textAnswers) {
			this.textAnswers = textAnswers;
		}

		public Double getAverageRating() {
			return averageRating;
		}

		public void setAverageRating(Double averageRating) {
			this.averageRating = averageRating;
		}
	}

	public FormStatisticsDTO() {
	}

	public FormStatisticsDTO(Long formId, String formTitle, int totalResponses,
			List<QuestionStatisticsDTO> questionsAnalytics) {
		this.formId = formId;
		this.formTitle = formTitle;
		this.totalResponses = totalResponses;
		this.questionsAnalytics = questionsAnalytics;
	}

	// Getters and setters
	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getFormTitle() {
		return formTitle;
	}

	public void setFormTitle(String formTitle) {
		this.formTitle = formTitle;
	}

	public int getTotalResponses() {
		return totalResponses;
	}

	public void setTotalResponses(int totalResponses) {
		this.totalResponses = totalResponses;
	}

	public List<QuestionStatisticsDTO> getQuestionsAnalytics() {
		return questionsAnalytics;
	}

	public void setQuestionsAnalytics(List<QuestionStatisticsDTO> questionsAnalytics) {
		this.questionsAnalytics = questionsAnalytics;
	}
}

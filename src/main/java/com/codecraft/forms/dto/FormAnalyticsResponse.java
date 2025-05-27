package com.codecraft.forms.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class FormAnalyticsResponse {
	private Long formId;
	private String formTitle;
	private Integer totalResponses;
	private LocalDateTime firstResponse;
	private LocalDateTime lastResponse;
	private List<QuestionAnalytics> questionAnalytics;

	public static class QuestionAnalytics {
		private Long questionId;
		private String questionText;
		private String questionType;
		private Map<String, Integer> answerCounts; // Para single/multiple choice
		private Double averageRating; // Para ratings
		private Integer totalAnswers;
		private List<String> textAnswers; // Para text/textarea (limitado)

		// Constructors
		public QuestionAnalytics() {
		}

		// Getters and Setters
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

		public Map<String, Integer> getAnswerCounts() {
			return answerCounts;
		}

		public void setAnswerCounts(Map<String, Integer> answerCounts) {
			this.answerCounts = answerCounts;
		}

		public Double getAverageRating() {
			return averageRating;
		}

		public void setAverageRating(Double averageRating) {
			this.averageRating = averageRating;
		}

		public Integer getTotalAnswers() {
			return totalAnswers;
		}

		public void setTotalAnswers(Integer totalAnswers) {
			this.totalAnswers = totalAnswers;
		}

		public List<String> getTextAnswers() {
			return textAnswers;
		}

		public void setTextAnswers(List<String> textAnswers) {
			this.textAnswers = textAnswers;
		}
	}

	// Constructors
	public FormAnalyticsResponse() {
	}

	// Getters and Setters
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

	public Integer getTotalResponses() {
		return totalResponses;
	}

	public void setTotalResponses(Integer totalResponses) {
		this.totalResponses = totalResponses;
	}

	public LocalDateTime getFirstResponse() {
		return firstResponse;
	}

	public void setFirstResponse(LocalDateTime firstResponse) {
		this.firstResponse = firstResponse;
	}

	public LocalDateTime getLastResponse() {
		return lastResponse;
	}

	public void setLastResponse(LocalDateTime lastResponse) {
		this.lastResponse = lastResponse;
	}

	public List<QuestionAnalytics> getQuestionAnalytics() {
		return questionAnalytics;
	}

	public void setQuestionAnalytics(List<QuestionAnalytics> questionAnalytics) {
		this.questionAnalytics = questionAnalytics;
	}
}

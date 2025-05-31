package com.codecraft.forms.dto;

import java.util.List;
import java.util.Map;

public class QuestionStatisticsDTO {
	private Long questionId;
	private String questionText;
	private String questionType;
	private Map<String, Integer> answerCounts; // For pie chart (option/number/rating)
	private List<String> textAnswers; // For open/number/rating questions

	public QuestionStatisticsDTO() {
	}

	public QuestionStatisticsDTO(Long questionId, String questionText, String questionType,
			Map<String, Integer> answerCounts, List<String> textAnswers) {
		this.questionId = questionId;
		this.questionText = questionText;
		this.questionType = questionType;
		this.answerCounts = answerCounts;
		this.textAnswers = textAnswers;
	}

	// Getters and setters
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

	public List<String> getTextAnswers() {
		return textAnswers;
	}

	public void setTextAnswers(List<String> textAnswers) {
		this.textAnswers = textAnswers;
	}
}

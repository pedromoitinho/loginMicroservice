package com.codecraft.forms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "form_answers")
public class FormAnswer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "form_response_id", nullable = false)
	private FormResponse formResponse;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "form_question_id", nullable = false)
	private FormQuestion formQuestion;

	@Column(columnDefinition = "TEXT")
	private String answerText;

	@Column
	private Integer answerNumber;

	// Constructors
	public FormAnswer() {
	}

	public FormAnswer(FormResponse formResponse, FormQuestion formQuestion, String answerText) {
		this.formResponse = formResponse;
		this.formQuestion = formQuestion;
		this.answerText = answerText;
	}

	public FormAnswer(FormResponse formResponse, FormQuestion formQuestion, Integer answerNumber) {
		this.formResponse = formResponse;
		this.formQuestion = formQuestion;
		this.answerNumber = answerNumber;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FormResponse getFormResponse() {
		return formResponse;
	}

	public void setFormResponse(FormResponse formResponse) {
		this.formResponse = formResponse;
	}

	public FormQuestion getFormQuestion() {
		return formQuestion;
	}

	public void setFormQuestion(FormQuestion formQuestion) {
		this.formQuestion = formQuestion;
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

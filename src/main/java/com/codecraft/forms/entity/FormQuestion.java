package com.codecraft.forms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "form_questions")
public class FormQuestion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "form_id", nullable = false)
	private Form form;

	@Column(nullable = false)
	private String questionText;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private QuestionType type;

	@Column(name = "question_order", nullable = false)
	private Integer order;

	@Column(name = "is_required", nullable = false)
	private Boolean isRequired = false;

	@Column(columnDefinition = "TEXT")
	private String options; // JSON string for multiple choice options

	public enum QuestionType {
		TEXT,
		EMAIL,
		NUMBER,
		PHONE,
		TEXTAREA,
		SINGLE_CHOICE,
		MULTIPLE_CHOICE,
		RATING,
		DATE
	}

	// Constructors
	public FormQuestion() {
	}

	public FormQuestion(Form form, String questionText, QuestionType type, Integer order) {
		this.form = form;
		this.questionText = questionText;
		this.type = type;
		this.order = order;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

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

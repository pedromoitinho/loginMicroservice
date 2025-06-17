package com.codecraft.forms.entity;

import java.util.ArrayList;
import java.util.List;

import com.codecraft.forms.type.QuestionType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "questions")
public class Question {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "text", nullable = false, columnDefinition = "varchar(255) default 'placeholder'")
	private String questionText;

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private QuestionType type;

	@Column
	private Integer questionOrder;

	@ManyToOne
	@JoinColumn(name = "form_id")
	private Form form;

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<QuestionOption> options = new ArrayList<>();

	@Column(name = "question_type")
	private String questionType;

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

	public QuestionType getType() {
		return type;
	}

	public void setType(QuestionType type) {
		this.type = type;
		// Only update questionType string if type is not null and there's no infinite
		// loop
		if (type != null) {
			this.questionType = type.name();
		}
	}

	public Integer getQuestionOrder() {
		return questionOrder;
	}

	public void setQuestionOrder(Integer questionOrder) {
		this.questionOrder = questionOrder;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public List<QuestionOption> getOptions() {
		return options;
	}

	public void setOptions(List<QuestionOption> options) {
		this.options = options;
	}

	public String getQuestionType() {
		// If questionType is null but type enum is set, return the enum name
		if (questionType == null && type != null) {
			return type.name();
		}
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
		// Only update type enum if questionType is not null and to avoid infinite loop
		if (questionType != null) {
			try {
				// This prevents circular updates by checking if type already matches
				QuestionType newType = QuestionType.valueOf(questionType);
				if (this.type != newType) {
					this.type = newType;
				}
			} catch (IllegalArgumentException e) {
				// Handle case where string doesn't match an enum value
				this.type = QuestionType.TEXT; // Default to TEXT
			}
		}
	}
}

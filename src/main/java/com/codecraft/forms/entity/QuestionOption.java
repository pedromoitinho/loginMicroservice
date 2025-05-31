package com.codecraft.forms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "question_option") // Ensure this matches the actual table name in the database
public class QuestionOption {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String text;

	@ManyToOne
	@JoinColumn(name = "question_id")
	private Question question;

	@Column(name = "option_order")
	private Integer optionOrder;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public Integer getOptionOrder() {
		return optionOrder;
	}

	public void setOptionOrder(Integer optionOrder) {
		this.optionOrder = optionOrder;
	}
}